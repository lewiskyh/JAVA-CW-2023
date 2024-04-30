package edu.uob.Command;

import edu.uob.*;

public class HealthCommand extends GameCommand{

    //Constructor for HealthCommand
    public HealthCommand(GameModel model, String playerName, String command){
        super(model, playerName, command);
    }

    //Execute the health command
    @Override
    public String execute() {
        StringBuilder sb = new StringBuilder();
        sb.append("Your health: ");
        sb.append(player.getHealth());
        sb.append("\n");
        return sb.toString();
    }
}
