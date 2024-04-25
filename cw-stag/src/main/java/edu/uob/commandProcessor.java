package edu.uob;

import java.util.ArrayList;
import java.util.Set;

public class commandProcessor {
    private String fullCommand;

    private Set<String> actionTriggers;

    private String playerName;

    public commandProcessor(String fullCommand){
        this.fullCommand = fullCommand;
    }

    /**
     * Split the command into PlayerName and Command parts
     * Set the playername internally
     * remove any symbols from the command
     * @return the command part of the full command
     */
    public String commandProcessing() {
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


    public String getPlayerName(){
        return this.playerName;
    }
}
