package edu.generalpuzzle.solver;

import edu.generalpuzzle.main.Main;
import org.springframework.stereotype.Component;

/** Production adapter for the legacy static command-line entry point. */
@Component
class LegacyMainSolverRunner implements SolverRunner {

    @Override
    public String run(String[] arguments) {
        return Main.mainGo(arguments);
    }
}
