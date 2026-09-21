package edu.generalpuzzle.solver;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.AdditionalMatchers.aryEq;

class PuzzleSolverServiceTest {

    @Test
    void convertsLegacySolverOutputToTypedResult() {
        SolverRunner runner = mock(SolverRunner.class);
        when(runner.run(aryEq(new String[] {"2d_ascii", "4", "5"}))).thenReturn("1,234");
        PuzzleSolverService service = new PuzzleSolverService(runner);

        SolveResult result = service.solve(new SolveRequest("2d_ascii", List.of("4", "5")));

        assertEquals(new SolveResult("2d_ascii", 1234), result);
        verify(runner).run(aryEq(new String[] {"2d_ascii", "4", "5"}));
    }

    @Test
    void wrapsNonNumericLegacyOutput() {
        SolverRunner runner = arguments -> "missing problemId";
        PuzzleSolverService service = new PuzzleSolverService(runner);

        PuzzleSolveException exception = assertThrows(PuzzleSolveException.class,
                () -> service.solve(new SolveRequest("unknown", List.of())));

        assertEquals("Unable to solve puzzle 'unknown': missing problemId", exception.getMessage());
    }
}
