package edu.generalpuzzle.main;

import edu.generalpuzzle.infra.engines.EngineType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SolverOptionsLoaderTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void loadsAllConfiguredValues() throws Exception {
        Path propertiesFile = temporaryDirectory.resolve("solver.properties");
        Files.writeString(propertiesFile, """
                ENGINE_TYPE=2
                DL_SPLITS=3
                FULL_OUTPUT=true
                GENERATE_BY_ALL=true
                GRAPH_FOR_ALL=true
                DL_SIZE_HEURISTIC=false
                INTERNAL_VIEWER=true
                STRANDED_HEURISTIC=false
                AUTO_GRAPH_IT=false
                AUTO_DEBUG=true
                THREADS=4
                """);

        SolverOptions options = new SolverOptionsLoader(propertiesFile).load();

        assertEquals(EngineType.ITERATIVE, options.engineType());
        assertEquals(3, options.dlSplits());
        assertTrue(options.fullOutput());
        assertTrue(options.generateByAll());
        assertTrue(options.graphForAll());
        assertFalse(options.sizeHeuristic());
        assertTrue(options.internalViewer());
        assertFalse(options.strandedHeuristic());
        assertFalse(options.autoGraphIt());
        assertTrue(options.autoDebug());
        assertEquals(4, options.threads());
    }

    @Test
    void appliesDefaultsForOmittedValues() {
        SolverOptions options = new SolverOptionsLoader(temporaryDirectory.resolve("unused"))
                .from(new Properties());

        assertEquals(SolverOptions.defaults(), options);
    }

    @Test
    void rejectsInvalidValues() {
        Properties properties = new Properties();
        properties.setProperty("ENGINE_TYPE", "99");

        assertThrows(IllegalArgumentException.class,
                () -> new SolverOptionsLoader(temporaryDirectory.resolve("unused")).from(properties));
    }
}
