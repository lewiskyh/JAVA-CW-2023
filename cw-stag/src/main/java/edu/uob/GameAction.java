package edu.uob;

import java.util.ArrayList;

public class GameAction {
    private ArrayList<String> produced;
    private ArrayList<String> consumed;
    private ArrayList<String> subjects;
    private ArrayList<String> triggers;
    private String narration;
    public GameAction(){
        this.produced = new ArrayList<>();
        this.consumed = new ArrayList<>();
        this.subjects = new ArrayList<>();
        this.triggers = new ArrayList<>();
    }

    public void addProduced(String entityName){
        this.produced.add(entityName);
    }

    public void addConsumed(String entityName){
        this.consumed.add(entityName);
    }

    public void addSubject(String entityName){
        this.subjects.add(entityName);
    }

    public void addTrigger(String entityName){
        this.triggers.add(entityName);
    }

    public void addNarration(String narration){
        this.narration = narration;
    }



}
