package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExtendedFileSTAGTests {
    private GameServer server;

    // Create a new server _before_ every @Test
    @BeforeEach
    void setup() {
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "extended-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
    }

    String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    // A lot of tests will probably check the game state using 'look' - so we better make sure 'look' works well !
    @Test
    void testLook() {
        String response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertTrue(response.contains("cabin"), "Did not see the name of the current room in response to look");
        assertTrue(response.contains("log cabin"), "Did not see a description of the room in response to look");
        assertTrue(response.contains("magic potion"), "Did not see a description of artifacts in response to look");
        assertTrue(response.contains("wooden trapdoor"), "Did not see description of furniture in response to look");
        assertTrue(response.contains("forest"), "Did not see available paths in response to look");
        assertTrue(response.contains("a silver coin"), "Did not see a description of coin in response to look");
    }

    // Test that we can pick something up and that it appears in our inventory
    @Test
    void testGet()
    {
        String response;
        sendCommandToServer("simon: get coin");
        response = sendCommandToServer("simon: inv");
        response = response.toLowerCase();
        assertTrue(response.contains("coin"), "Did not see the coin in the inventory after an attempt was made to get it");
        response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertFalse(response.contains("coin"), "coin is still present in the room after an attempt was made to get it");
    }

    // Test that we can goto a different location (we won't get very far if we can't move around the game !)
    @Test
    void testGoto()
    {
        sendCommandToServer("simon: goto forest");
        String response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertTrue(response.contains("key"), "Failed attempt to use 'goto' command to move to the forest - there is no key in the current location");
        sendCommandToServer("simon: goto cabin");
        response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertTrue(response.contains("axe"), "Failed attempt to use 'goto' command to move to the cabin - there is no axe in the current location");
    }
    // Test that we can cut down tree
    @Test

    void testCutDown(){
        sendCommandToServer("simon: get axe");
        sendCommandToServer("simon: goto forest");
        assertThrows(RuntimeException.class, () -> sendCommandToServer("simon: cutdown tree"), "cutdown is not valid trigger here");
        String response = sendCommandToServer("simon: look");
        assertFalse(response.contains("log"), "Faile to cut down tree and produce log");
        sendCommandToServer("simon: cut down tree");
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("log"), "Failed to cut down tree and produce log");
    }

    //Test series of production and consumption and death

    @Test

    void testComplex(){
        sendCommandToServer("simon: get coin");
        sendCommandToServer("simon: get axe");
        sendCommandToServer("simon: get potion");
        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: get key");
        sendCommandToServer("simon: goto cabin");
        sendCommandToServer("simon: trapdoor unlock");
        assertThrows(RuntimeException.class, () -> sendCommandToServer("simon: pay elf"), "elf is in cellar");
        sendCommandToServer("simon: goto cellar");
        assertThrows(RuntimeException.class, () -> sendCommandToServer("simon: pay"), "missing subject in pay command");
        sendCommandToServer("simon: pay my coin");
        String response = sendCommandToServer("simon: look");
        assertTrue(response.contains("shovel"), "Failed to pay elf and produce shovel");
        sendCommandToServer("simon: get shovel");
        response = sendCommandToServer("simon: look");
        assertFalse(response.contains("shovel"), "Already got shovel, it should not appear in the location");
        response= sendCommandToServer("simon: inv");
        assertTrue(response.contains("shovel"), "Failed to get shovel in inventory");
        sendCommandToServer("simon: goto cabin");sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: cut down tree");
        response = sendCommandToServer("simon: get log");
        assertTrue(response.contains("log"), "Failed to get log");
        sendCommandToServer("simon: goto riverbank");
        assertThrows(RuntimeException.class, () -> sendCommandToServer("simon: goto clearing"), "Clearing is not accessible");
        sendCommandToServer("simon: bridge with log");
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("clearing"), "Failed to create new path to clearing");
        sendCommandToServer("simon: goto clearing");
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("ground"), "Failed to move to clearing and see ground");
        sendCommandToServer("simon: dig ground");
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("gold"), "Failed to dig ground and produce gold");
        assertTrue(response.contains("hole"), "Failed to dig ground and produce hole");
        assertThrows(RuntimeException.class, () -> sendCommandToServer("simon: get hole"), "hole is a furniture");
        response = sendCommandToServer("simon: inv");
        assertFalse(response.contains("gold"), "gold should not be in inventory");
        sendCommandToServer("simon: get gold");
        sendCommandToServer("simon: goto riverbank"); sendCommandToServer("simon: goto forest");sendCommandToServer("simon: goto cabin");
        assertThrows(RuntimeException.class, () -> sendCommandToServer("simon: unlock trapdoor"), "key is used already!");
        sendCommandToServer("simon: goto cellar");
        sendCommandToServer("simon: attack elf to dead");
        sendCommandToServer("simon: attack elf to steal money");
        response = sendCommandToServer("simon: attack elf regardless of my HP");
        assertTrue(response.contains("dead"), "You are dead already");
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("cabin"), "You restart at cabin");
        response = sendCommandToServer("simon: inv");
        assertFalse(response.contains("coin"), "coin should not be in inventory");
        assertFalse(response.contains("axe"), "axe should not be in inventory");
        sendCommandToServer("simon: goto cellar");
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("axe"), "Axe is at the location you died");
        assertTrue(response.contains("potion"), "Potion is at the location you died");
        sendCommandToServer("simon: get axe");
        response = sendCommandToServer("simon: inv");
        assertTrue(response.contains("axe"), "Failed to get axe in inventory");
        response = sendCommandToServer("simon: health");
        assertTrue(response.contains("3"), "You should have restarted with 3 health");
    }

    //Test another series - producing character
    @Test

    void testComplex2(){
        sendCommandToServer("simon: get axe");
        sendCommandToServer("simon: get coin");
        sendCommandToServer("simon: goto forest"); sendCommandToServer("simon: goto riverbank");
        sendCommandToServer("simon: get horn");
        String response = sendCommandToServer("simon: inv");
        assertTrue(response.contains("horn"), "Failed to get horn in inventory");
        response = sendCommandToServer("simon: blow horn");
        assertTrue(response.contains("You blow the horn and as if by magic, a lumberjack appears !"), "Failed to blow horn and produce unicorn");
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("lumberjack"), "Failed to blow horn and produce unicorn");
        assertThrows(RuntimeException.class, () -> sendCommandToServer("simon: get lumberjack"), "lumberjack is a character! Not an artefact!");
        response = sendCommandToServer("simon: inv");
        assertTrue(response.contains("horn"), "horn should not be consumed");
    }

    //Test multiple players
    @Test

    void testMultiPlayer(){
        sendCommandToServer("simon: get coin");
        sendCommandToServer("sion: get axe");
        String response = sendCommandToServer("neill: look");
        assertTrue(response.contains("simon"), "simon is also in the starting location");
        assertTrue(response.contains("sion"), "sion is also in the starting location");
        assertThrows(RuntimeException.class, () -> sendCommandToServer("neill: get coin"), "coin is already taken by simon");
        assertThrows(RuntimeException.class, () -> sendCommandToServer("neill: get axe"), "axe is already taken by sion");
        assertThrows(RuntimeException.class, () -> sendCommandToServer("MSC213: get coin"), "coin is already taken by simon");
        sendCommandToServer("evil's msc-student: get potion");
        response = sendCommandToServer("neill: look");
        assertTrue(response.contains("evil's msc-student"), "evil's msc-student is also in the starting location");
        assertTrue(response.contains("simon"), "simon is also in the starting location");
        assertTrue(response.contains("sion"), "sion is also in the starting location");
        assertFalse(response.contains("MSC213"), "MSC213: get coin was invalid - so player not in the game yet");
        sendCommandToServer("neill: goto forest");
        assertThrows(RuntimeException.class, () -> sendCommandToServer("neill: cut down tree"), "neill does not have axe");
        sendCommandToServer("neill: get key"); sendCommandToServer("neill: goto cabin");
        sendCommandToServer("sion: drop axe");
        sendCommandToServer("neill: get axe");
        response = sendCommandToServer("neill: inv");
        assertTrue(response.contains("axe")); assertTrue(response.contains("key"));
        sendCommandToServer("neill: unlock trapdoor"); sendCommandToServer("neill: goto cellar");
        sendCommandToServer("neill: attack elf to death");
        sendCommandToServer("neill: attack elf to death");
        response= sendCommandToServer("neill: health");
        assertTrue(response.contains("1"), "neill should have 1 health");
        sendCommandToServer("evil's msc-student: goto cellar");
        sendCommandToServer("evil's msc-student: drop potion for neill");
        sendCommandToServer("neill: get potion");
        sendCommandToServer("neill: potion DRINk ASAP");
        response = sendCommandToServer("neill: health");
        assertTrue(response.contains("2"), "neill should have 3 health");
        sendCommandToServer("neill: attack elf to death");
        sendCommandToServer("neill: attack elf to death");
        response = sendCommandToServer("evil's msc-student: look");
        assertTrue(response.contains("axe"), "axe should be at the location neill died");
        assertFalse(response.contains("neill"), "neill should be at cabin");
        sendCommandToServer("evil's msc-student: get axe");
        response = sendCommandToServer("evil's msc-student: inv");
        assertTrue(response.contains("axe"), "Failed to get axe in inventory");
        assertThrows(RuntimeException.class, () -> sendCommandToServer("evil's msc-student: goto attack elf"), "2 valid triggers");
        sendCommandToServer("evil's msc-student: goto cabin");
        response = sendCommandToServer("evil's msc-student: look");
        assertTrue(response.contains("neill"), "neill restarted");
        assertThrows(RuntimeException.class, () -> sendCommandToServer("student@ uob: get axe"), "invalid username");

    }

    @Test

    void testConsumedInStoreroom(){
        sendCommandToServer("simon: GET AXE");
        sendCommandToServer("simon: GOTO forest");
        sendCommandToServer("simon: tree cut down");
        String response = sendCommandToServer("simon: look");
        assertFalse(response.contains("tree"));
        GameModel model = server.getModel();
        List<Furniture> furnitureList = model.getLocation("storeroom").getFurnitureList();
        ArrayList<String> furnitureNames= new ArrayList<>();
        for (Furniture furniture : furnitureList){
            furnitureNames.add(furniture.getName());
        }
        assertTrue(furnitureNames.contains("tree"));


    }

    //Test consumed location?
    


}
