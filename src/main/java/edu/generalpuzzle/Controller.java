package edu.generalpuzzle;

import edu.generalpuzzle.main.Main;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@RestController
public class Controller {

    private static final Logger log = LoggerFactory.getLogger(Controller.class);

    @GetMapping("solve/{problemId}/{dimensions}")
    public ResponseEntity<String> solve(
            @PathVariable("problemId") String problemId,
            @PathVariable("dimensions") String dimensions
            ) {

        List<String> args = new ArrayList<>();
        args.add(problemId);
        if (! dimensions.isEmpty() && ! dimensions.equals("null")) {
            args.addAll(List.of(dimensions.split("_")));
        }

        log.info("solve problemId {} dims {}", problemId, dimensions);
        String res = Main.mainGo(args.toArray(new String[0]));
        log.info("DONE problemId {} >> result {}", problemId, res);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
