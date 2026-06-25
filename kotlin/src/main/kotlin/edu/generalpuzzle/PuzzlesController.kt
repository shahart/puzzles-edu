package edu.generalpuzzle

import edu.generalpuzzle.core.Puzzle2D
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@RestController
class PuzzlesController(private val puzzle2D: Puzzle2D) {

    private val log = LoggerFactory.getLogger(PuzzlesController::class.java)

    @GetMapping("solve/{problemId}", "solve/{problemId}/{dimensions}")
    fun solve(
        @PathVariable problemId: String,
        @PathVariable(required = false) dimensions: String? = null
    ): ResponseEntity<Int> {
        log.info("Starting id {}", problemId)

        val rowsCols = problemId.split("_")
        puzzle2D.set(rowsCols[0].toInt(), rowsCols[1].toInt())

        val executor = Executors.newSingleThreadExecutor()
        val future: Future<Int> = executor.submit(Callable { puzzle2D.solve() })
        var res = -1

        try {
            res = future.get(5, TimeUnit.SECONDS)
            log.info("Done id {} with result {}", puzzle2D, res)
        } catch (e: TimeoutException) {
            log.warn("Task timed out!", e)
            future.cancel(true)
        } catch (e: InterruptedException) {
            log.error("Task interrupted!", e)
        } catch (e: java.util.concurrent.ExecutionException) {
            log.error("Task execution exception!", e)
        } finally {
            executor.shutdown()
        }

        log.info("DONE problemId {} >> result {}", problemId, res)
        return ResponseEntity(res, HttpStatus.OK)
    }
}
