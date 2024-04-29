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

public class BasicCommandTests {

    GameServer server;

    @BeforeEach
    void setUp() {
            File entitiesFile = Paths.get("config" + File.separator + "basic-entities.dot").toAbsolutePath().toFile();
            File actionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
            server = new GameServer(entitiesFile, actionsFile);
    }

    String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
                "Server took too long to respond (probably stuck in an infinite loop)");
    }
    @Test
    void testSimpleWithSpaceSymbol() {
        String response = sendCommandToServer("simon:     -look-");
        assertTrue(response.contains("cabin"));

        sendCommandToServer("simon:      goto       forest       ");
        response = sendCommandToServer("simon:     look       ");
        assertTrue(response.contains("forest"));

        sendCommandToServer("simon:      get   the    key please!      ");
        response = sendCommandToServer("simon:     inventory!!!!       ");
        assertTrue(response.contains("key"));

    }

    @Test

    void testPartial(){
        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: get key ");
        sendCommandToServer("simon: goto cabin");
        //Assert runtime error message return
        String response = sendCommandToServer("simon: open");
        assertTrue(response.contains("ERROR"));
        sendCommandToServer("simon: open with key");
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("cellar"));
    }

    @Test

    void testMissingSubject(){
        assertTrue(sendCommandToServer("simon: goto").contains("ERROR"));
        assertTrue(sendCommandToServer("simon: get").contains("ERROR"));
        assertTrue(sendCommandToServer("simon: drop").contains("ERROR"));
        assertTrue(sendCommandToServer("simon: drink").contains("ERROR"));
        assertTrue(sendCommandToServer("simon: look inventory").contains("ERROR"));
    }

    @Test

    void testMultiActions(){
        assertTrue(sendCommandToServer("simon: get axe and potion").contains("ERROR"));
    }

    @Test
    void testMultiTriggers(){
        assertTrue(sendCommandToServer("simon: goto get inv inventory").contains("ERROR"));
        assertTrue(sendCommandToServer("simon: goto get key").contains("ERROR"));
        assertTrue(sendCommandToServer("simon: goto forest get key").contains("ERROR"));
        assertTrue(sendCommandToServer("simon: look health").contains("ERROR"));
    }
    @Test
    void testBoundary(){
        assertTrue(sendCommandToServer("simon: look hea forest get key").contains("ERROR"));
        assertTrue(sendCommandToServer("simon: goto forest get key goto cabin").contains("ERROR"));
        assertTrue(sendCommandToServer("simon: goto forest get key goto cabin look").contains("ERROR"));
        assertTrue(sendCommandToServer("simon: goto forest get key goto cabin look inventory").contains("ERROR"));
    }

    @Test

    void testLookDetails(){
        String response = sendCommandToServer("simon: look");
        assertTrue(response.contains("cabin"));
        assertTrue(response.contains("A log cabin in the woods"));
        assertTrue(response.contains("axe"));
        assertTrue(response.contains("potion"));
        assertTrue(response.contains("trapdoor"));

    }

    }
