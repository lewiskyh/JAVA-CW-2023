package edu.uob;

import java.util.ArrayList;
import java.util.List;

public abstract class GameEntity {
    private final String name;
    private final String description;
    private String location;

    //Constructor for GameEntity - parent class
    public GameEntity(String name, String description)
    {
        this.name = name;
        this.description = description;
    }

    //get entity name in string
    public String getName()
    {
        return name;
    }

    //get entity description in string
    public String getDescription()  { return description;}

    //set entity location
    public void setLocation (String location){ this.location = location;}
    //get entity location in string
    public String getLocation(){ return location;}





}
