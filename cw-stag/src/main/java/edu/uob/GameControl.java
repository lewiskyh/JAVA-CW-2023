package edu.uob;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class GameControl {
    private GameModel model;
    private commandProcessor processor;
    private String currentPlayer;
    private String actualCommandString;
    private Command command;
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
        this.actualCommandString = this.processor.commandProcessing().toLowerCase();
        this.currentPlayer = this.processor.getPlayerName();
        //Add player to gameModel if not exists already
        if (this.model.getPlayer(this.currentPlayer) == null) {
            this.model.addPlayer(this.currentPlayer);
        }
        //check if the command is a basic command or a tailored command
        //The trigger returned must be a valid trigger
        ArrayList<String> actionTrigger = checkActionTrigger(this.actualCommandString);
        if (actionTrigger.size() != 1) {
            for (String trigger : actionTrigger){

            }
        }
        //How to postprocess triggers???
        return actionTrigger.get(0);
    }



    public void executeCommand(){
        this.command.execute();
    }

    public void processCommand(String trigger){
        switch(trigger){
            case "look":
                this.command = new LookCommand(this.model, this.currentPlayer, this.actualCommandString);
            case "goto":
                //this.command = new GotoCommand(this.model, this.currentPlayer, this.actualCommandString);
                break;
            case "get":
                //this.command = new GetCommand(this.model, this.currentPlayer, this.actualCommandString);
                break;
            case "drop":
                //this.command = new DropCommand(this.model, this.currentPlayer, this.actualCommandString);
                break;
            case "inventory":
                //this.command = new InventoryCommand(this.model, this.currentPlayer, this.actualCommandString);
                break;
            case "inv":
                //this.command = new InventoryCommand(this.model, this.currentPlayer, this.actualCommandString);
                break;
            default:
                //this.command = new TailoredCommand(this.model, this.currentPlayer, trigger, this.actualCommandString);
                break;
        }
    }

    public ArrayList<String> checkActionTrigger (String actualCommand){
        int triggerCount = 0;
        ArrayList<String> actionTrigger = new ArrayList<>();

        //Combine both basic and tailored triggers
        List<String> allTriggers = new ArrayList<>();

        allTriggers.addAll(this.tailoredCommandTrigger);
        allTriggers.addAll(List.of(this.basicCommands));
        allTriggers.sort(Comparator.comparingInt(String::length).reversed());

        for (String trigger : allTriggers){
            if(trigger.split(" ").length > 1) {
                String phrasalVerb = phrasalVerbHandler(actualCommand, trigger);
                if (phrasalVerb != null) {
                    triggerCount++;
                    actionTrigger.add(phrasalVerb);
                    System.out.println("Phrasal Verb: " + actionTrigger);
                }
            }
            else{
                if(actualCommand.contains(trigger)) {
                    triggerCount++;
                    actionTrigger.add(trigger);
                    System.out.println("Singleword Verb: " + actionTrigger);

                }
            }
        }
        return actionTrigger;

    }

    //THink!

    private ArrayList<String> filterTrigger (ArrayList<String> actionTrigger){
        actionTrigger.sort(Comparator.comparingInt(String::length).reversed());
        ArrayList<String> filteredTrigger = new ArrayList<>();
        for (String trigger : actionTrigger){
            if (trigger.split(" ").length > 1){
                filteredTrigger.add(trigger);
            }
        }
        return filteredTrigger;
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
            System.out.println("Trigger: " + trigger);
            if (match){ return trigger;}
        }

        return null;
    }

    public String getCurrentPlayer(){ return this.currentPlayer; }

    public GameModel getModel() { return this.model; }



}
