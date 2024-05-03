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
    /* Add artefact, character, furniture and path to the location */
    public void addCharacter(GameCharacter character){ characterList.add(character); }
    /* Add artefact, character, furniture and path to the location */
    public void addFurniture(Furniture furniture){ furnitureList.add(furniture); }
    /* Add artefact, character, furniture and path to the location */
    public void addPath(Location path){ pathList.add(path); }
    /* Drop path from the location */
    public void dropPath(Location path){ pathList.remove(path); }

    /* For a given name, return the artefact, character, furniture or path object*/
    public Artefact getArtefact(String name){

        for (Artefact artefact : artefactList){
            if(artefact.getName().equals(name)){ return artefact; }
        }
        return null;
    }
    /* For a given name, return the artefact, character, furniture or path object*/
    public GameCharacter getCharacter(String name){
        for (GameCharacter character : characterList){
            if(character.getName().equals(name)){ return character; }
        }
        return null;
    }
    //For a given name, return the artefact, character, furniture or path object
    /*public Furniture getFurniture(String name){
        for (Furniture furniture : furnitureList){
            if(furniture.getName().equals(name)){ return furniture; }
        }
        return null;
    }*/
    public Location getPath(String name){
        for (Location path : pathList){
            if(path.getName().equals(name)){ return path; }
        }
        return null;
    }

    /* Getters for the location object */
    public String getName(){ return super.getName(); }

    /* Getters for the location object */
    public String getDescription(){ return super.getDescription(); }

    /* Get artefactlist */
    public List<Artefact> getArtefactList(){ return artefactList; }
    /* Get characterlist */
    public List<GameCharacter> getCharacterList(){ return characterList; }
    /* Get furniturelist */
    public List<Furniture> getFurnitureList(){ return furnitureList; }
    /* Get pathlist */
    public List<Location> getPathList(){ return pathList; }

    /* Remove artefact from the location */
    public void removeArtefact(String artefactName){
        for (Artefact artefact : this.artefactList){
            if(artefact.getName().equalsIgnoreCase(artefactName)){
                artefactList.remove(artefact);
                break;
            }
        }
    }
    /* Remove character from the location */

    public void removeCharacter(String characterName){
        for (GameCharacter character : this.characterList){
            if(character.getName().equalsIgnoreCase(characterName)){
                characterList.remove(character);
                break;
            }
        }
    }
    /* Remove furniture from the location */

    public void removeFurniture(String furnitureName){
        for (Furniture furniture : this.furnitureList){
            if(furniture.getName().equalsIgnoreCase(furnitureName)){
                furnitureList.remove(furniture);
                break;
            }
        }
    }




}
