package edu.generalpuzzle.main;

import edu.generalpuzzle.infra.engines.EngineType;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Reads and validates solver settings from a Java properties file. */
public class SolverOptionsLoader {

    private final Path propertiesFile;

    public SolverOptionsLoader(Path propertiesFile) {
        this.propertiesFile = propertiesFile;
    }

    public SolverOptions load() throws IOException {
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(propertiesFile)) {
            properties.load(reader);
        }
        return from(properties);
    }

    public SolverOptions from(Properties properties) {
        SolverOptions defaults = SolverOptions.defaults();
        int threads = integer(properties, "THREADS", defaults.threads());
        if (threads < 0) {
            throw new IllegalArgumentException("THREADS must be zero or greater");
        }
        return new SolverOptions(
                EngineType.fromLegacyId(integer(properties, "ENGINE_TYPE", defaults.engineType().legacyId())),
                integer(properties, "DL_SPLITS", defaults.dlSplits()),
                bool(properties, "FULL_OUTPUT", defaults.fullOutput()),
                bool(properties, "GENERATE_BY_ALL", defaults.generateByAll()),
                bool(properties, "GRAPH_FOR_ALL", defaults.graphForAll()),
                bool(properties, "DL_SIZE_HEURISTIC", defaults.sizeHeuristic()),
                bool(properties, "INTERNAL_VIEWER", defaults.internalViewer()),
                bool(properties, "STRANDED_HEURISTIC", defaults.strandedHeuristic()),
                bool(properties, "AUTO_GRAPH_IT", defaults.autoGraphIt()),
                bool(properties, "AUTO_DEBUG", defaults.autoDebug()),
                threads);
    }

    private int integer(Properties properties, String key, int defaultValue) {
        try {
            return Integer.parseInt(value(properties, key, Integer.toString(defaultValue)));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid " + key + " value", exception);
        }
    }

    private boolean bool(Properties properties, String key, boolean defaultValue) {
        return Boolean.parseBoolean(value(properties, key, Boolean.toString(defaultValue)));
    }

    private String value(Properties properties, String key, String defaultValue) {
        String rawValue = properties.getProperty(key, defaultValue).split(" ")[0];
        return rawValue.replaceAll("[^A-Za-z0-9]", "");
    }
}
