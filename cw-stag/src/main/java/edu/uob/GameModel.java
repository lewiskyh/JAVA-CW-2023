package edu.uob;

import java.util.*;
import java.io.*;

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

public class GameModel {

    private Location startingLocation;
    private Map<String, Set<GameAction>> actionMap;
    private ArrayList<String> existingEntities;
    private HashMap<String, Location> gameLocations;
    private HashMap<String, Player> players;

    public GameModel(File entitiesFile,File actionsFile) {
        this.existingEntities = new ArrayList<>();
        this.gameLocations = new HashMap<>();
        this.players = new HashMap<>();
        try{
            readEntityFile(entitiesFile);
        }catch(IOException | ParseException e){
            throw new RuntimeException("Error reading entity file");
        }
        try {
            actionMap = readActionFile(actionsFile);
        }catch (IOException | ParserConfigurationException | SAXException e){
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
            interpretLocation(locationGraph);
        }

        /*Path subgraphs must appear after the locations*/
        Graph pathGraph = wholeDocument.getSubgraphs().get(1);
        /*Parse the paths and store them in the corresponding location object*/
        interpretPaths(pathGraph);
    }

    public void interpretLocation(Graph locationGraph) {
        Node locationDetails = locationGraph.getNodes(false).get(0);
        String locationName = locationDetails.getId().getId();
        String description = locationDetails.getAttribute("description");
        Location location = new Location(locationName, description);

        /*Add the location to the list of starting entities*/
        this.existingEntities.add(locationName);

        /*Parse the contents of the location*/
        interpretEntitiesOfLocation(locationGraph, location);

        /*Add the location to the list of game locations*/
        this.gameLocations.put(locationName, location);
        /*Set the location as the starting location if it is the first location*/
        if (this.startingLocation == null) {
            this.startingLocation = location;
        }
    }

    public void interpretEntitiesOfLocation(Graph locationGraph, Location location) {

        ArrayList<Graph> locationEntities = locationGraph.getSubgraphs();

        for (Graph entity : locationEntities) {
            String entityType = entity.getId().getId();
            ArrayList<Node> nodeofEntity = entity.getNodes(false);

            switch (entityType) {
                case "artefacts":
                    for (Node artefact : nodeofEntity) {
                        String name = artefact.getId().getId();
                        String description = artefact.getAttribute("description");
                        Artefact artefactObject = new Artefact(name, description);
                        this.existingEntities.add(name);
                        location.addArtefact(artefactObject);
                    }
                    break;
                case "characters":
                    for (Node character : nodeofEntity) {
                        String name = character.getId().getId();
                        String description = character.getAttribute("description");
                        Character characterObject = new Character(name, description);
                        this.existingEntities.add(name);
                        location.addCharacter(characterObject);
                    }
                    break;
                case "furniture":
                    for (Node furniture : nodeofEntity) {
                        String name = furniture.getId().getId();
                        String description = furniture.getAttribute("description");
                        Furniture furnitureObject = new Furniture(name, description);
                        this.existingEntities.add(name);
                        location.addFurniture(furnitureObject);
                    }
                    break;
                default:
                    throw new RuntimeException("Unknown type of entity found in location");

            }
        }
    }

    public void interpretPaths(Graph pathGraph) {
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
            if (from != null && to != null) {
                from.addPath(to);
            } else {
                throw new RuntimeException("From/To Location not found when setting paths");
            }
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
                triggers.add(triggerNode.getTextContent().toLowerCase().trim());
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
                    action.addSubject(node.getTextContent().toLowerCase().trim());
                    break;
                case "produced":
                    action.addProduced(node.getTextContent().toLowerCase().trim());
                    break;
                case "consumed":
                    action.addConsumed(node.getTextContent().toLowerCase().trim());
                    break;
                case "triggers":
                    action.addTrigger(node.getTextContent().toLowerCase().trim());
                    actionHashMap.get(node.getTextContent().toLowerCase().trim()).add(action);
                    break;
                default:
                    throw new RuntimeException("Unknown tag name found in action file");
            }
        }
    }

    public Location getLocation (String locationName){ return this.gameLocations.get(locationName);}

    public Location getStartingLocation(){ return this.startingLocation; }

    public Map<String, Set<GameAction>> getActionHashMap() {
        return actionMap;
    }

    public Player getPlayer (String playerName){
        return this.players.get(playerName);
    }

    public void addPlayer (String playerName){ this.players.put(playerName, new Player(playerName)); }
}
