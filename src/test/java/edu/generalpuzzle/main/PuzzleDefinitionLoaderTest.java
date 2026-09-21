package edu.generalpuzzle.main;

import bsh.Interpreter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PuzzleDefinitionLoaderTest {

    @TempDir
    Path configDirectory;

    @org.junit.jupiter.api.BeforeEach
    void disableLegacyTraceOutput() {
        PuzzleException.setAddTrace(false);
    }

    @Test
    void loadsTypedDefinitionsAndOptionalMatches() throws Exception {
        Files.writeString(configDirectory.resolve("sample_grid.bsh"),
                "return new edu.generalpuzzle.infra.IGrid();");
        Files.writeString(configDirectory.resolve("sample_parts.bsh"),
                "return new edu.generalpuzzle.infra.Parts();");
        Files.writeString(configDirectory.resolve("sample_matches.bsh"),
                "return new boolean[][] { { true, false }, { false, true } };");
        PuzzleDefinitionLoader loader = new PuzzleDefinitionLoader(configDirectory);
        Interpreter interpreter = loader.newInterpreter(new String[] {"4", "5"});

        assertNotNull(loader.loadGrid(interpreter, "sample"));
        assertNotNull(loader.loadParts(interpreter, "sample"));
        boolean[][] matches = loader.loadMatches(interpreter, "sample").orElseThrow();
        assertArrayEquals(new boolean[] {true, false}, matches[0]);
    }

    @Test
    void treatsAMissingMatchesScriptAsOptional() throws Exception {
        PuzzleDefinitionLoader loader = new PuzzleDefinitionLoader(configDirectory);

        assertFalse(loader.loadMatches(loader.newInterpreter(new String[0]), "missing").isPresent());
    }

    @Test
    void rejectsDefinitionWithWrongReturnType() throws Exception {
        Files.writeString(configDirectory.resolve("invalid_grid.bsh"), "return \"not a grid\";");
        PuzzleDefinitionLoader loader = new PuzzleDefinitionLoader(configDirectory);

        PuzzleException exception = assertThrows(PuzzleException.class,
                () -> loader.loadGrid(loader.newInterpreter(new String[0]), "invalid"));

        assertTrue(exception.getMessage()
                .startsWith("Puzzle 'invalid' grid definition must return IGrid, not java.lang.String"));
    }
}
