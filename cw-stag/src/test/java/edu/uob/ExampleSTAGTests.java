package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Paths;
import java.io.IOException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class ExampleSTAGTests {

  private GameServer server;

  // Create a new server _before_ every @Test
  @BeforeEach
  void setup() {
      File entitiesFile = Paths.get("config" + File.separator + "basic-entities.dot").toAbsolutePath().toFile();
      File actionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
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
  }

  // Test that we can pick something up and that it appears in our inventory
  @Test
  void testGet()
  {
      String response;
      sendCommandToServer("simon: get potion");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertTrue(response.contains("potion"), "Did not see the potion in the inventory after an attempt was made to get it");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertFalse(response.contains("potion"), "Potion is still present in the room after an attempt was made to get it");
  }

  // Test that we can goto a different location (we won't get very far if we can't move around the game !)
  @Test
  void testGoto()
  {
      sendCommandToServer("simon: goto forest");
      String response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertTrue(response.contains("key"), "Failed attempt to use 'goto' command to move to the forest - there is no key in the current location");
  }

  // Add more unit tests or integration tests here.

    @Test
    void testLook2() {
        String response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertTrue(response.contains("cabin"), "Did not see the name of the current room in response to look");
        assertTrue(response.contains("log cabin"), "Did not see a description of the room in response to look");
        assertTrue(response.contains("magic potion"), "Did not see a description of artifacts in response to look");
        assertTrue(response.contains("wooden trapdoor"), "Did not see description of furniture in response to look");
        assertTrue(response.contains("forest"), "Did not see available paths in response to look");

    }

    @Test
    void testbadGoto(){
      //No path from cabin to cellar
      String response = sendCommandToServer("simon: goto cellar");
      assertTrue(response.contains("ERROR"), "No path from cabin to cellar");
      sendCommandToServer("simon: goto forest");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertTrue(response.contains("a dark forest"), "Failed attempt to use 'goto' command to move to the forest - there is no key in the current location");
      response = sendCommandToServer("simon: goto cellar");
        assertTrue(response.contains("ERROR"), "No path from forest to cellar");
      sendCommandToServer("simon: goto cabin");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertTrue(response.contains("magic potion"), "Failed attempt to use 'goto' command to move to the forest - there is no key in the current location");
    }

    @Test

    void testComplexGetDrop(){
      //Can only get one item
        String response = sendCommandToServer("simon: get potion and axe");
        assertTrue(response.contains("ERROR"), "Invalid get command, cannot get two items at once");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("potion"), "Invalid get command, potion should not be in the inventory");
      assertFalse(response.contains("axe"), "Invalid get command, axe should not be in the inventory");
      sendCommandToServer("simon: get potion");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertTrue(response.contains("potion"), "Did not see the potion in the inventory after an attempt was made to get it");
      sendCommandToServer("simon: drop potion");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("potion"), "Potion is still present in the room after an attempt was made to get it");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertTrue(response.contains("magic potion"), "Did not see a description of artifacts in response to look");
    }

    @Test

    void testComplexGetDrop2(){
        sendCommandToServer("simon: get axe");
        sendCommandToServer("simon: get potion");
        String response = sendCommandToServer("simon: drop axe and potion");
        assertTrue(response.contains("ERROR"), "Invalid drop command, cannot drop two items at once");
        response = sendCommandToServer("simon: inv");
        assertTrue(response.contains("potion"), "Potion should be in the inventory after the invalid drop command");
        assertTrue(response.contains("axe"), "Axe should be in the inventory after the invalid drop command");
        sendCommandToServer("simon: drop axe");
        response = sendCommandToServer("simon: inv");
        response = response.toLowerCase();
        assertFalse(response.contains("axe"), "Axe should not be in the inventory after the drop command");
    }

    //Test the Basic action from now on
    @Test

    void testBasicProduceConsume1(){
        sendCommandToServer("simon: get axe");
        sendCommandToServer("simon: goto forest");
        //tested - cut tree, cut with axe, cut tree with axe
        String response = sendCommandToServer("simon: cut with axe");
        assertTrue(response.contains("You cut down the tree with the axe"));
        response = sendCommandToServer("simon: look");
        //Tree gone, log is produced
        assertFalse(response.contains("tree"));
        assertTrue(response.contains("log"));
        response = sendCommandToServer("simon: inv");
        assertFalse(response.contains("log"));
        sendCommandToServer("simon: get log");
        response = sendCommandToServer("simon: inv");
        assertTrue(response.contains("log"));
    }

    @Test
    void testBasicProduceConsume2(){
        sendCommandToServer("simon: get POTION");
        sendCommandToServer("simon: get axe");
        sendCommandToServer("simon: goto forest");
        //tested - cut tree, cut with axe, cut tree with axe
        //assertThrows(RuntimeException.class, () -> sendCommandToServer("simon: cut tree with axe"));
        sendCommandToServer("simon: get key");
        String response = sendCommandToServer("simon: inv");
        assertTrue(response.contains("key"));
        assertTrue(response.contains("axe"));
        //Trapdoor is not present in forest
        response = sendCommandToServer("simon: unlock trapdoor");
        assertTrue(response.contains("ERROR"));
        sendCommandToServer("simon: goto cabin");
        response = sendCommandToServer("simon: trapdoor unlock");
        assertTrue(response.contains("You unlock the trapdoor and see steps leading down into a cellar"));
        response = sendCommandToServer("simon: look");
        //Trapdoor wont be consumed
        assertTrue(response.contains("trapdoor"));
        response = sendCommandToServer("simon: inv");
        assertFalse(response.contains("key"));
        //Assert that trapdoor is unlocked and that can go to cellar
        sendCommandToServer("simon: goto cellar");
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("elf"));
        sendCommandToServer("simon: hit elf");
        sendCommandToServer("simon: hit elf");
        response = sendCommandToServer("simon: health");
        assertTrue(response.contains("1"));
        sendCommandToServer("simon: drink potion");
        response = sendCommandToServer("simon: health");
        assertTrue(response.contains("2"));
        response = sendCommandToServer("simon: inv");
        assertFalse(response.contains("potion"));
        sendCommandToServer("simon: hit elf");
        response = sendCommandToServer("simon: hit elf");
        assertTrue(response.contains("LOSER"));
        //player is dead and needs to restart the game in starting position
        response = sendCommandToServer("simon: health");
        assertTrue(response.contains("3"));
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("cabin"));
        response = sendCommandToServer("simon: inv");
        assertFalse(response.contains("axe"));
        //Player should lose all items when HP drops to zero and items remain at dead scene
        //Cellar was opened before player died. Should remain open
        sendCommandToServer("simon: goto cellar");
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("axe"));

    }

    @Test
    void testHealth(){
      String response = sendCommandToServer("simon: health");
      assertTrue(response.contains("3"));
      sendCommandToServer("simon: get potion");
      sendCommandToServer("simon: drink potion");
      response = sendCommandToServer("simon: health");
      //Max health is 3 - can drink potion but no effect
      assertTrue(response.contains("3"));
      assertFalse(response.contains("4"));
    }



}
