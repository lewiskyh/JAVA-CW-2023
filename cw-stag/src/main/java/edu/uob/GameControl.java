package edu.uob;

import edu.uob.Command.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class GameControl {
    private GameModel model;
    private commandProcessor processor;
    private String currentPlayer;
    private String actualCommandString;

    private GameCommand command;
    private String[] basicCommands = {"look", "goto", "get", "drop", "inventory", "inv"};
    private Set<String> tailoredCommandTrigger;

    public GameControl(GameModel model){
        this.model = model;
        this.tailoredCommandTrigger = this.model.getActionHashMap().keySet();
    }
    /**
     * set the actual command after :
     * set the current player name
     * @param fullCommand the full command string e.g. simon: look
     * @return StringTrigger the trigger of the command e.g. look
     *
     */
    public String preprocessCommand(String fullCommand) {
        this.processor = new commandProcessor(fullCommand);
        this.actualCommandString = this.processor.commandCleaning().toLowerCase();
        this.currentPlayer = this.processor.getPlayerName();
        //Ensure only 1 trigger is used in the command
        checkTriggerNumber(this.actualCommandString, this.tailoredCommandTrigger, this.basicCommands);
        //Add player to gameModel if not exists already
        if (this.model.getPlayer(this.currentPlayer) == null) {
            this.model.addPlayer(this.currentPlayer);
            //Set the player's starting location
            this.model.getPlayer(this.currentPlayer).setCurrentLocation(this.model.getStartingLocation());
        }
        //check if the command is a basic command or a tailored command
        //The trigger returned must be a valid trigger
        return getActionTrigger(this.actualCommandString);
    }

    private void checkTriggerNumber(String actualCommandString, Set<String> tailoredCommandTrigger, String[] basicCommands){
        String [] commandFragments = actualCommandString.split(" ");
        List<String> allTriggers = new ArrayList<>();
        allTriggers.addAll(tailoredCommandTrigger);
        allTriggers.addAll(List.of(basicCommands));
        int triggerCount = 0;
        //Count the number of triggers in the command fragments
        for (int i = 0; i < commandFragments.length; i++){
            for (String trigger : allTriggers) {
                if (commandFragments[i].equalsIgnoreCase(trigger)) {
                    triggerCount++;
                }
            }
        }
        if (triggerCount != 1){
            throw new RuntimeException("You should use ONE trigger in the command");
        }
    }

    public void processCommand(String trigger){
        switch(trigger){
            case "look":
                this.command = new LookCommand(this.model, this.currentPlayer, this.actualCommandString);
                break;
            case "goto":
                this.command = new GotoCommand(this.model, this.currentPlayer, this.actualCommandString);
                break;
            case "get":
                this.command = new GetCommand(this.model, this.currentPlayer, this.actualCommandString);
                break;
            case "drop":
                this.command = new DropCommand(this.model, this.currentPlayer, this.actualCommandString);
                break;
            case "inventory", "inv":
                this.command = new InventoryCommand(this.model, this.currentPlayer, this.actualCommandString);
                break;
            default:
                this.command = new TailoredCommand(this.model, this.currentPlayer, this.actualCommandString, trigger);
                break;
        }
    }

    public GameCommand getCommand(){
        return this.command;
    }

    private String getActionTrigger (String actualCommand) {

        //Combine both basic and tailored triggers
        List<String> allTriggers = new ArrayList<>();

        allTriggers.addAll(this.tailoredCommandTrigger);
        allTriggers.addAll(List.of(this.basicCommands));
        allTriggers.sort(Comparator.comparingInt(String::length).reversed());

        for (String trigger : allTriggers) {
            if (trigger.split(" ").length > 1) {
                String phrasalVerb = phrasalVerbHandler(actualCommand, trigger);
                if (phrasalVerb != null) {
                    return phrasalVerb;
                }
            } else {
                if (actualCommand.contains(trigger)) {
                    return trigger;

                }
            }
        }
        return null;
    }


    private String phrasalVerbHandler (String actualCommand, String trigger){
        String [] commandFragments = actualCommand.split(" ");
        String [] triggerFragments = trigger.split(" ");
        int triggerLength = triggerFragments.length;
        String [] triggerKey = new String[triggerLength];
        for (int i = 0; i<=commandFragments.length-triggerLength; i++){
            boolean match = true;
            for (int j = 0; j<triggerLength; j++){
                if(!commandFragments[j+i].equalsIgnoreCase(triggerFragments[j])){
                    match = false;
                    break;
                }
            }
            if (match){ return trigger;}
        }

        return null;
    }

    public String getCurrentPlayer(){ return this.currentPlayer; }

    public GameModel getModel() { return this.model; }



}
