package edu.generalpuzzle.main;

import bsh.EvalError;
import bsh.Interpreter;
import edu.generalpuzzle.infra.IGrid;
import edu.generalpuzzle.infra.Parts;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

/** Loads the BeanShell files that define a puzzle without coupling callers to file names. */
public class PuzzleDefinitionLoader {

    private final Path configDirectory;

    public PuzzleDefinitionLoader(Path configDirectory) {
        this.configDirectory = configDirectory;
    }

    public Interpreter newInterpreter(String[] scriptArguments) throws EvalError {
        Interpreter interpreter = new Interpreter();
        interpreter.set("bsh.args", scriptArguments);
        return interpreter;
    }

    public IGrid loadGrid(Interpreter interpreter, String problemId)
            throws EvalError, FileNotFoundException, IOException {
        return requireType(load(interpreter, problemId, "grid"), IGrid.class, problemId, "grid");
    }

    public Parts loadParts(Interpreter interpreter, String problemId)
            throws EvalError, FileNotFoundException, IOException {
        return requireType(load(interpreter, problemId, "parts"), Parts.class, problemId, "parts");
    }

    public Optional<boolean[][]> loadMatches(Interpreter interpreter, String problemId)
            throws EvalError, IOException {
        try {
            return Optional.of(requireType(load(interpreter, problemId, "matches"), boolean[][].class,
                    problemId, "matches"));
        } catch (FileNotFoundException exception) {
            return Optional.empty();
        }
    }

    private Object load(Interpreter interpreter, String problemId, String type)
            throws EvalError, FileNotFoundException, IOException {
        return interpreter.source(configDirectory.resolve(problemId + "_" + type + ".bsh").toString());
    }

    private <T> T requireType(Object value, Class<T> expectedType, String problemId, String definitionType) {
        if (!expectedType.isInstance(value)) {
            String actualType = value == null ? "null" : value.getClass().getName();
            throw new PuzzleException("Puzzle '" + problemId + "' " + definitionType
                    + " definition must return " + expectedType.getSimpleName() + ", not " + actualType);
        }
        return expectedType.cast(value);
    }
}
