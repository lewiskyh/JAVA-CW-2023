package edu.uob.Command;

import edu.uob.*;

import java.util.ArrayList;
import java.util.List;

public class DropCommand extends GameCommand{

    public DropCommand(GameModel model, String playerName, String command){
        super(model, playerName, command);
    }

    @Override
    public String execute(){
        String currentLocationName = player.getCurrentLocation().getName();
        Location currentLocation = model.getLocation(currentLocationName);
        ArrayList<Artefact> artefactList = player.getItemList();
        String[] commandFragments = command.split(" ");
        String artefactName = "";
        Artefact artefactToDrop = null;
        int itemMatch = 0;

        for (int j = 0; j < commandFragments.length; j++){
            for (int i =0 ; i < player.getItemList().size(); i++){
                if (commandFragments[j].equalsIgnoreCase(artefactList.get(i).getName())){
                    itemMatch++;
                    if(itemMatch > 1){
                        throw new RuntimeException("You can only drop ONE item each time.");
                    }
                    artefactToDrop = artefactList.get(i);
                }
            }
        }
        if (itemMatch == 0){
            throw new RuntimeException("No such item in your inventory.");
        }
        player.removeItem(artefactToDrop);
        artefactName = artefactToDrop.getName();

        //Add the artefact to the location
        currentLocation.addArtefact(artefactToDrop);
        return "You have dropped the item: " + artefactName;

    }

}
