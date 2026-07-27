package edu.generalpuzzle

import edu.generalpuzzle.core.Puzzle2D
import edu.generalpuzzle.core.Puzzle3D
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.ObjectProvider
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestParam
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@RestController
class PuzzlesController(private val puzzle2DProvider: ObjectProvider<Puzzle2D>) {

    private val log = LoggerFactory.getLogger(PuzzlesController::class.java)

    @GetMapping("solve/{problemId}", "solve/{problemId}/{dimensions}")
    fun solve(
        @PathVariable problemId: String,
        @PathVariable(required = false) dimensions: String? = null,
        @RequestParam(defaultValue = "false") count: Boolean
    ): ResponseEntity<Int> {
        log.info("Starting id {}", problemId)

        val rowsCols = problemId.split("_")
        val puzzle2D = puzzle2DProvider.getObject()
        puzzle2D.set(rowsCols[0].toInt(), rowsCols[1].toInt())

        val res = solveWithTimeout(problemId, Callable { puzzle2D.solve(count) })
        log.info("DONE problemId {} >> result {}", problemId, res)
        return ResponseEntity(res, HttpStatus.OK)
    }

    @GetMapping("solve3d/{problemId}")
    fun solve3d(@PathVariable problemId: String): ResponseEntity<Int> {
        log.info("Starting 3D id {}", problemId)
        val dimensions = problemId.split("_").map(String::toInt)
        val result = solveWithTimeout(problemId, Callable {
            Puzzle3D(dimensions[0], dimensions[1], dimensions[2]).solve()
        })
        log.info("Done 3D id {} with result {}", problemId, result)
        return ResponseEntity.ok(result)
    }

    private fun solveWithTimeout(problemId: String, solver: Callable<Int>): Int {
        val executor = Executors.newSingleThreadExecutor()
        val future = executor.submit(solver)

        return try {
            future.get(5, TimeUnit.SECONDS)
        } catch (e: TimeoutException) {
            log.warn("Id {} timed out", problemId, e)
            future.cancel(true)
            -1
        } catch (e: InterruptedException) {
            log.error("Id {} was interrupted", problemId, e)
            future.cancel(true)
            Thread.currentThread().interrupt()
            -1
        } catch (e: java.util.concurrent.ExecutionException) {
            log.error("Id {} failed", problemId, e)
            -1
        } finally {
            executor.shutdown()
        }
    }
}
