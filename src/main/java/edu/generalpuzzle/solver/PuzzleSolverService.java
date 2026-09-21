package edu.generalpuzzle.solver;

import org.springframework.stereotype.Service;

import java.util.concurrent.locks.ReentrantLock;

/** Adapts the legacy solver entry point for application-facing callers. */
@Service
public class PuzzleSolverService {

    /*
     * The legacy solver keeps run state in static fields. Until that state is
     * fully moved into a SolveContext, application requests must not overlap.
     */
    private final ReentrantLock solveLock = new ReentrantLock(true);
    private final SolverRunner solverRunner;

    public PuzzleSolverService(SolverRunner solverRunner) {
        this.solverRunner = solverRunner;
    }

    public SolveResult solve(SolveRequest request) {
        solveLock.lock();
        try {
            String result = solverRunner.run(request.toArguments());
            try {
                return new SolveResult(request.problemId(), Integer.parseInt(result.replace(",", "")));
            } catch (NumberFormatException exception) {
                throw new PuzzleSolveException("Unable to solve puzzle '" + request.problemId() + "': " + result, exception);
            }
        } finally {
            solveLock.unlock();
        }
    }
}
