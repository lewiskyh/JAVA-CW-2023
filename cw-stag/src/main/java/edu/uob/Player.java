package edu.uob;

import java.util.ArrayList;

public class Player extends GameCharacter{

    private int health = 3;
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

    public void removeItem(Artefact artefact){
        items.remove(artefact);
    }

    public void addHealth(){
        if(this.health == 3){ return; }
        else{
            this.health = this.health + 1;
        }
    }
    public void reduceHealth() {
        this.health = this.health - 1;
    }

    public void playerLostAllItems(){
        for(Artefact artefact : items){
            currentLocation.addArtefact(artefact);
        }
        items.clear();
    }
    public void playerRestartGame(GameModel model){
        this.health = 3;
        currentLocation = model.getStartingLocation();
    }
    public int getHealth(){
        return this.health;
    }
}
