package edu.generalpuzzle.main;

import edu.generalpuzzle.infra.Shared;
import edu.generalpuzzle.infra.engines.ParallelEngineStrategy;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Coordinates parallel engine execution and collects its numerical outcome. */
public class SolverExecution {

    private static final Logger LOG = Logger.getLogger(SolverExecution.class.getName());

    public SolverExecutionResult execute(ParallelEngineStrategy[] engines, int workerCount, int fromPart,
                                         int toPart, long knownSolutions) {
        int partsSize = engines[0].getParts().getParts().size();
        for (ParallelEngineStrategy engine : engines) {
            engine.setForRatio(knownSolutions);
        }
        assignRanges(engines, fromPart, toPart, partsSize);

        Thread.currentThread().setPriority(Thread.NORM_PRIORITY - 1);
        System.gc();
        long startedAt = System.currentTimeMillis();

        ExecutorService executor = Executors.newFixedThreadPool(workerCount);
        try {
            List<Callable<Integer>> tasks = new ArrayList<>(List.of(engines));
            executor.invokeAll(tasks);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            executor.shutdown();
        }

        long wallClockSeconds = (System.currentTimeMillis() - startedAt) / 1000;
        LOG.info("parallel time: " + wallClockSeconds);
        return aggregate(engines, wallClockSeconds);
    }

    static SolverExecutionResult aggregate(ParallelEngineStrategy[] engines, long wallClockSeconds) {
        long triedParts = 0;
        long engineMilliseconds = 0;
        int uniqueSolutions = 0;
        int totalSolutions = 0;
        for (ParallelEngineStrategy engine : engines) {
            triedParts += engine.getTriedParts();
            engineMilliseconds += engine.getElapsedTime();
            uniqueSolutions += engine.getUniqueSolutions();
            totalSolutions += engine.getTotalSolutions();
        }

        int duplicateEntries = 0;
        int duplicateSets = 0;
        if (Shared.getInstance().uniqsFoundSolutions != null) {
            for (var duplicateRow : Shared.getInstance().uniqsFoundSolutions) {
                for (var duplicates : duplicateRow) {
                    if (duplicates != null && !duplicates.isEmpty()) {
                        duplicateEntries += duplicates.size();
                        duplicateSets++;
                    }
                }
            }
        }
        return new SolverExecutionResult(wallClockSeconds, engineMilliseconds / 1000, triedParts,
                uniqueSolutions, totalSolutions, duplicateSets, duplicateEntries);
    }

    private void assignRanges(ParallelEngineStrategy[] engines, int fromPart, int toPart, int partsSize) {
        int partsPerEngine = partsSize / engines.length;
        int currentPart = fromPart;
        for (int index = 0; index < engines.length; index++) {
            if (index == engines.length - 1 && currentPart + partsPerEngine < partsSize) {
                partsPerEngine = toPart - currentPart;
            }
            engines[index].setRange(currentPart, currentPart + partsPerEngine);
            currentPart += partsPerEngine;
        }
    }
}
