package edu.uob;

public class LookCommand extends Command{

    public LookCommand(GameModel model, String playerName, String command){
        super(model, playerName, command);
    }

    public String execute(){
        String currentLocationName = player.getCurrentLocation().getName();
        Location currentLocation = model.getLocation(currentLocationName);
        return "";
    }
    //Player's current location. Print the entities in the location.But how to set location of player? The starting location?
}
