package edu.uob.Command;

import edu.uob.*;

public class LookCommand extends GameCommand {

    public LookCommand(GameModel model, String playerName, String command){
        super(model, playerName, command);
    }

    public String execute(){
        String currentLocationName = player.getCurrentLocation().getName();
        Location currentLocation = model.getLocation(currentLocationName);
        StringBuilder sb = new StringBuilder();
        //Append location description, artefacts, characters, furniture and paths and their descriptions
        sb.append(currentLocation.getName());
        sb.append("\n");
        sb.append(currentLocation.getDescription());
        sb.append("\n");
        sb.append("Artefacts: ");
        for (Artefact artefact : currentLocation.getArtefactList()){
            sb.append(artefact.getName());
            sb.append(" -> ");
            sb.append(artefact.getDescription());
            sb.append("\n");
        }
        sb.append("Characters: ");
        for (GameCharacter character : currentLocation.getCharacterList()){
            sb.append(character.getName());
            sb.append(" -> ");
            sb.append(character.getDescription());
            sb.append("\n");
        }
        sb.append("Furniture: ");
        for (Furniture furniture : currentLocation.getFurnitureList()){
            sb.append(furniture.getName());
            sb.append(" -> ");
            sb.append(furniture.getDescription());
            sb.append("\n");
        }
        sb.append("You can go to: ");
        for (Location path : currentLocation.getPathList()){
            sb.append(path.getName());
            sb.append(" ");
        }
        sb.append("\n");
        //Check if there are other players in the location!Think

        return sb.toString();
    }
}
