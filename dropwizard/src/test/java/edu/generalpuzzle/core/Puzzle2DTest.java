package edu.generalpuzzle.core;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import static org.junit.jupiter.api.Assertions.*;

public class Puzzle2DTest {

    @Test
    @Timeout(5)
    void test12_5() {
        Puzzle2D puzzle2D = new Puzzle2D(12, 5);
        assertEquals(1010, puzzle2D.solve());
    }

    @Disabled
    @Timeout(5)
    void test5_12() {
        Puzzle2D puzzle2D = new Puzzle2D(5, 12);
        assertEquals(1010, puzzle2D.solve());
    }

    @Disabled
    @Timeout(5)
    void test10_6() {
        Puzzle2D puzzle2D = new Puzzle2D(10, 6);
        assertEquals(2339, puzzle2D.solve());
    }


}
