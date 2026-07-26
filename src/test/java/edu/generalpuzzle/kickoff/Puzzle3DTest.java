package edu.generalpuzzle.kickoff;

import org.junit.jupiter.api.*;

public class Puzzle3DTest {

    @Test
    public void testHasSolution() {
        Puzzle3D puzzle3d = new Puzzle3D(3, 4, 5);
        Assertions.assertTrue(puzzle3d.solve(), "not solved");
        Assertions.assertEquals(1, puzzle3d.totalSolutions);
        assertEveryPieceHasFiveCubes(puzzle3d);
    }

    @Test
    public void testNoSolution() {
        Puzzle3D puzzle3d = new Puzzle3D(3, 2, 5);
        Assertions.assertFalse(puzzle3d.solve(), "solved");
        Assertions.assertEquals(0, puzzle3d.totalSolutions);
    }

    @Test
    public void testDimensionsMustBePositive() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Puzzle3D(0, 4, 5));
    }

    private void assertEveryPieceHasFiveCubes(Puzzle3D puzzle) {
        int[] cubesPerPiece = new int[Puzzle3D.PIECES + 1];
        for (int[][] plane : puzzle.grid) {
            for (int[] line : plane) {
                for (int cell : line) {
                    Assertions.assertTrue(cell > 0 && cell <= Puzzle3D.PIECES);
                    cubesPerPiece[cell]++;
                }
            }
        }
        for (int piece = 1; piece <= Puzzle3D.PIECES; piece++) {
            Assertions.assertEquals(5, cubesPerPiece[piece], "piece " + piece);
        }
    }
}
