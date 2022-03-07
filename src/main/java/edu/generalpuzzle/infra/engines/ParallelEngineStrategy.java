package edu.generalpuzzle.infra.engines;

import edu.generalpuzzle.infra.IGrid;
import edu.generalpuzzle.infra.Parts;
//import org.apache.log4j.Logger;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * Date: 23/08/2008
 */
public abstract class ParallelEngineStrategy extends EngineStrategy implements Callable<Integer> { // Runnable

    public static final Logger LOG = Logger.getLogger(ParallelEngineStrategy.class.getName());


    private enum RunState { RUNNING, FINISHED, ACKNOWLEDGED}
    protected RunState runState = RunState.RUNNING;

    public ParallelEngineStrategy(Parts parts, IGrid grid) {
        super(parts, grid);
    }

//    public static ParallelEngineStrategy getConcreteEngine(Parts parts, IGrid grid, int userEngine) { // TODO factory?
//        switch (userEngine) {
//            case EngineStrategy.ENGINE_TYPE_RECURSIVE:
//                return new TrivialRecursiveEngineStrategy(parts, grid);
//            case EngineStrategy.ENGINE_TYPE_ITERATIVE:
//                return new TrivialIterativeEngineStrategy(parts, grid);
//            default:
//                return new DlxEngineStrategy(parts, grid);
//        }
//    }

    public boolean isAlive() {
        boolean finished = (runState == RunState.ACKNOWLEDGED);

        if (runState == RunState.FINISHED)
            runState = RunState.ACKNOWLEDGED;

//        if (finished)
//            startTime += 1000;
        
        return ! finished;
    }

    public Integer call() {
        Thread.currentThread().setPriority(Thread.NORM_PRIORITY-1); // parArr
//        starttime = System.currentTimeMillis();
        LOG.info("starting EngineStrategy" + id + " using thread " + Thread.currentThread().getName());

        try {
            solve();
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, "internal error: unexpected " + e.getCause(), e);
            System.out.println("\ninternal error:\n");
            e.printStackTrace(System.out);
        }

        LOG.info("finished EngineStrategy" + id); // LOG derived from EngineStrategy
        runState = RunState.FINISHED;
        return 0;
    }

    public abstract void solve();

}
