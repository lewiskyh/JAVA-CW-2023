package edu.uob.Command;
import edu.uob.*;

import java.util.List;

public class GetCommand extends GameCommand{

    //Constructor for the GetCommand
    public GetCommand(GameModel model, String playerName, String command){
        super(model, playerName, command);
    }

    //Execute the get command
    @Override
    public String execute() {

        String currentLocationName = player.getCurrentLocation().getName();
        Location currentLocation = model.getLocation(currentLocationName);
        String[] commandFragments = command.split(" ");
        List<Artefact> artefactList = currentLocation.getArtefactList();
        int itemMatch = 0;
        String artefactName = "";
        Artefact artefactToGet = null;

        for (String fragment : commandFragments){
            for (Artefact artefact : artefactList){
                if (fragment.equalsIgnoreCase(artefact.getName())){
                    itemMatch++;
                    if(itemMatch > 1){
                        throw new RuntimeException("You can only pick up one item each time. Please specify the item.");
                    }
                    artefactToGet = artefact;
                    artefactName = artefactToGet.getName();
                }
            }
        }
        if (itemMatch == 0){
            throw new RuntimeException("No such item. Please check again.");
        }
        //Add the artefact to the player's inventory
        player.addItem(artefactToGet);
        //Remove the artefact from the location
        currentLocation.removeArtefact(artefactName);
        return "You have picked up the item: " + artefactName;
    }
    //Throw error if more than one target is provided
}

