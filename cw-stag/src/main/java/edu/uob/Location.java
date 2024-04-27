package edu.uob;

import java.util.ArrayList;
import java.util.List;

public class Location extends GameEntity{

    /* List of artefacts, characters, furniture and paths in the location */
    private List<Artefact> artefactList;
    private List<GameCharacter> characterList;
    private List<Furniture> furnitureList;
    private List<Location> pathList;

    /* Initialise Location object with empty lists ready to store elements parsed*/
    public Location(String name, String description){
        super(name, description);
        artefactList = new ArrayList<Artefact>();
        characterList = new ArrayList<GameCharacter>();
        furnitureList = new ArrayList<Furniture>();
        pathList = new ArrayList<Location>(); /* Path to another location */
    }

    /* Add artefact, character, furniture and path to the location */
    public void addArtefact(Artefact artefact){ artefactList.add(artefact); }
    public void addCharacter(GameCharacter character){ characterList.add(character); }
    public void addFurniture(Furniture furniture){ furnitureList.add(furniture); }
    public void addPath(Location path){ pathList.add(path); }

    /* For a given name, return the artefact, character, furniture or path object*/

    public Artefact getArtefact(String name){

        for (Artefact artefact : artefactList){
            if(artefact.getName().equals(name)){ return artefact; }
        }
        return null;
    }
    public GameCharacter getCharacter(String name){
        for (GameCharacter character : characterList){
            if(character.getName().equals(name)){ return character; }
        }
        return null;
    }

    public Furniture getFurniture(String name){
        for (Furniture furniture : furnitureList){
            if(furniture.getName().equals(name)){ return furniture; }
        }
        return null;
    }
    public Location getPath(String name){
        for (Location path : pathList){
            if(path.getName().equals(name)){ return path; }
        }
        return null;
    }

    public String getName(){ return super.getName(); }
    public String getDescription(){ return super.getDescription(); }

    public List<Artefact> getArtefactList(){ return artefactList; }
    public List<GameCharacter> getCharacterList(){ return characterList; }
    public List<Furniture> getFurnitureList(){ return furnitureList; }
    public List<Location> getPathList(){ return pathList; }

    public void removeArtefact(String artefactName){
        for (Artefact artefact : this.artefactList){
            if(artefact.getName().equalsIgnoreCase(artefactName)){
                artefactList.remove(artefact);
                break;
            }
        }
    }




}
