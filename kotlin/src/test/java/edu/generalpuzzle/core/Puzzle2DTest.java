package edu.generalpuzzle.core;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.*;

@SpringBootTest
public class Puzzle2DTest {

    // and layouts?!

    @BeforeEach
    public void resetGlobals() {
        Piece.totalFill = 0;
    }

    @Autowired
    Puzzle2D puzzle2D;

    @Test
    public void testHasSolution12_5() {
        puzzle2D.set(12, 5);
        Assertions.assertEquals(puzzle2D.solve(), 1);
    }

    @Test
    public void testHasSolution10_6() {
        puzzle2D.set(10, 6);
        Assertions.assertEquals(puzzle2D.solve(), 1);
    }

    @Test
    public void testNoSolution5_13() {
        puzzle2D.set(5, 13);
        Assertions.assertEquals(puzzle2D.solve(), 0);
    }

}
