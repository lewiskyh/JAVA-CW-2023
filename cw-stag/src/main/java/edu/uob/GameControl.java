package edu.uob;

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
        return checkActionTrigger(this.actualCommandString);
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

    public String checkActionTrigger (String actualCommand){
        int triggerCount = 0;
        String actionTrigger = "";
        for (String command : this.basicCommands){
            if(actualCommand.contains(command)){
                triggerCount++;
                actionTrigger = command;
            }
        }
        for (String command : this.tailoredCommandTrigger){
            if(actualCommand.contains(command)){
                triggerCount++;
                actionTrigger = command;
            }
        }
        if(triggerCount != 1){
            throw new RuntimeException("Invalid command, please enter only one action trigger");
        }
        return actionTrigger;

    }



}
