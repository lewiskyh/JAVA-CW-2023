package edu.uob;

import java.util.ArrayList;

public class Player extends GameCharacter{

    private int health = 3;
    private Location currentLocation;
    private ArrayList<Artefact> items = new ArrayList<>();
    public Player(String name){
        super(name, "Player: " + name);
    }

    //Set the current location of the player
    public void setCurrentLocation(Location location){
        this.currentLocation = location;
    }

    //Get the current location of the player
    public Location getCurrentLocation(){
        return this.currentLocation;
    }

    //Add an item to the player's inventory
    public void addItem(Artefact artefact){
        items.add(artefact);
    }
    //Get the player's inventory

    public ArrayList<Artefact> getItemList(){
        return items;
    }

    //Remove an item from the player's inventory
    public void removeItem(Artefact artefact){
        items.remove(artefact);
    }

    //Check if the player has an item
    public void addHealth(){
        if(this.health == 3){ return; }
        else{
            this.health = this.health + 1;
        }
    }
    //Reduce the player's health
    public void reduceHealth() {
        this.health = this.health - 1;
    }

    //Copy all player's items to the current location and clear all from player
    public void playerLostAllItems(){
        for(Artefact artefact : items){
            currentLocation.addArtefact(artefact);
        }
        items.clear();
    }
    //Copy all player's items to the current location and clear all from player
    public void playerRestartGame(GameModel model){
        this.health = 3;
        currentLocation = model.getStartingLocation();
    }
    //Get the player's health
    public int getHealth(){
        return this.health;
    }

    //Get the player's location in String
    public String getCurrentLocationName(){
        return currentLocation.getName();
    }
}
