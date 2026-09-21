package edu.generalpuzzle;

import edu.generalpuzzle.solver.PuzzleSolverService;
import edu.generalpuzzle.solver.SolveRequest;
import edu.generalpuzzle.solver.SolveResult;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ControllerTest {

    @Test
    void passesUnderscoreSeparatedDimensionsToSolverService() {
        PuzzleSolverService solverService = mock(PuzzleSolverService.class);
        when(solverService.solve(new SolveRequest("2d_ascii", List.of("4", "5"))))
                .thenReturn(new SolveResult("2d_ascii", 12));
        Controller controller = new Controller(solverService);

        var response = controller.solve("2d_ascii", "4_5");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new SolveResult("2d_ascii", 12), response.getBody());
        ArgumentCaptor<SolveRequest> request = ArgumentCaptor.forClass(SolveRequest.class);
        verify(solverService).solve(request.capture());
        assertEquals(new SolveRequest("2d_ascii", List.of("4", "5")), request.getValue());
    }

    @Test
    void omitsDimensionsWhenRouteDoesNotSupplyThem() {
        PuzzleSolverService solverService = mock(PuzzleSolverService.class);
        when(solverService.solve(new SolveRequest("2d_ascii", List.of())))
                .thenReturn(new SolveResult("2d_ascii", 1));
        Controller controller = new Controller(solverService);

        controller.solve("2d_ascii", null);

        verify(solverService).solve(new SolveRequest("2d_ascii", List.of()));
    }
}
