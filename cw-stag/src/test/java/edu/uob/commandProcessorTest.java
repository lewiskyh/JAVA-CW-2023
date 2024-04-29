package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class commandProcessorTest {

    @Test

    void testSimple(){
        // Create a new Tokeniser object before every test
        commandProcessor processor= new commandProcessor("simon: look");
        processor.commandCleaning();
        assertEquals("simon", processor.getPlayerName());
        assertEquals("look", processor.commandCleaning());
    }

    @Test
    void testComplex() {
        commandProcessor processor= new commandProcessor("simon: LOOK at the POTION in the room");
        assertEquals("LOOK at the POTION in the room", processor.commandCleaning());
    }

    @Test
    void testComplex1(){
        commandProcessor processor= new commandProcessor("simon: LOOK at 123 !* POTION in the room.");
        processor.commandCleaning();
        assertEquals("simon", processor.getPlayerName());
        assertEquals("LOOK at POTION in the room", processor.commandCleaning());
    }



}
