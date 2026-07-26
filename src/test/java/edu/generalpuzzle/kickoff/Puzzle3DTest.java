package edu.generalpuzzle.kickoff;

import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

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

    @Test
    public void testCliAcceptsAllSolutionsOption() {
        String output = captureOutput(() -> Puzzle3D.main(new String[]{"3", "2", "5", "--all"}));

        Assertions.assertFalse(output.contains("usage:"));
        Assertions.assertTrue(output.contains("number of solutions 0"));
    }

    @Test
    public void testCliRejectsUnknownOption() {
        String output = captureOutput(() -> Puzzle3D.main(new String[]{"3", "4", "5", "--unknown"}));

        Assertions.assertTrue(output.contains("usage: Puzzle3D rows columns depth [--all|--count]"));
    }

    @Test
    public void testCliAcceptsCountOptionWithoutShowingGrid() {
        String output = captureOutput(() -> Puzzle3D.main(new String[]{"3", "2", "5", "--count"}));

        Assertions.assertFalse(output.contains("usage:"));
        Assertions.assertFalse(output.contains("depth 0"));
        Assertions.assertTrue(output.contains("number of solutions 0"));
    }

    @Test
    public void testThreeByFourByFiveBoxHas3940Solutions() {
        Assertions.assertEquals(3940, countSolutions(3, 4, 5));
    }

    @Test
    public void testTwoByFiveBySixBoxHas264Solutions() {
        Assertions.assertEquals(264, countSolutions(2, 5, 6));
    }

    @Test
    public void testTwoByThreeByTenBoxHas12Solutions() {
        Assertions.assertEquals(12, countSolutions(2, 3, 10));
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

    private static String captureOutput(Runnable action) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(output));
            action.run();
        } finally {
            System.setOut(originalOut);
        }
        return output.toString();
    }

    private static int countSolutions(int rows, int columns, int depth) {
        int[] count = new int[1];
        captureOutput(() -> count[0] = new Puzzle3D(rows, columns, depth).countSolutions());
        return count[0];
    }
}
