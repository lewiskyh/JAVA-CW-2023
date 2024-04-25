package edu.uob;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.File;

import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import com.alexmerz.graphviz.objects.Edge;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import static org.junit.jupiter.api.Assertions.*;

public class PraseXMLTests {

    private GameServer server;

    @BeforeEach
    void setUp() throws ParserConfigurationException, IOException, SAXException {

        File entitiesFile = Paths.get("config" + File.separator + "basic-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
        this.server = new GameServer(entitiesFile,actionsFile);
    }


    //Assert triggers are loaded to the action hashmap as Key
    @Test
    void testReadActionFile() {
        Map<String, Set<GameAction>> actions = server.getModel().getActionHashMap();
        assertEquals(9, actions.size());
        assertTrue(actions.containsKey("open"));
        assertTrue(actions.containsKey("unlock"));
        assertTrue(actions.containsKey("chop"));
        assertTrue(actions.containsKey("cut"));
        assertTrue(actions.containsKey("cutdown"));
        assertTrue(actions.containsKey("drink"));
        assertTrue(actions.containsKey("fight"));
        assertTrue(actions.containsKey("hit"));
        assertTrue(actions.containsKey("attack"));
    }

    //Assert the content of the action hashmap
    @Test
    void testActionHashMapContent() {
        Map<String, Set<GameAction>> actions = server.getModel().getActionHashMap();
        assertEquals(1, actions.get("open").size());
        assertEquals(1, actions.get("unlock").size());
        assertEquals(1, actions.get("chop").size());
        for (GameAction action : actions.get("open")) {
            assertTrue(action.getSubjects().contains("key"));
            assertTrue(action.getSubjects().contains("trapdoor"));
            assertEquals(2, action.getSubjects().size());
            assertTrue(action.getConsumed().contains("key"));
            assertEquals(1, action.getConsumed().size());
            assertTrue(action.getProduced().contains("cellar"));
            assertTrue(action.getTriggers().contains("open"));
            assertTrue(action.getTriggers().contains("unlock"));
            assertEquals(2, action.getTriggers().size());
            assertEquals("You unlock the trapdoor and see steps leading down into a cellar", action.getNarration());
        }
        for (GameAction action : actions.get("unlock")) {
            assertTrue(action.getSubjects().contains("key"));
            assertTrue(action.getSubjects().contains("trapdoor"));
            assertEquals(2, action.getSubjects().size());
            assertTrue(action.getConsumed().contains("key"));
            assertEquals(1, action.getConsumed().size());
            assertTrue(action.getProduced().contains("cellar"));
            assertEquals(1, action.getProduced().size());
            assertTrue(action.getTriggers().contains("open"));
            assertTrue(action.getTriggers().contains("unlock"));
        }
        for (GameAction action : actions.get("chop")) {
            assertTrue(action.getSubjects().contains("axe"));
            assertTrue(action.getSubjects().contains("tree"));
            assertTrue(action.getConsumed().contains("tree"));
            assertTrue(action.getProduced().contains("log"));
            assertTrue(action.getTriggers().contains("chop"));
            assertTrue(action.getTriggers().contains("cut"));
            assertTrue(action.getTriggers().contains("cutdown"));
        }
        for (GameAction action : actions.get("fight")) {
            assertTrue(action.getSubjects().contains("elf"));
            assertTrue(action.getConsumed().contains("health"));
            assertTrue(action.getProduced().isEmpty());
            assertEquals(3,action.getTriggers().size());
        }

    }




}
