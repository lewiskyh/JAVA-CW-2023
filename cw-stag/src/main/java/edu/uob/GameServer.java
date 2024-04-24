package edu.uob;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.*;

import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import com.alexmerz.graphviz.objects.Edge;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public final class GameServer {

    private static final char END_OF_TRANSMISSION = 4;

    private Location startingLocation;

    private Map<String, Set<GameAction>> actionHashMap;
    private ArrayList<String> existingEntityName = new ArrayList<>();
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
        try {
            this.actionHashMap = readActionFile(actionsFile);
        }catch (IOException | ParserConfigurationException | SAXException e){
            throw new RuntimeException(e);
        }
    }

    public Map<String, Set<GameAction>> readActionFile (File actionsFile) throws ParserConfigurationException, IOException, SAXException {
        Map<String, Set<GameAction>> actionHashMap = new HashMap<>();
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(actionsFile);
        Element root = document.getDocumentElement();
        NodeList actions = root.getElementsByTagName("action");
        Set<String> triggers = new HashSet<>();
        fillTriggerSet(triggers, actions);
        for(String trigger : triggers){
            actionHashMap.put(trigger, new HashSet<>());
        }
        for(int i = 0; i <actions.getLength(); i++){
            Element actionElement = (Element) actions.item(i);
            GameAction action = new GameAction();
            addActionDetailsByTag(actionElement, "subjects", "entity", action, actionHashMap);
            addActionDetailsByTag(actionElement, "produced", "entity", action, actionHashMap);
            addActionDetailsByTag(actionElement, "consumed", "entity", action, actionHashMap);
            addActionDetailsByTag(actionElement, "triggers", "keyphrase", action, actionHashMap);
            String narration = actionElement.getElementsByTagName("narration").item(0).getTextContent().trim();
            action.addNarration(narration);
        }

        return actionHashMap;
    }

    public void fillTriggerSet (Set<String> triggers, NodeList actions){
        for(int i = 0; i < actions.getLength(); i++){
            Element actionElement = (Element) actions.item(i);
            Element triggersElement = (Element) actionElement.getElementsByTagName("triggers").item(0);
            NodeList triggerNodeList = triggersElement.getElementsByTagName("keyphrase");
            for(int j = 0; j < triggerNodeList.getLength(); j++){
                Element triggerNode = (Element) triggerNodeList.item(j);
                triggers.add(triggerNode.getTextContent().trim());
            }
        }
    }

    public void addActionDetailsByTag (Element actionElement, String tagName, String childTag,GameAction action,
                                       Map<String, Set<GameAction>> actionHashMap){
        Element elemntChild = (Element) actionElement.getElementsByTagName(tagName).item(0);
        NodeList childNodeList = elemntChild.getElementsByTagName(childTag);
        for(int i = 0; i < childNodeList.getLength(); i++){
            Element node = (Element) childNodeList.item(i);
            switch (tagName) {
                case "subjects":
                    action.addSubject(node.getTextContent().trim());
                    break;
                case "produced":
                    action.addProduced(node.getTextContent().trim());
                    break;
                case "consumed":
                    action.addConsumed(node.getTextContent().trim());
                    break;
                case "triggers":
                     action.addTrigger(node.getTextContent().trim());
                     actionHashMap.get(node.getTextContent().trim()).add(action);
                     break;
                default:
                    throw new RuntimeException("Unknown tag name found in action file");
            }
        }
    }

    public Map<String, Set<GameAction>> getActionHashMap() {
        return actionHashMap;
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
            interpretLocation(locationGraph);
        }

        /*Path subgraphs must appear after the locations*/
        Graph pathGraph = wholeDocument.getSubgraphs().get(1);
        /*Parse the paths and store them in the corresponding location object*/
        interpretPaths(pathGraph);
    }

    public void interpretPaths (Graph pathGraph) {
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

    public void interpretLocation (Graph locationGraph){
        Node locationDetails = locationGraph.getNodes(false).get(0);
        String locationName = locationDetails.getId().getId();
        String description = locationDetails.getAttribute("description");
        Location location = new Location(locationName, description);

        /*Add the location to the list of starting entities*/
        this.existingEntityName.add(locationName);

        /*Parse the contents of the location*/
        interpretEntitiesOfLocation(locationGraph, location);

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

    public void interpretEntitiesOfLocation (Graph locationGraph, Location location){

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
                        this.existingEntityName.add(name);
                        location.addArtefact(artefactObject);
                    }
                    break;
                case "characters":
                    for (Node character : nodeofEntity){
                        String name = character.getId().getId();
                        String description = character.getAttribute("description");
                        Character characterObject = new Character(name, description);
                        this.existingEntityName.add(name);
                        location.addCharacter(characterObject);
                    }
                    break;
                case "furniture":
                    for (Node furniture : nodeofEntity){
                        String name = furniture.getId().getId();
                        String description = furniture.getAttribute("description");
                        Furniture furnitureObject = new Furniture(name, description);
                        this.existingEntityName.add(name);
                        location.addFurniture(furnitureObject);
                    }
                    break;
                default:
                    throw new RuntimeException("Unknown type of entity found in location");

            }
        }
    }

    public Boolean checkEntityAlreadyExists (String enentityName){
        return this.existingEntityName.contains(enentityName);
    }

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
