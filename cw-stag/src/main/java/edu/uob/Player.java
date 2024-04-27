package edu.uob;

import java.util.ArrayList;

public class Player extends GameCharacter{

    private int health;
    private Location currentLocation;
    private ArrayList<Artefact> items = new ArrayList<>();
    public Player(String name){
        super(name, "Player: " + name);
    }

    public void setCurrentLocation(Location location){
        this.currentLocation = location;
    }
    public Location getCurrentLocation(){
        return this.currentLocation;
    }

    public void addItem(Artefact artefact){
        items.add(artefact);
    }

    public ArrayList<Artefact> getItemList(){
        return items;
    }


}
