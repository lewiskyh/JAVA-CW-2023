package edu.uob.Command;

import edu.uob.*;
public class InventoryCommand extends GameCommand {

    public InventoryCommand(GameModel model, String playerName, String command){
        super(model, playerName, command);
    }

    @Override
    public String execute() {
        StringBuilder sb = new StringBuilder();
        sb.append("Your inventory: ");
        sb.append("\n");
        for (Artefact artefact : player.getItemList()){
            sb.append(artefact.getName());
            sb.append("\n");
        }
        return sb.toString();
    }

}
