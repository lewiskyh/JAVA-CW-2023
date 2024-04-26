package edu.uob;

public class LookCommand extends Command{

    public LookCommand(GameModel model, String playerName, String command){
        super(model, playerName, command);
    }

    public void execute(){
        this.player.getCurrentLocation().getLookDescription();
    }
}
