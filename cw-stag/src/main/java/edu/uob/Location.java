package edu.uob;

import java.util.ArrayList;
import java.util.List;

public class Location extends GameEntity{

    /* List of artefacts, characters, furniture and paths in the location */
    private List<Artefact> artefactList;
    private List<Character> characterList;
    private List<Furniture> furnitureList;
    private List<Location> pathList;

    /* Initialise Location object with empty lists ready to store elements parsed*/
    public Location(String name, String description){
        super(name, description);
        artefactList = new ArrayList<Artefact>();
        characterList = new ArrayList<Character>();
        furnitureList = new ArrayList<Furniture>();
        pathList = new ArrayList<Location>();
    }

}
