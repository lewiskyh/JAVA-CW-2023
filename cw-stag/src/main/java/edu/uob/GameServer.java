package edu.uob;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import com.alexmerz.graphviz.objects.Edge;



public final class GameServer {

    private static final char END_OF_TRANSMISSION = 4;

    private Location startingLocation;
    private HashMap <String, GameEntity> entities = new HashMap<>();

    private HashMap<String, Location> gameLocations = new HashMap<>();

    public static void main(String[] args) throws IOException {
        File entitiesFile = Paths.get("config" + File.separator + "basic-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
        GameServer server = new GameServer(entitiesFile, actionsFile);
        server.blockingListenOn(8888);
    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * Instanciates a new server instance, specifying a game with some configuration files
    *
    * @param entitiesFile The game configuration file containing all game entities to use in your game
    * @param actionsFile The game configuration file containing all game actions to use in your game
    */
    public GameServer(File entitiesFile, File actionsFile) {
        try{
            readEntityFile(entitiesFile);
        }catch(IOException | ParseException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Read the entities file and parse the entities.
     * @param entitiesFile The game configuration file containing all game entities to use in your game
     * @throws IOException
     * @throws ParseException
     */

    public void readEntityFile(File entitiesFile) throws IOException, ParseException {
        Parser parser = new Parser();
        FileReader reader = new FileReader(entitiesFile);
        parser.parse(reader);
        /*Get the whole document from the first <graph> tag*/
        Graph wholeDocument = parser.getGraphs().get(0);

        /*locations must be the 1st subgraph*/
        ArrayList<Graph> locations = wholeDocument.getSubgraphs().get(0).getSubgraphs();

        //Parse the location graphs and add to the location list
        for (Graph locationGraph : locations) {
            parseLocation(locationGraph);
        }

        /*Path subgraphs must appear after the locations*/
        Graph pathGraph = wholeDocument.getSubgraphs().get(1);
        /*Parse the paths and store them in the corresponding location object*/
        parsePaths(pathGraph);
    }

    public void parsePaths (Graph pathGraph) {
        //Get the path and add to the corresponding location
        ArrayList<Edge> paths = pathGraph.getEdges();
        for (Edge path : paths) {
            Node fromLocation = path.getSource().getNode();
            String fromName = fromLocation.getId().getId();
            Node toLocation = path.getTarget().getNode();
            String toName = toLocation.getId().getId();

            //Get the location objects from the gameLocations hashmap
            Location from = gameLocations.get(fromName);
            Location to = gameLocations.get(toName);
            if(from != null && to != null){
                from.addPath(to);
            }
            else { throw new RuntimeException("From/To Location not found when setting paths"); }
        }
    }

    /**
     * Parse the graph for location and add to location and enetity list of server
     * @param locationGraph The graph object for a location in the DOT file
     */

    public void parseLocation (Graph locationGraph){
        Node locationDetails = locationGraph.getNodes(false).get(0);
        String locationName = locationDetails.getId().getId();
        String description = locationDetails.getAttribute("description");
        Location location = new Location(locationName, description);

        /*Add the location to the list of starting entities*/
        this.entities.put(locationName,location);

        /*Parse the contents of the location*/
        parseContentsOfLocation(locationGraph, location);

        /*Add the location to the list of game locations*/
        this.gameLocations.put(locationName, location);
        /*Set the location as the starting location if it is the first location*/
        if (this.startingLocation == null){ this.startingLocation = location; }
    }
    /**
     * Parse the contents of the location and add to the location object and add to entity list of server
     * @param locationGraph The graph object for the location in the DOT file
     * @param location The location object to add the contents to
     */

    public void parseContentsOfLocation (Graph locationGraph, Location location){

        ArrayList<Graph> locationEntities = locationGraph.getSubgraphs();

        for (Graph entity : locationEntities){
            String entityType = entity.getId().getId();
            ArrayList<Node> nodeofEntity = entity.getNodes(false);

            switch (entityType){
                case "artefacts":
                    for(Node artefact : nodeofEntity){
                        String name = artefact.getId().getId();
                        String description = artefact.getAttribute("description");
                        Artefact artefactObject = new Artefact(name, description);
                        this.entities.put(name,artefactObject);
                        location.addArtefact(artefactObject);
                    }
                    break;
                case "characters":
                    for (Node character : nodeofEntity){
                        String name = character.getId().getId();
                        String description = character.getAttribute("description");
                        Character characterObject = new Character(name, description);
                        this.entities.put(name,characterObject);
                        location.addCharacter(characterObject);
                    }
                    break;
                case "furniture":
                    for (Node furniture : nodeofEntity){
                        String name = furniture.getId().getId();
                        String description = furniture.getAttribute("description");
                        Furniture furnitureObject = new Furniture(name, description);
                        this.entities.put(name,furnitureObject);
                        location.addFurniture(furnitureObject);
                    }
                    break;
                default:
                    throw new RuntimeException("Unknown type of entity found in location");

            }
        }
    }

    public GameEntity getEntity (String enentityName){ return this.entities.get(enentityName); }

    public Location getLocation (String locationName){ return this.gameLocations.get(locationName);}

    public Location getStartingLocation(){ return this.startingLocation; }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * This method handles all incoming game commands and carries out the corresponding actions.</p>
    *
    * @param command The incoming command to be processed
    */
    public String handleCommand(String command) {
        // TODO implement your server logic here
        return "";
    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * Starts a *blocking* socket server listening for new connections.
    *
    * @param portNumber The port to listen on.
    * @throws IOException If any IO related operation fails.
    */
    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
                } catch (IOException e) {
                    System.out.println("Connection closed");
                }
            }
        }
    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * Handles an incoming connection from the socket server.
    *
    * @param serverSocket The client socket to read/write from.
    * @throws IOException If any IO related operation fails.
    */
    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {
            System.out.println("Connection established");
            String incomingCommand = reader.readLine();
            if(incomingCommand != null) {
                System.out.println("Received message from " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();
            }
        }
    }
}
