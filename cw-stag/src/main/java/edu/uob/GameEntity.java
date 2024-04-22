package edu.uob;

import java.util.ArrayList;
import java.util.List;

public abstract class GameEntity {
    private final String name;
    private final String description;

    public GameEntity(String name, String description)
    {
        this.name = name;
        this.description = description;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }




}
