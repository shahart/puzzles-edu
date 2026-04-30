package edu.generalpuzzle.resources;

import com.codahale.metrics.annotation.Timed;
import edu.generalpuzzle.api.Solutions;
import edu.generalpuzzle.core.Puzzle2D;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

@Path("/solve")
@Produces(MediaType.APPLICATION_JSON)
public class SolutionsResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(SolutionsResource.class);

    public SolutionsResource() {
    }

    @GET
    @Timed
    public Solutions solve(@QueryParam("id") String id) {

        LOGGER.info("Starting id {}", id);

        String[] rowsCols = id.split("_");
        Puzzle2D puzzle2d = new Puzzle2D(Integer.parseInt(rowsCols[0]), Integer.parseInt(rowsCols[1]));

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Integer> future = executor.submit(() -> puzzle2d.solve()); // or submit(puzzle2d::solve)
        int res = -1;

        try {
            res = future.get(5, TimeUnit.SECONDS);
            LOGGER.info("Done id {} with result {}", id, res);
        } catch (TimeoutException e) {
            LOGGER.warn("Task timed out!", e);
            future.cancel(true);
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Task interrupted!", e);
        } finally {
            executor.shutdown();
        }

        return new Solutions(id, res);
    }
}
