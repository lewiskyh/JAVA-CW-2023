package edu.uob.Command;

import edu.uob.*;

public class HealthCommand extends GameCommand{

    public HealthCommand(GameModel model, String playerName, String command){
        super(model, playerName, command);
    }

    @Override
    public String execute() {
        StringBuilder sb = new StringBuilder();
        sb.append("Your health: ");
        sb.append(player.getHealth());
        sb.append("\n");
        return sb.toString();
    }
}
