package edu.uob;

import java.util.ArrayList;

public class Player extends Character{

    private int health;
    private Location currentLocation;
    private ArrayList<Artefact> items;
    public Player(String name){
        super(name, "Player: " + name);
    }


}
