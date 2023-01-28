package edu.generalpuzzle.kickoff;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.*;

public class Puzzle2DTest {

    // and layouts?!

    @BeforeEach
    public void resetGlobals() {
        Piece.totalFill = 0;
    }

    @Test
    public void testHasSolution() {
        Puzzle2D puzzle2d = new Puzzle2D(12, 5);
        Assertions.assertTrue(puzzle2d.solve(), "not solved");
    }

    @Test
    public void testNoSolution() {
        Puzzle2D puzzle2d = new Puzzle2D(12, 1);
        Assertions.assertFalse(puzzle2d.solve(), "solved");
    }

    @Test
    public void testAscii() throws IOException {
        // Files.copy(Path.of("./config/ascii/2d_6_6.txt"), Path.of("./myPzl.txt"), StandardCopyOption.REPLACE_EXISTING);
        Puzzle2D puzzle2d = new Puzzle2D(0, 0);
        Assertions.assertTrue(puzzle2d.solve(), "not solved");
    }

}
