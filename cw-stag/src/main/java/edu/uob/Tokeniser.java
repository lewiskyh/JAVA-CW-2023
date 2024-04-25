package edu.uob;

import java.util.ArrayList;
import java.util.Set;

public class Tokeniser {
    private String fullCommand;

    private Set<String> actionTriggers;

    private String playerName;

    public Tokeniser(String fullCommand){
        this.fullCommand = fullCommand;
    }

    /**
     * Split the command into PlayerName and Command parts
     * Set the playername internally
     * @return the command part of the full command
     */
    public String tokenise() {
        String[] commandFragment = this.fullCommand.split(":", 2);
        this.playerName = commandFragment[0].trim();
        return commandFragment[1].trim();
    }


    public String getPlayerName(){
        return this.playerName;
    }
}
