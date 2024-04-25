package edu.uob;

import java.util.ArrayList;
import java.util.List;

public abstract class GameEntity {
    private final String name;
    private final String description;
    private String location;

    public GameEntity(String name, String description)
    {
        this.name = name;
        this.description = description;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()  { return description;}

    public void setLocation (String location){ this.location = location;}
    public String getLocation(){ return location;}




}
