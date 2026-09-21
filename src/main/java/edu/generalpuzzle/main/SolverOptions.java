package edu.generalpuzzle.main;

import edu.generalpuzzle.infra.engines.EngineType;

/** Immutable runtime settings for one solver run. */
public record SolverOptions(
        EngineType engineType,
        int dlSplits,
        boolean fullOutput,
        boolean generateByAll,
        boolean graphForAll,
        boolean sizeHeuristic,
        boolean internalViewer,
        boolean strandedHeuristic,
        boolean autoGraphIt,
        boolean autoDebug,
        int threads) {

    public static SolverOptions defaults() {
        return new SolverOptions(EngineType.RECURSIVE, 0, false, false, false,
                true, false, true, true, false, 1);
    }
}
