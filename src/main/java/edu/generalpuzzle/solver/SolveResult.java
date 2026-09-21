package edu.generalpuzzle.solver;

/** Stable response returned by solver entry points, independent of console output. */
public record SolveResult(String problemId, int totalSolutions) {
}
