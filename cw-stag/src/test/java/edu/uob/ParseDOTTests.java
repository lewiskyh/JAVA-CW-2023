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
        assertEquals("cabin", server.getEntity("cabin").getName());
        assertEquals("forest", server.getEntity("forest").getName());
        assertEquals("storeroom", server.getEntity("storeroom").getName());
        assertEquals("cellar", server.getEntity("cellar").getName());
        //Assert descriptions are loaded
        assertEquals("A log cabin in the woods", server.getEntity("cabin").getDescription());
        assertEquals("A dark forest", server.getEntity("forest").getDescription());
        assertEquals("Storage for any entities not placed in the game", server.getEntity("storeroom").getDescription());
        assertEquals("A dusty cellar", server.getEntity("cellar").getDescription());
        assertEquals("A log cabin in the woods", server.getLocation("cabin").getDescription());
        //Assert entities(artefact, furniture, character, paths) are loaded in locations
        Artefact artefact = new Artefact("axe", "A sharp axe");
        assertNotEquals(null, server.getLocation("cabin"));
        //Bugs in parseContentofLocation - cannot parse the contents after "Type"


    }


}
