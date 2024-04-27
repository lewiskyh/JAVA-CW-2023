package edu.uob;

public class Command {
    GameModel model;
    Player player;
    String command;

    public Command(GameModel model, String playerName, String command){
        this.model = model;
        this.player = this.model.getPlayer(playerName);
        this.command = command;
    }

    public String execute(){
        //to be overriden by the child classes

        return "";
    }

}
