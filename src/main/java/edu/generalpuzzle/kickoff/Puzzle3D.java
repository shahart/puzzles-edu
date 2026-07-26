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
    private boolean enumerateAllSolutions;
    private boolean showAllSolutions;

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
        return solveInternal(false, false);
    }

    /**
     * Solves the puzzle, optionally continuing after the first solution.
     *
     * @param showAllSolutions when {@code true}, enumerate and print every solution
     */
    public boolean solve(boolean showAllSolutions) {
        return solveInternal(showAllSolutions, showAllSolutions);
    }

    /**
     * Enumerates every solution without printing individual solution grids.
     *
     * @return the number of solutions found
     */
    public int countSolutions() {
        totalSolutions = 0;
        triedPieces = 0;
        long start = System.currentTimeMillis();
        if ((long) ROWS * COLUMNS * DEPTH == PIECES * 5L) {
            Set<Placement> uniquePlacements =
                    java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>());
            for (List<Placement> placements : placementsByCell) {
                uniquePlacements.addAll(placements);
            }
            List<int[]> exactCoverRows = new ArrayList<>(uniquePlacements.size());
            List<Placement> rowPlacements = new ArrayList<>(uniquePlacements.size());
            List<int[]> boxSymmetries = buildBoxSymmetries();
            int cellCount = ROWS * COLUMNS * DEPTH;
            for (Placement placement : uniquePlacements) {
                // The L pentomino is asymmetric, so exactly one of its placements
                // can represent each orbit under the box's rotations/reflections.
                if (placement.piece() == 0
                        && !isCanonicalLPlacement(placement, boxSymmetries)) {
                    continue;
                }
                int[] row = Arrays.copyOf(placement.cells(), placement.cells().length + 1);
                row[row.length - 1] = cellCount + placement.piece();
                exactCoverRows.add(row);
                rowPlacements.add(placement);
            }
            ExactCoverCounter.Result result =
                    ExactCoverCounter.count(
                            cellCount + PIECES,
                            exactCoverRows,
                            selectedRows -> isCanonicalSolution(
                                    selectedRows, rowPlacements, boxSymmetries));
            totalSolutions = Math.toIntExact(result.solutions());
            triedPieces = result.triedRows();
        }
        long elapsedMillis = System.currentTimeMillis() - start;
        System.out.println("elapsed time in milliseconds " + elapsedMillis);
        System.out.println("tried " + NumberFormat.getInstance().format(triedPieces) + " pieces");
        System.out.println("number of solutions " + totalSolutions);
        return totalSolutions;
    }

    private boolean isCanonicalLPlacement(
            Placement placement, List<int[]> boxSymmetries) {
        long canonicalMask = placement.cellMask();
        for (int[] transformation : boxSymmetries) {
            long transformedMask = transformMask(placement.cells(), transformation);
            if (Long.compareUnsigned(transformedMask, canonicalMask) < 0) {
                canonicalMask = transformedMask;
            }
        }
        return placement.cellMask() == canonicalMask;
    }

    private boolean isCanonicalSolution(
            int[] selectedRows,
            List<Placement> rowPlacements,
            List<int[]> boxSymmetries) {
        int[] solution = new int[ROWS * COLUMNS * DEPTH];
        long lMask = 0;
        for (int selectedRow : selectedRows) {
            Placement placement = rowPlacements.get(selectedRow);
            for (int cell : placement.cells()) {
                solution[cell] = placement.piece() + 1;
            }
            if (placement.piece() == 0) {
                lMask = placement.cellMask();
            }
        }

        for (int[] transformation : boxSymmetries) {
            long transformedLMask = 0;
            int[] transformed = new int[solution.length];
            for (int cell = 0; cell < solution.length; cell++) {
                transformed[transformation[cell]] = solution[cell];
                if (solution[cell] == 1) {
                    transformedLMask |= 1L << transformation[cell];
                }
            }
            if (transformedLMask == lMask && lexicographicallyLess(transformed, solution)) {
                return false;
            }
        }
        return true;
    }

    private static boolean lexicographicallyLess(int[] left, int[] right) {
        for (int index = 0; index < left.length; index++) {
            if (left[index] != right[index]) {
                return left[index] < right[index];
            }
        }
        return false;
    }

    private static long transformMask(int[] cells, int[] transformation) {
        long result = 0;
        for (int cell : cells) {
            result |= 1L << transformation[cell];
        }
        return result;
    }

    private List<int[]> buildBoxSymmetries() {
        List<int[]> transformations = new ArrayList<>();
        int[] dimensions = {ROWS, COLUMNS, DEPTH};
        int[][] permutations = {
                {0, 1, 2}, {0, 2, 1}, {1, 0, 2},
                {1, 2, 0}, {2, 0, 1}, {2, 1, 0}
        };
        for (int[] permutation : permutations) {
            if (dimensions[0] != dimensions[permutation[0]]
                    || dimensions[1] != dimensions[permutation[1]]
                    || dimensions[2] != dimensions[permutation[2]]) {
                continue;
            }
            for (int flipMask = 0; flipMask < 8; flipMask++) {
                int[] transformation = new int[ROWS * COLUMNS * DEPTH];
                for (int cell = 0; cell < transformation.length; cell++) {
                    int row = cell / (COLUMNS * DEPTH);
                    int remainder = cell % (COLUMNS * DEPTH);
                    int[] coordinates = {row, remainder / DEPTH, remainder % DEPTH};
                    int transformedRow = transformedCoordinate(
                            coordinates, dimensions, permutation, flipMask, 0);
                    int transformedColumn = transformedCoordinate(
                            coordinates, dimensions, permutation, flipMask, 1);
                    int transformedDepth = transformedCoordinate(
                            coordinates, dimensions, permutation, flipMask, 2);
                    int transformedCell =
                            (transformedRow * COLUMNS + transformedColumn) * DEPTH
                                    + transformedDepth;
                    transformation[cell] = transformedCell;
                }
                transformations.add(transformation);
            }
        }
        return transformations;
    }

    private static int transformedCoordinate(
            int[] coordinates,
            int[] dimensions,
            int[] permutation,
            int flipMask,
            int targetAxis) {
        int coordinate = coordinates[permutation[targetAxis]];
        return (flipMask & (1 << targetAxis)) == 0
                ? coordinate
                : dimensions[targetAxis] - coordinate - 1;
    }

    private boolean solveInternal(boolean enumerateAllSolutions, boolean showSolutions) {
        this.enumerateAllSolutions = enumerateAllSolutions;
        this.showAllSolutions = showSolutions;
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
        if ((long) ROWS * COLUMNS * DEPTH == PIECES * 5L) {
            put();
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
            if (usedPieces != (1 << PIECES) - 1) {
                return false;
            }
            totalSolutions++;
            if (showAllSolutions) {
                System.out.println("solution " + totalSolutions);
                showGrid();
            }
            return !enumerateAllSolutions;
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
        if (args.length < 3 || args.length > 4
                || (args.length == 4
                && !args[3].equals("--all")
                && !args[3].equals("--count"))) {
            System.out.println("usage: Puzzle3D rows columns depth [--all|--count]");
            return;
        }
        String option = args.length == 4 ? args[3] : "";
        Puzzle3D puzzle = new Puzzle3D(
                Integer.parseInt(args[0]),
                Integer.parseInt(args[1]),
                Integer.parseInt(args[2]));
        if (option.equals("--count")) {
            puzzle.countSolutions();
        } else if (option.equals("--all")) {
            puzzle.solve(true);
        } else if (puzzle.solve()) {
            puzzle.showGrid();
        }
    }
}
