package edu.generalpuzzle.solver;

/** Executes the legacy solver for one already-validated argument list. */
@FunctionalInterface
public interface SolverRunner {

    String run(String[] arguments);
}
