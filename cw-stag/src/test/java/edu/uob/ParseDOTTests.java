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
        server = new GameServer(actionsFile,entitiesFile);
        //Assert locations are loaded
        assertEquals("cabin", server.getModel().getLocation("cabin").getName());
        assertEquals("forest", server.getModel().getLocation("forest").getName());
        assertEquals("storeroom", server.getModel().getLocation("storeroom").getName());
        assertEquals("cellar", server.getModel().getLocation("cellar").getName());
        //Assert descriptions are loaded
        assertEquals("A log cabin in the woods", server.getModel().getLocation("cabin").getDescription());
        assertEquals("A dark forest", server.getModel().getLocation("forest").getDescription());
        assertEquals("A dusty cellar", server.getModel().getLocation("cellar").getDescription());
        assertEquals("Storage for any entities not placed in the game", server.getModel().getLocation("storeroom").getDescription());
        //Assert entities
        assertEquals("Angry Elf", server.getModel().getLocation("cellar").getCharacter("elf").getDescription());
        assertEquals("cabin", server.getModel().getStartingLocation().getName());
        assertEquals("Magic potion", server.getModel().getLocation("cabin").getArtefact("potion").getDescription());
    }

    @Test
    void parseExtendedDotFile(){
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(actionsFile, entitiesFile);

        //Assert entities of location are loaded
        assertEquals("storeroom", server.getModel().getLocation("storeroom").getName());
        assertEquals("A burly wood cutter", server.getModel().getLocation("storeroom").getCharacter("lumberjack").getDescription());
        assertEquals("cabin", server.getModel().getStartingLocation().getName());
    }
    @Test
    void parsePathsofDotFile(){
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(actionsFile, entitiesFile);
        //Assert paths are loaded
        assertEquals("forest", server.getModel().getLocation("cabin").getPath("forest").getName());
        assertEquals("cabin", server.getModel().getLocation("forest").getPath("cabin").getName());
        assertEquals("cabin", server.getModel().getLocation("cellar").getPath("cabin").getName());
        assertEquals("riverbank", server.getModel().getLocation("forest").getPath("riverbank").getName());
        assertEquals("forest", server.getModel().getLocation("riverbank").getPath("forest").getName());
        assertEquals("riverbank", server.getModel().getLocation("clearing").getPath("riverbank").getName());
    }


}
