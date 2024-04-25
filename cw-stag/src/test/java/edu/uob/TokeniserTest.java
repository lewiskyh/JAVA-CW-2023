package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TokeniserTest {

    @Test

    void testSimple(){
        // Create a new Tokeniser object before every test
        Tokeniser tokeniser = new Tokeniser("simon: look");
        tokeniser.tokenise();
        assertEquals("simon", tokeniser.getPlayerName());
        assertEquals("look", tokeniser.tokenise());
    }

    @Test
    void testComplex() {
        Tokeniser tokeniser = new Tokeniser("simon: LOOK at the POTION in the room");
        assertEquals("LOOK at the POTION in the room", tokeniser.tokenise());
    }

}
