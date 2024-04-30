package edu.uob.Command;

import edu.uob.*;

import java.util.*;

public class TailoredCommand extends GameCommand{

    //Constructor for TailoredCommand
    private final String trigger;
    public TailoredCommand(GameModel model, String playerName, String command, String trigger){
        super(model, playerName, command);
        this.trigger = trigger;
    }

    //Support partial commands - must contain a trigger and at least ONE subject
    // E.g. "unlock trapdoor with key" can be written as "unlock trapdoor" or "unlock with key"
    //ONE trigger and ONE subject are provided

    //Entities in the command must match the entities defined in the action file

    //If more than one action is matched, do not execute and throw error message

    @Override
    public String execute() {
        String currentLocationName = player.getCurrentLocation().getName();
        Location currentLocation = model.getLocation(currentLocationName);
        String[] commandFragments = command.split(" ");
        int actionMatched = 0;
        GameAction actionToExecute = null;
        Set<GameAction> possibleActions = this.model.getActionHashMap().get(this.trigger);
        for (GameAction action: possibleActions){
            if (isAtLeastOneSubjectProvided(action, commandFragments)){
                actionMatched++;
                if (actionMatched > 1){
                    throw new RuntimeException("More than one possible action possible. Please specify.");
                }
                actionToExecute = action;
            }
        }
        //Based on the action, check if the subjects exist in the player's inventory or in the location
        // The subjects can be character, artefacts, furniture, location, health.
        if (actionMatched == 0){ throw new RuntimeException("Cannot identify action. Please specify subjects");}
        if (!checkSubjectFullAvailability(actionToExecute, currentLocation)){
            throw new RuntimeException("Subjects must be available in the location or in player's inventory.");
        }
        //Produce the entity.
        executeProduce(actionToExecute);
        //Consume the entity - only return meaningful if player is dead
        String restartMessage = executeConsume(actionToExecute);
        return actionToExecute.getNarration() + "\n" + restartMessage;
    }

    //To check if at least one subject in the command matches the subjects in action details
    private boolean isAtLeastOneSubjectProvided (GameAction action, String[] commandFragments){
        ArrayList<String> subjects = action.getSubjects();
        for (String fragment : commandFragments) {
            for (String subject : subjects) {
                if (fragment.equalsIgnoreCase(subject)) {
                    return true;
                }
            }
        }
        return false;
    }
    //Check if the subjects are present in the player's inventory or in the location
    private boolean checkSubjectFullAvailability(GameAction action, Location currentLocation){

        Set<String> environmentSubjects = new HashSet<>();
        for (GameEntity entity : currentLocation.getCharacterList()){
            environmentSubjects.add(entity.getName().toLowerCase());
        }
        for (GameEntity entity : currentLocation.getFurnitureList()){
            environmentSubjects.add(entity.getName().toLowerCase());
        }
        for (GameEntity entity : player.getItemList()){
            environmentSubjects.add(entity.getName().toLowerCase());
        }

        for (String subject : action.getSubjects()){
            if(!environmentSubjects.contains(subject.toLowerCase())){ return false; }
        }
        return true;
    }
    //Location(produced as path) vs Other Entities (moved from original location to current's location)
    private void executeProduce(GameAction action){
        //Check Entity is a location or not
        for (String entity : action.getProduced()){
            String entityLowerCase = entity.toLowerCase();
            if(entityLowerCase.equalsIgnoreCase("health")) {
                player.addHealth();
            }
            else if (isEntityLocation(entityLowerCase)){
                //Add location path to current location
                Location newLocation = model.getGameLocations().get(entityLowerCase);
                player.getCurrentLocation().addPath(newLocation);
            }
            else{
                //Move entity from original location to current location
                String currentLocationName = player.getCurrentLocation().getName();
                Location currentLocation = model.getLocation(currentLocationName);
                GameEntity entityToMove = extractEntityFromOriginalLocation(entityLowerCase);
                moveEntityToGivenLocation(currentLocation, entityToMove);
            }
        }
    }

    //Move entity from original location to a given location

    private void moveEntityToGivenLocation(Location newLocation, GameEntity entity){
        if (entity instanceof GameCharacter){
            newLocation.addCharacter((GameCharacter) entity);
        }
        else if (entity instanceof Artefact){
            newLocation.addArtefact((Artefact) entity);
        }
        else if (entity instanceof Furniture) {
            newLocation.addFurniture((Furniture) entity);
        }
        else{
            throw new RuntimeException("Null entity ");
        }
    }

    //Extract entity from player's inventory or location

    private GameEntity extractEntityFromOriginalLocation(String entity){
        //Search player's inventory first

        for (Location location : model.getGameLocations().values()){
            GameEntity entityFound = findAndRemoveEntity(location, entity);
            if (entityFound != null){
                return entityFound;
            }
        }
        return null;
    }


    //Return the corresponding entity and remove it from the location or player's inventory

    private GameEntity findAndRemoveEntity (Location location, String targetEntity){
        for (Artefact artefact : player.getItemList()){
            if (artefact.getName().equalsIgnoreCase(targetEntity)){
                player.removeItem(artefact);
                return artefact;
            }
        }
        for (GameEntity character : location.getCharacterList()){
            if (character.getName().equalsIgnoreCase(targetEntity)){
                location.removeCharacter(targetEntity);
                return character;
            }
        }
        for (GameEntity artefact : location.getArtefactList()){
            if (artefact.getName().equalsIgnoreCase(targetEntity)){
                location.removeArtefact(targetEntity);
                return artefact;
            }
        }
        for (GameEntity furniture : location.getFurnitureList()){
            if (furniture.getName().equalsIgnoreCase(targetEntity)){
                location.removeFurniture(targetEntity);
                return furniture;
            }
        }
        return null;
    }


    //Check if the entity is a location
    //@return true if the entity is a location
    private boolean isEntityLocation(String entity){
        HashMap<String, Location> locationMap = model.getGameLocations();
        return locationMap.containsKey(entity.toLowerCase());
    }

    //Execute "consume" of the action - remove the consumed entities from the location or player's inventory
    private String executeConsume(GameAction action){
        //Health command?
        for (String entity : action.getConsumed()){
            String entityLowerCase = entity.toLowerCase();
            if(entityLowerCase.equalsIgnoreCase("health")) {
                player.reduceHealth();
                if(player.getHealth() == 0){
                    player.playerLostAllItems();
                    player.playerRestartGame(model);
                    return "You are dead, LOSER! Game restarted.\n";
                }
            }
            //Check Entity is a location or NOT
            else if (isEntityLocation(entityLowerCase)){
                //Add location path to current location
                Location newLocation = model.getGameLocations().get(entityLowerCase);
                player.getCurrentLocation().dropPath(newLocation);
            }
            else{
                //Move entity from original location to current location
                Location storeroom = model.getGameLocations().get("storeroom");
                GameEntity entityToMove = extractEntityFromOriginalLocation(entityLowerCase);
                moveEntityToGivenLocation(storeroom, entityToMove);

            }
        }
        return "";
    }
}
