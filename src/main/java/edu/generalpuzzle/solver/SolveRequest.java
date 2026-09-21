package edu.generalpuzzle.solver;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** A transport-independent request to solve one configured puzzle. */
public record SolveRequest(String problemId, List<String> dimensions) {

    public SolveRequest {
        problemId = Objects.requireNonNull(problemId, "problemId must not be null").trim();
        if (problemId.isEmpty()) {
            throw new IllegalArgumentException("problemId must not be blank");
        }
        dimensions = List.copyOf(dimensions == null ? List.of() : dimensions);
    }

    public String[] toArguments() {
        List<String> arguments = new ArrayList<>();
        arguments.add(problemId);
        dimensions.stream()
                .filter(dimension -> dimension != null && !dimension.isBlank())
                .forEach(arguments::add);
        return arguments.toArray(String[]::new);
    }
}
