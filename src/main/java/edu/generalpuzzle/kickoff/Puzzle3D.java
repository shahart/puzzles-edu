package edu.generalpuzzle.kickoff;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A small, self-contained 3D counterpart to {@link Puzzle2D}.
 * <p>
 * The puzzle uses the twelve pentominoes from {@code Puzzle2D}. A pentomino may
 * be turned in any of the 24 rigid orientations of a cube, but it may not be
 * reflected. The solver fills the first empty cell and backtracks when none of
 * the remaining pieces can cover it.
 */
public class Puzzle3D {
    public static final int PIECES = 12;

    public final int ROWS;
    public final int COLUMNS;
    public final int DEPTH;

    public int totalSolutions;
    public long triedPieces;
    public final int[][][] grid;

    private static final char[] NAMES = "LUFXYNWPZVTI".toCharArray();
    private static final int[][][] PENTOMINOES = {
            {{1}, {1}, {1}, {1, 1}},
            {{1, 1}, {1}, {1, 1}},
            {{0, 1, 1}, {1, 1}, {0, 1}},
            {{0, 1}, {1, 1, 1}, {0, 1}},
            {{1, 1, 1, 1}, {0, 0, 1}},
            {{0, 1}, {1, 1}, {1}, {1}},
            {{0, 0, 1}, {0, 1, 1}, {1, 1}},
            {{1}, {1, 1}, {1, 1}},
            {{0, 0, 1}, {1, 1, 1}, {1}},
            {{0, 0, 1}, {0, 0, 1}, {1, 1, 1}},
            {{0, 0, 1}, {1, 1, 1}, {0, 0, 1}},
            {{1, 1, 1, 1, 1}}
    };

    private final List<int[][]>[] orientations;
    private final List<Placement>[] placementsByCell;
    private int usedPieces;
    private long occupiedCells;

    @SuppressWarnings("unchecked")
    Puzzle3D(int rows, int columns, int depth) {
        if (rows < 1 || columns < 1 || depth < 1) {
            throw new IllegalArgumentException("All dimensions must be positive");
        }

        ROWS = rows;
        COLUMNS = columns;
        DEPTH = depth;
        grid = new int[ROWS][COLUMNS][DEPTH];

        orientations = (List<int[][]>[]) new List<?>[PIECES];
        for (int piece = 0; piece < PIECES; piece++) {
            orientations[piece] = buildOrientations(PENTOMINOES[piece]);
        }
        placementsByCell = buildPlacements();
    }

    /**
     * Finds one tiling of the configured box.
     *
     * @return {@code true} when all twelve pentominoes fit exactly
     */
    public boolean solve() {
        totalSolutions = 0;
        triedPieces = 0;
        usedPieces = 0;
        occupiedCells = 0;
        for (int[][] plane : grid) {
            for (int[] line : plane) {
                Arrays.fill(line, 0);
            }
        }

        long start = System.currentTimeMillis();
        if ((long) ROWS * COLUMNS * DEPTH == PIECES * 5L && put()) {
            totalSolutions = 1;
        }

        long elapsedMillis = System.currentTimeMillis() - start;
        NumberFormat numberFormat = NumberFormat.getInstance();
        System.out.println("elapsed time in milliseconds " + elapsedMillis);
        System.out.println("tried " + numberFormat.format(triedPieces) + " pieces");
        System.out.println("number of solutions " + totalSolutions);
        return totalSolutions > 0;
    }

    private boolean put() {
        int empty = mostConstrainedEmptyCell();
        if (empty == -1) {
            return usedPieces == (1 << PIECES) - 1;
        }

        for (Placement placement : placementsByCell[empty]) {
            triedPieces++;
            int mask = 1 << placement.piece();
            if ((usedPieces & mask) != 0 || !isFree(placement)) {
                continue;
            }

            fill(placement, placement.piece() + 1);
            usedPieces |= mask;
            if (put()) {
                return true;
            }
            usedPieces &= ~mask;
            fill(placement, 0);
        }
        return false;
    }

    private int mostConstrainedEmptyCell() {
        int bestCell = -1;
        int bestOptions = Integer.MAX_VALUE;
        int cell = 0;
        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                for (int depth = 0; depth < DEPTH; depth++, cell++) {
                    if (grid[row][column][depth] != 0) {
                        continue;
                    }
                    int options = 0;
                    for (Placement placement : placementsByCell[cell]) {
                        if ((usedPieces & (1 << placement.piece())) == 0 && isFree(placement)) {
                            options++;
                        }
                    }
                    if (options < bestOptions) {
                        bestOptions = options;
                        bestCell = cell;
                        if (options == 0) {
                            return bestCell;
                        }
                    }
                }
            }
        }
        return bestCell;
    }

    private boolean isFree(Placement placement) {
        return (occupiedCells & placement.cellMask()) == 0;
    }

    private void fill(Placement placement, int value) {
        if (value == 0) {
            occupiedCells &= ~placement.cellMask();
        } else {
            occupiedCells |= placement.cellMask();
        }
        for (int cell : placement.cells()) {
            int row = cell / (COLUMNS * DEPTH);
            int remainder = cell % (COLUMNS * DEPTH);
            grid[row][remainder / DEPTH][remainder % DEPTH] = value;
        }
    }

    @SuppressWarnings("unchecked")
    private List<Placement>[] buildPlacements() {
        int cellsInGrid = ROWS * COLUMNS * DEPTH;
        if (cellsInGrid != PIECES * 5) {
            // solve() rejects a differently sized box before consulting this index.
            return (List<Placement>[]) new List<?>[0];
        }
        List<Placement>[] byCell = (List<Placement>[]) new List<?>[cellsInGrid];
        for (int cell = 0; cell < cellsInGrid; cell++) {
            byCell[cell] = new ArrayList<>();
        }

        for (int piece = 0; piece < PIECES; piece++) {
            for (int[][] orientation : orientations[piece]) {
                int maxRow = 0;
                int maxColumn = 0;
                int maxDepth = 0;
                for (int[] cube : orientation) {
                    maxRow = Math.max(maxRow, cube[0]);
                    maxColumn = Math.max(maxColumn, cube[1]);
                    maxDepth = Math.max(maxDepth, cube[2]);
                }
                for (int rowOffset = 0; rowOffset + maxRow < ROWS; rowOffset++) {
                    for (int columnOffset = 0;
                         columnOffset + maxColumn < COLUMNS;
                         columnOffset++) {
                        for (int depthOffset = 0;
                             depthOffset + maxDepth < DEPTH;
                             depthOffset++) {
                            int[] cells = new int[orientation.length];
                            for (int cube = 0; cube < orientation.length; cube++) {
                                int row = rowOffset + orientation[cube][0];
                                int column = columnOffset + orientation[cube][1];
                                int depth = depthOffset + orientation[cube][2];
                                cells[cube] = (row * COLUMNS + column) * DEPTH + depth;
                            }
                            long cellMask = 0;
                            for (int cell : cells) {
                                cellMask |= 1L << cell;
                            }
                            Placement placement = new Placement(piece, cells, cellMask);
                            for (int cell : cells) {
                                byCell[cell].add(placement);
                            }
                        }
                    }
                }
            }
        }
        return byCell;
    }

    private record Placement(int piece, int[] cells, long cellMask) {
    }

    private static List<int[][]> buildOrientations(int[][] layout) {
        int[][] cubes = layoutToCubes(layout);
        List<int[][]> result = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        int[][] permutations = {
                {0, 1, 2}, {0, 2, 1}, {1, 0, 2},
                {1, 2, 0}, {2, 0, 1}, {2, 1, 0}
        };
        for (int[] permutation : permutations) {
            int permutationSign = permutationSign(permutation);
            for (int firstSign : new int[]{-1, 1}) {
                for (int secondSign : new int[]{-1, 1}) {
                    for (int thirdSign : new int[]{-1, 1}) {
                        // Keep proper rotations only (determinant +1).
                        if (permutationSign * firstSign * secondSign * thirdSign != 1) {
                            continue;
                        }
                        int[] signs = {firstSign, secondSign, thirdSign};
                        int[][] transformed = new int[cubes.length][3];
                        for (int cube = 0; cube < cubes.length; cube++) {
                            for (int axis = 0; axis < 3; axis++) {
                                transformed[cube][axis] =
                                        signs[axis] * cubes[cube][permutation[axis]];
                            }
                        }
                        normalizeAndSort(transformed);
                        String key = Arrays.deepToString(transformed);
                        if (seen.add(key)) {
                            result.add(transformed);
                        }
                    }
                }
            }
        }
        return result;
    }

    private static int[][] layoutToCubes(int[][] layout) {
        List<int[]> cubes = new ArrayList<>(5);
        for (int row = 0; row < layout.length; row++) {
            for (int column = 0; column < layout[row].length; column++) {
                if (layout[row][column] == 1) {
                    cubes.add(new int[]{row, column, 0});
                }
            }
        }
        return cubes.toArray(int[][]::new);
    }

    private static int permutationSign(int[] permutation) {
        int inversions = 0;
        for (int i = 0; i < permutation.length; i++) {
            for (int j = i + 1; j < permutation.length; j++) {
                if (permutation[i] > permutation[j]) {
                    inversions++;
                }
            }
        }
        return inversions % 2 == 0 ? 1 : -1;
    }

    private static void normalizeAndSort(int[][] cubes) {
        int[] minimum = {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
        for (int[] cube : cubes) {
            for (int axis = 0; axis < 3; axis++) {
                minimum[axis] = Math.min(minimum[axis], cube[axis]);
            }
        }
        for (int[] cube : cubes) {
            for (int axis = 0; axis < 3; axis++) {
                cube[axis] -= minimum[axis];
            }
        }
        Arrays.sort(cubes, (left, right) -> {
            for (int axis = 0; axis < 3; axis++) {
                int comparison = Integer.compare(left[axis], right[axis]);
                if (comparison != 0) {
                    return comparison;
                }
            }
            return 0;
        });
    }

    public void showGrid() {
        for (int depth = 0; depth < DEPTH; depth++) {
            System.out.println("depth " + depth);
            for (int row = 0; row < ROWS; row++) {
                for (int column = 0; column < COLUMNS; column++) {
                    int piece = grid[row][column][depth];
                    System.out.print(piece == 0 ? "- " : NAMES[piece - 1] + " ");
                }
                System.out.println();
            }
        }
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("usage: Puzzle3D rows columns depth");
            return;
        }
        Puzzle3D puzzle = new Puzzle3D(
                Integer.parseInt(args[0]),
                Integer.parseInt(args[1]),
                Integer.parseInt(args[2]));
        if (puzzle.solve()) {
            puzzle.showGrid();
        }
    }
}
