package edu.generalpuzzle.main;

import edu.generalpuzzle.infra.engines.ParallelEngineStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SolverExecutionTest {

    @Test
    void aggregatesEngineMetrics() {
        ParallelEngineStrategy first = mock(ParallelEngineStrategy.class);
        when(first.getTriedParts()).thenReturn(10L);
        when(first.getElapsedTime()).thenReturn(1_200L);
        when(first.getUniqueSolutions()).thenReturn(2);
        when(first.getTotalSolutions()).thenReturn(4);
        ParallelEngineStrategy second = mock(ParallelEngineStrategy.class);
        when(second.getTriedParts()).thenReturn(15L);
        when(second.getElapsedTime()).thenReturn(2_800L);
        when(second.getUniqueSolutions()).thenReturn(3);
        when(second.getTotalSolutions()).thenReturn(6);

        SolverExecutionResult result = SolverExecution.aggregate(
                new ParallelEngineStrategy[] {first, second}, 7);

        assertEquals(7, result.wallClockSeconds());
        assertEquals(4, result.engineSeconds());
        assertEquals(25, result.triedParts());
        assertEquals(5, result.uniqueSolutions());
        assertEquals(10, result.totalSolutions());
    }
}
