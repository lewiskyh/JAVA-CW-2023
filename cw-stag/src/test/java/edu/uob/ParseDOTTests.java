package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.File;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import com.alexmerz.graphviz.objects.Edge;

import static org.junit.jupiter.api.Assertions.*;

final class ParseDOTTests {

    private GameServer server;

    @Test
    void parseBasicDotFile(){
        File entitiesFile = Paths.get("config" + File.separator + "basic-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
        //Assert locations are loaded
        assertEquals("cabin", server.getLocation("cabin").getName());
        assertEquals("forest", server.getLocation("forest").getName());
        assertEquals("storeroom", server.getLocation("storeroom").getName());
        assertEquals("cellar", server.getLocation("cellar").getName());
        //Assert descriptions are loaded
        assertEquals("A log cabin in the woods", server.getLocation("cabin").getDescription());
        assertEquals("A dark forest", server.getLocation("forest").getDescription());
        assertEquals("Storage for any entities not placed in the game", server.getLocation("storeroom").getDescription());
        assertEquals("A dusty cellar", server.getLocation("cellar").getDescription());
        assertEquals("A log cabin in the woods", server.getLocation("cabin").getDescription());
        //Assert entities(artefact, furniture, character, paths) are loaded in locations
        assertEquals("A razor sharp axe", server.getLocation("cabin").getArtefact("axe").getDescription());
        assertEquals("Magic potion", server.getLocation("cabin").getArtefact("potion").getDescription());
        assertEquals("Brass key", server.getLocation("forest").getArtefact("key").getDescription());
        assertEquals("A heavy wooden log", server.getLocation("storeroom").getArtefact("log").getDescription());
        assertEquals("Angry Elf", server.getLocation("cellar").getCharacter("elf").getDescription());
        assertEquals("Wooden trapdoor", server.getLocation("cabin").getFurniture("trapdoor").getDescription());
        assertEquals("cabin", server.getStartingLocation().getName());
    }

    @Test
    void parseExtendedDotFile(){
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);

        //Assert entities of location are loaded
        assertEquals("storeroom", server.getLocation("storeroom").getName());
        assertEquals("A burly wood cutter", server.getLocation("storeroom").getCharacter("lumberjack").getDescription());
        assertEquals("cabin", server.getStartingLocation().getName());
    }
    @Test
    void parsePathsofDotFile(){
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
        //Assert paths are loaded
        assertEquals("forest", server.getLocation("cabin").getPath("forest").getName());
        assertEquals("cabin", server.getLocation("forest").getPath("cabin").getName());
        assertEquals("cabin", server.getLocation("cellar").getPath("cabin").getName());
        assertEquals("riverbank", server.getLocation("forest").getPath("riverbank").getName());
        assertEquals("forest", server.getLocation("riverbank").getPath("forest").getName());
        assertEquals("riverbank", server.getLocation("clearing").getPath("riverbank").getName());
    }


}
