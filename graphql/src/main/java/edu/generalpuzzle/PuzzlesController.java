package edu.generalpuzzle;

import edu.generalpuzzle.core.Puzzle2D;
import edu.generalpuzzle.model.Puzzle;
import edu.generalpuzzle.model.Solutions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
public class PuzzlesController {

    private static final Log log = LogFactory.getLog(PuzzlesController.class);

    @QueryMapping
    public Puzzle puzzlesById(@Argument String id) {
        log.info("Starting id " + id);

        String[] rowsCols = id.split("_");
        Puzzle2D puzzle2d = new Puzzle2D(Integer.parseInt(rowsCols[0]), Integer.parseInt(rowsCols[1]), false);
        int res = puzzle2d.solve();

        log.info("Done. result " + res);

        return new Puzzle(id, res >= 1 ? puzzle2d.getAllSolutions().getFirst() : null, null);
    }

    @SchemaMapping
    public Solutions solutions(Puzzle puzzle) {
        String id = puzzle.id();

        log.info("Starting ALL for id " + id);

        String[] rowsCols = id.split("_");
        Puzzle2D puzzle2d = new Puzzle2D(Integer.parseInt(rowsCols[0]), Integer.parseInt(rowsCols[1]), true);
        int res = puzzle2d.solve();

        log.info("Done ALL. result " + res);

        return new Solutions(id, puzzle2d.getAllSolutions(), res);
    }

}
