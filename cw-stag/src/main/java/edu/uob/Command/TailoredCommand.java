package edu.uob.Command;

import edu.uob.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TailoredCommand extends GameCommand{

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
            if (isEnoughSubjectProvided(action, commandFragments)){
                actionMatched++;
                if (actionMatched > 1){
                    throw new RuntimeException("More than one possible action identified. Please specify subjects.");
                }
                actionToExecute = action;
            }
        }
        //Based on the action, check if the subjects exist in the player's inventory or in the location
        // The subjects can be character, artefacts, furniture, location, health.
        if (actionMatched == 0){ throw new RuntimeException("Cannot identify action. Please specify subjects");}
        return actionToExecute.getNarration();
    }

    //To check if at least one subject in the command matches the subjects in action details
    private boolean isEnoughSubjectProvided (GameAction action, String[] commandFragments){
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

    private boolean checkSubjectAvailability(GameAction action, Location currentLocation){
        //Check if the subjects are present in the player's inventory or in the location
        ArrayList<GameEntity> environmentSubjects = new ArrayList<>();
        environmentSubjects.addAll(currentLocation.getArtefactList());
        environmentSubjects.addAll(currentLocation.getCharacterList());
        environmentSubjects.addAll(currentLocation.getFurnitureList());
        ArrayList<Artefact> playerInventory = player.getItemList();
        ArrayList<String> subjectsToCheck = action.getSubjects();
        int subjectCount = subjectsToCheck.size();

        for (String subject : subjectsToCheck){
            for (GameEntity entity : environmentSubjects){
                if(subject.equalsIgnoreCase(entity.getName())){
                    subjectCount--;
                }
            }
            for(Artefact artefact : playerInventory){
                if(subject.equalsIgnoreCase(artefact.getName())){
                    subjectCount--;
                }
            }
            if(subjectCount == 0){ return true; }
        }
        return false;
    }

    private void executeProduce(GameAction action){
        //The entity type?
        //Path?
        //If it is health, add health to player
        ArrayList<String> entitiestoProduce = action.getProduced();
    }

    private void executeConsume(GameAction action){
        //Is the entity present in player's inventory or in the environment?
        //If it is health, deduct health from player.
        //If health drops to 0, all items dropped to location as artefacts. Player transfer back to starting location
        //after this, build health built in function! - display player's health
    }
}
