package edu.generalpuzzle;

import edu.generalpuzzle.core.Puzzle2D;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.*;

@RestController
public class PuzzlesController {

    private static final Logger log = LoggerFactory.getLogger(PuzzlesController.class);

    @Autowired
    Puzzle2D puzzle2D;

    @GetMapping(value = {"solve/{problemId}", "solve/{problemId}/{dimensions}"})
    public ResponseEntity<Integer> solve(
            @PathVariable("problemId") String problemId,
            @PathVariable("dimensions") Optional<String> optDimensions
    ) {

        log.info("Starting id {}",  problemId);

        String[] rowsCols = problemId.split("_");
        puzzle2D.set(Integer.parseInt(rowsCols[0]), Integer.parseInt(rowsCols[1]));

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Integer> future = executor.submit(() -> puzzle2D.solve()); // or submit(puzzle2d::solve)
        int res = -1;

        try {
            res = future.get(5, TimeUnit.SECONDS);
            log.info("Done id {} with result {}", puzzle2D, res);
        } catch (TimeoutException e) {
            log.warn("Task timed out!", e);
            future.cancel(true);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Task interrupted!", e);
        } finally {
            executor.shutdown();
        }

        log.info("DONE problemId {} >> result {}", problemId, res);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
