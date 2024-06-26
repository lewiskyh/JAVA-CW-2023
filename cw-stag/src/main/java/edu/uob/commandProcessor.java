package edu.uob;

import java.util.ArrayList;
import java.util.Set;

public class commandProcessor {
    private String fullCommand;

    private String playerName;

    //Constructor for commandProcessor

    public commandProcessor(String fullCommand){
        this.fullCommand = fullCommand;
    }

    /**
     * Split the command into PlayerName and Command parts
     * Set the playername internally
     * remove any symbols from the actual command (after ":" )
     * @return the command part of the full command
     */
    public String commandCleaning() {
        String[] commandFragment = this.fullCommand.split(":", 2);
        this.playerName = commandFragment[0].trim();
        String [] actualCommand = commandFragment[1].split(" ");
        for (int i = 0; i < actualCommand.length; i++) {
            actualCommand[i] = actualCommand[i].replaceAll("[^a-zA-Z]", "");
        }
        //build the command string
        StringBuilder commandString = new StringBuilder();
        for (String word : actualCommand) {
            if(!word.isBlank()){
                commandString.append(word).append(" ");
            }
        }
        return commandString.toString().trim();

    }

    /**
     * Get player name
     * @return the player name in string
     */
    public String getPlayerName(){
        return this.playerName;
    }
}
