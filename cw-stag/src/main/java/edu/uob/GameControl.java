package edu.uob;

public class GameControl {
    private GameModel model;
    private commandProcessor processor;
    private Player currentPlayer;
    private String actualCommandString;
    private Command command;

    public GameControl(GameModel model){
        this.model = model;
    }
    /*how to add player to model?. How to handle trigger?*/
    public void preprocessCommand(String fullCommand){
        this.processor = new commandProcessor(fullCommand);
        this.currentPlayer = this.model.getPlayer(this.processor.getPlayerName());
        this.actualCommandString = this.processor.commandProcessing();
    }

    public void parseCommand(){

    }



}
