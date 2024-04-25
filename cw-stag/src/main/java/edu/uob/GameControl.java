package edu.uob;

public class GameControl {
    private GameModel model;
    private Tokeniser tokeniser;
    private Player currentPlayer;
    private String actualCommandString;
    private Command command;

    public GameControl(GameModel model){
        this.model = model;
    }
    /*how to add player to model?. How to handle trigger?*/
    public void preprocessCommand(String fullCommand){
        this.tokeniser = new Tokeniser(fullCommand);
        this.currentPlayer = this.model.getPlayer(this.tokeniser.getPlayerName());
        this.actualCommandString = this.tokeniser.tokenise();
    }

    public void parseCommand(){

    }



}
