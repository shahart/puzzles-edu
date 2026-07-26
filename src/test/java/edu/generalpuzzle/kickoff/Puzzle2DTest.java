package edu.generalpuzzle.kickoff;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class Puzzle2DTest {

    @BeforeEach
    public void resetGlobals() {
        Piece.totalFill = 0;
    }

    @Test
    public void testHasSolution() {
        Puzzle2D puzzle2d = new Puzzle2D(12, 5);

        Assertions.assertTrue(puzzle2d.solve(), "not solved");
        Assertions.assertEquals(1, puzzle2d.totalSolutions);
        Assertions.assertTrue(puzzle2d.triedPieces > 0);
        Assertions.assertEquals(puzzle2d.pieces.length, puzzle2d.piecesIndices.size());
    }

    @Test
    public void testPentominoSet() {
        Puzzle2D puzzle2d = new Puzzle2D(12, 5);

        Assertions.assertEquals(12, puzzle2d.PIECES);
        Assertions.assertEquals("LUFXYNWPZVTI", puzzle2d.names);
        Assertions.assertEquals(60, Piece.getTotalFill());
        for (int index = 0; index < puzzle2d.PIECES; index++) {
            int pieceIndex = index;
            Piece piece = puzzle2d.pieces[pieceIndex];
            Assertions.assertAll(
                    () -> Assertions.assertNotNull(piece),
                    () -> Assertions.assertEquals(5, piece.getTotalThisFill()),
                    () -> Assertions.assertEquals(pieceIndex, piece.getIndex()),
                    () -> Assertions.assertEquals(puzzle2d.names.charAt(pieceIndex), piece.getName())
            );
        }
    }

    @Test
    public void testNoSolution() {
        Puzzle2D puzzle2d = new Puzzle2D(12, 1);

        Assertions.assertFalse(puzzle2d.solve(), "solved");
        Assertions.assertEquals(0, puzzle2d.totalSolutions);
        Assertions.assertEquals(0, puzzle2d.triedPieces,
                "a volume mismatch should be rejected before searching");
    }

    @Test
    public void testEightByEightBoardHasBlockedCorners() {
        Puzzle2D puzzle2d = new Puzzle2D(8, 8);

        Assertions.assertEquals(8, puzzle2d.ROWS);
        Assertions.assertEquals(8, puzzle2d.COLUMNS);
        Assertions.assertEquals(-1, puzzle2d.grid[0][0]);
        Assertions.assertEquals(-1, puzzle2d.grid[0][7]);
        Assertions.assertEquals(-1, puzzle2d.grid[7][0]);
        Assertions.assertEquals(-1, puzzle2d.grid[7][7]);
        Assertions.assertEquals(1, puzzle2d.column,
                "the first candidate position should skip the blocked corner");

        int blockedCells = 0;
        for (int[] row : puzzle2d.grid) {
            for (int cell : row) {
                if (cell == -1) {
                    blockedCells++;
                }
            }
        }
        Assertions.assertEquals(4, blockedCells);
    }

    @Test
    public void testAsciiConfiguration() {
        Puzzle2D puzzle2d = new Puzzle2D(0, 0);

        Assertions.assertEquals(6, puzzle2d.ROWS);
        Assertions.assertEquals(6, puzzle2d.COLUMNS);
        Assertions.assertEquals(10, puzzle2d.PIECES);
        Assertions.assertEquals(36, Piece.getTotalFill());
        Assertions.assertEquals(10, puzzle2d.piecesIndices.size());
        for (Piece piece : puzzle2d.pieces) {
            Assertions.assertNotNull(piece);
        }
        Assertions.assertTrue(puzzle2d.solve(), "not solved");
        Assertions.assertEquals(1, puzzle2d.totalSolutions);
    }

    @Test
    public void testCliAcceptsAllSolutionsOption() {
        String output = captureOutput(() -> Puzzle2D.main(new String[]{"12", "1", "--all"}));

        Assertions.assertFalse(output.contains("usage:"));
        Assertions.assertTrue(output.contains("number of solutions 0"));
    }

    @Test
    public void testCliRejectsUnknownOption() {
        String output = captureOutput(() -> Puzzle2D.main(new String[]{"12", "5", "--unknown"}));

        Assertions.assertTrue(output.contains("usage: Puzzle2D rows columns [--all|--count]"));
    }

    @Test
    public void testCliAcceptsCountOptionWithoutShowingGrid() {
        String output = captureOutput(() -> Puzzle2D.main(new String[]{"12", "1", "--count"}));

        Assertions.assertFalse(output.contains("usage:"));
        Assertions.assertFalse(output.contains("Found solution"));
        Assertions.assertTrue(output.contains("number of solutions 0"));
    }

    @Test
    public void testFiveByTwelveRectangleHas1010Solutions() {
        Puzzle2D puzzle2d = new Puzzle2D(5, 12);
        int[] count = new int[1];

        String output = captureOutput(() -> count[0] = puzzle2d.countSolutions());

        Assertions.assertEquals(1010, count[0]);
        Assertions.assertEquals(1010, puzzle2d.totalSolutions);
        Assertions.assertFalse(output.contains("Found solution"));
        Assertions.assertFalse(output.contains("showGrid"));
        Assertions.assertTrue(output.contains("number of solutions 1,010")
                || output.contains("number of solutions 1010"));
    }

    @Test
    public void testSixByTenRectangleHas2339Solutions() {
        Assertions.assertEquals(2339, countSolutions(6, 10));
    }

    @Test
    public void testFourByFifteenRectangleHas368Solutions() {
        Assertions.assertEquals(368, countSolutions(4, 15));
    }

    @Test
    public void testThreeByTwentyRectangleHas2Solutions() {
        Assertions.assertEquals(2, countSolutions(3, 20));
    }

    private static int countSolutions(int rows, int columns) {
        int[] count = new int[1];
        captureOutput(() -> count[0] = new Puzzle2D(rows, columns).countSolutions());
        return count[0];
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

}
