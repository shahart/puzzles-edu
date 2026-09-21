package edu.generalpuzzle;

import edu.generalpuzzle.solver.PuzzleSolveException;
import edu.generalpuzzle.solver.PuzzleSolverService;
import edu.generalpuzzle.solver.SolveRequest;
import edu.generalpuzzle.solver.SolveResult;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
public class Controller {

    private static final Logger log = LoggerFactory.getLogger(Controller.class);
    private final PuzzleSolverService puzzleSolverService;

    public Controller(PuzzleSolverService puzzleSolverService) {
        this.puzzleSolverService = puzzleSolverService;
    }

    @GetMapping(value = {"solve/{problemId}", "solve/{problemId}/{dimensions}"})
    public ResponseEntity<SolveResult> solve(
            @PathVariable("problemId") String problemId,
            @PathVariable(value = "dimensions", required = false) String dimensions
            ) {
        log.info("solve problemId {} dims {}", problemId, dimensions);
        List<String> parsedDimensions = dimensions == null || dimensions.isBlank()
                ? List.of()
                : List.of(dimensions.split("_"));
        SolveResult result = puzzleSolverService.solve(new SolveRequest(problemId, parsedDimensions));
        log.info("DONE problemId {} >> totalSolutions {}", problemId, result.totalSolutions());
        return ResponseEntity.ok(result);
    }

    @ExceptionHandler({IllegalArgumentException.class, PuzzleSolveException.class})
    public ResponseEntity<String> invalidSolveRequest(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }
}
