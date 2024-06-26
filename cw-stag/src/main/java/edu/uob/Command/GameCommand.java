package edu.uob.Command;

import edu.uob.*;

public class GameCommand {
    GameModel model;
    Player player;
    String command;

    //Parent class to be inherited by all the command classes

    public GameCommand(GameModel model, String playerName, String command){
        this.model = model;
        this.player = this.model.getPlayer(playerName);
        this.command = command;
    }

    public String execute(){
        //to be overriden by the child classes

        return "";
    }

}
