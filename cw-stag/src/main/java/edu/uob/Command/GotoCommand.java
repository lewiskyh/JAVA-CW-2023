package edu.uob.Command;


import edu.uob.*;

import java.util.List;

public class GotoCommand extends GameCommand {

    public GotoCommand(GameModel model, String playerName, String command){
        super(model, playerName, command);
    }

    public String execute() {
        String currentLocationName = player.getCurrentLocation().getName();
        Location currentLocation = model.getLocation(currentLocationName);
        int pathMatch = 0;
        String pathName = "";
        String[] commandFragments = command.split(" ");

        List<Location> pathList = currentLocation.getPathList();

        for (String fragment : commandFragments){
            for (Location path : pathList){
                if (fragment.equalsIgnoreCase(path.getName())){
                    pathName = path.getName();
                    pathMatch++;
                    if (pathMatch > 1){ throw new RuntimeException("More than one path provided. Please specify the path."); }
                }
            }
        }
        if (pathMatch == 0){ throw new RuntimeException("No available path. Please check again."); }
        this.player.setCurrentLocation(model.getLocation(pathName));

        return "You have arrived at new location " + pathName + "\n" + "Location : " + model.getLocation(pathName).getDescription();
    }

}
