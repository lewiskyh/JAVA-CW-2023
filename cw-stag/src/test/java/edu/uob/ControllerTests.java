package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ControllerTests {
    GameModel model;
    GameControl control;

    @BeforeEach

    public void setup() {
        File entitiesFile = Paths.get("config" + File.separator + "basic-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "extended-actions.xml").toAbsolutePath().toFile();
        model = new GameModel (entitiesFile, actionsFile);
        control = new GameControl(model);
    }

    @Test
    public void testCommandPreprocessing() {
        assertEquals("look", this.control.preprocessCommand("simon: look"));
        assertEquals("simon", this.control.getCurrentPlayer());
        assertEquals("simon", this.control.getModel().getPlayer("simon").getName());
        assertEquals("goto", this.control.preprocessCommand("lewis: GOTO storeroom"));
        assertEquals("lewis", this.control.getCurrentPlayer());
        assertEquals("lewis", this.control.getModel().getPlayer("lewis").getName());
        assertEquals("get", this.control.preprocessCommand("simon: get key"));
        assertEquals("cut", this.control.preprocessCommand("simon: cut tree"));
        assertEquals("cut down", this.control.preprocessCommand("simon: cut down the tree"));
        //Assert error when no. of trigger used is not 1
        assertThrows(RuntimeException.class, () -> this.control.preprocessCommand("simon: cut down goto"));
        assertThrows(RuntimeException.class, () -> this.control.preprocessCommand("simon: key!"));
        assertThrows(RuntimeException.class, () -> this.control.preprocessCommand("simon: cut and goto cellar"));

    }
}
