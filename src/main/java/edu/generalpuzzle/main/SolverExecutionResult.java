package edu.generalpuzzle.main;

/** Aggregated, transport-independent outcome of one parallel solver run. */
public record SolverExecutionResult(
        long wallClockSeconds,
        long engineSeconds,
        long triedParts,
        int uniqueSolutions,
        int totalSolutions,
        int duplicateSets,
        int duplicateEntries) {
}
