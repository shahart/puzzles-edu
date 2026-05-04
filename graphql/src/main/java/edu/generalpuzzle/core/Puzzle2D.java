package edu.generalpuzzle.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.text.NumberFormat;

/**
 * 
 * This is a backtracking solution for the 2D puzzle of filling in a grid with given pieces. The pieces are polyominoes of 5 squares.
 * 
 * Taken from src/../edu/generalpuzzle/kickoff package with some changes, like logger, timeout, and fix missing init.
 *
 * elapsed time in seconds 2 (was 4 at 2020 on i7, was 193 at 2007 hardware)
 * tried 3,537,414 pieces
 * at 1,768,707 pieces per second
 */
public class Puzzle2D {

    private static final Logger LOGGER = LoggerFactory.getLogger(Puzzle2D.class);

    public int PIECES = 12; // 12 polyominoes of 5

    // target: 12*5 + 4 = 8 * 8

    public int ROWS; // 6
    public int COLUMNS; // 10

    public int totalSolutions = 0;
    public int triedPieces = 0;

    // position for the next piece
    public int row = 0;
    public int column = 0;

    public List<Integer>  piecesIndices = new LinkedList<>();
    public Integer []solution = new Integer[PIECES];
    public Piece []pieces = new Piece[PIECES];
    String names;

    public int [][]grid;

    public Piece currPiece;
    // public int [][]currPieceLayout;

    private int totalFillInGrid;
    private int availInGrid;
	
	private boolean all;
    private List<String> allSolutions = new ArrayList<>();

    public Puzzle2D(int rows, int columns, boolean all)
    {
        ROWS = rows;
        COLUMNS = columns;
		this.all = all;

        grid = new int[ROWS][COLUMNS];

        // prepare pieces

        int [][][]allPieces = {

//                {{1,1},{1},{1,1}},
//                {{0,1,1},{1,1}},
//                {{1,1,1},{0,1}},
//                {{0,1,1},{1,1}},
//                {{1,1},{1}},
//                {{1,1}},
//                {{0,0,1},{1,1,1}},
//                {{1,1},{1,1}},
//                {{1,1}},
//                {{1,1,1,1}},

                {{1},{1},{1},{1,1}},        //4-2 (2)   L sym = indicates it is symmetric // TODO write flip
                {{1,1},{1},{1,1}},          //4-1 (5)   U
                {{0,1,1},{1,1},{0,1}},      //4-2 (9)   F sym - used to elimination "dups"
                {{0,1},{1,1,1},{0,1}},      //1-1 (6)   X
                {{1,1,1,1},{0,0,1}},        //4-2 (3)   Y sym
                {{0,1},{1,1},{1},{1}},      //4-2 (12)  N sym
                {{0,0,1},{0,1,1},{1,1}},    //4-1 (7)   W
                {{1},{1,1},{1,1}},          //4-2 (4)   P sym
                {{0,0,1},{1,1,1},{1}},      //2-2 (11)  Z sym ??
                {{0,0,1},{0,0,1},{1,1,1}},  //4-1 (10)  V
                {{0,0,1},{1,1,1},{0,0,1}},  //4-1 (8)   T
                {{1,1,1,1,1}},              //2-1 (1)   I
        };

        int []rotations = { 4,4,2/*4*/,1,4,4,4,4,2,4,4,2 };
        int []symmetric = { 2,1,1/*2*/,1,2,2,1,2,2,1,1,1 };
        names = "LUFXYNWPZVTI";

        if (ROWS == COLUMNS && ROWS == 8) {
            rotations[2] = 1;
        }

        if (allPieces.length > PIECES) {
            LOGGER.warn("Id " + ROWS + "_" + COLUMNS + ". Warning- using first pieces from the whole set");
        }

        // prepare grid (the board)

        if (ROWS == 0 && COLUMNS == 0) {
            throw new IllegalStateException("not supported yet");
        }
        else {
            for (int i=0; i<PIECES/*allPieces.length*/; i++) {
                int piece = i;
                piecesIndices.add(piece);
                pieces[i]= new Piece(piece, allPieces[i], rotations[i], symmetric[i], names.charAt(i));
            }
            if (ROWS == COLUMNS && ROWS == 8) {
                grid[0][0]=-1;
                grid[7][0]=-1;
                grid[0][7]=-1;
                grid[7][7]=-1;
            }
            while (grid[0][column] == -1) {
                column++;
            }
        }

        totalFillInGrid = ROWS * COLUMNS;
        for (int[] ints : grid) {
            for (int anInt : ints) {
                if (anInt == -1) {
                    totalFillInGrid--;
                }
            }
        }

        availInGrid = totalFillInGrid;
        LOGGER.info("Found " + ROWS + " rows, " + COLUMNS + " cols, with total of cells " + availInGrid);
        showGrid();
    }

    public List<String> getAllSolutions() {
        return allSolutions;
    }

    public void showGrid()
    {
        System.out.println(/*(new Date().getTime() - this.start +*/ " [msec] showGrid. Tried Pieces " + this.triedPieces + " leftPieces " + this.piecesIndices.size());
        for (int i=0; i<ROWS; i++) {
            for (int j=0;  j<COLUMNS; j++) {
                if (grid[i][j] == -1) {
                    System.out.print("*  ");
                } else if (/*totalSolutions == 0 || */ grid[i][j] == 0) {
                    System.out.print("-  ");
                } else {
                    System.out.print(names.charAt(grid[i][j] - 1) + " ");
                }
            }
            System.out.println();
        }
    }

    public String getSolution() {
        String line = "";
        for (int i = 0; i < this.PIECES - this.piecesIndices.size(); i++) {
            line += names.charAt(solution[i]) + " ";
        }
        return line;
    }

    public void showPieces() {
        LOGGER.info(getSolution());
    }

    public void put()
    {
        int leftPieces = piecesIndices.size();

        if (leftPieces == 0) { //  && availInGrid == 0) {
            totalSolutions++;
            LOGGER.info("totalSolutions {}", totalSolutions);
            allSolutions.add(getSolution());
//            if (!all && totalSolutions == 1) {
                LOGGER.info("Found a solution");
                showGrid();
                showPieces();
//            }
        }

        if (!all && totalSolutions >= 1) {
            return;
        }

        int []rowsSet = new int[5]; // TODO chng 5 to const
         int []columnsSet = new int[5];

        for (int i=0; i< leftPieces; i++) {
            int piece = piecesIndices.get(i);
            currPiece = pieces[piece];
            for (int r = pieces[piece].getAvailRotations(); r > 0; r--, currPiece.rotate()) {
                // currPieceLayout = currPiece.getLayout();
                if (canPut(rowsSet, columnsSet)) {
                    piecesIndices.remove(i);
                    solution[PIECES-leftPieces] = piece; // solution.add(piece);
                    putCurrPiece(rowsSet, columnsSet);

                    if (triedPieces % 50_000 == 0 /* || leftPieces <= 2 */) {
                        this.showGrid();
                        this.showPieces();

                        if (Thread.currentThread().isInterrupted()) {
                            LOGGER.warn("Id " + ROWS + "_" + COLUMNS + ". Signaled timed out! totalSolutions " + totalSolutions);
                            return;
                        }
                    }
                    // this.showPieces();

                    put(); // the recurse
                    removeLast(piece, rowsSet, columnsSet);
                    //solution.remove(piece); // like removeLast
                    piecesIndices.add(i, piece);
                }
            }
        }
    }

    private void putCurrPiece(int []rowsSet, int []columnsSet) { //Integer piece) {

        currPiece.setPosition(row, column);

        int currIndex = currPiece.getIndex()+1;
        for (int i=0; i<currPiece.getTotalThisFill(); i++) {
            grid[rowsSet[i]][columnsSet[i]] = currIndex;
        }

        // find next avail free position
        goForward();

        availInGrid -= currPiece.getTotalThisFill();
    }

    private void goForward() {
        for (; row<ROWS; row++) {
            for (; column<COLUMNS; column++) {
                if (grid[row][column] == 0) {
                    return;
                }
            }
            column = 0;
        }
    }

    private void removeLast(Integer piece, int []rowsSet, int []columnsSet) {
        currPiece = pieces[piece];
        // currPieceLayout = currPiece.getLayout();

        // getPosition
        row = currPiece.getRow();
        column = currPiece.getColumn();

        // for debug- currPiece.setPosition(-1, -1);

        for (int i=0; i<currPiece.getTotalThisFill(); i++) {
            grid[rowsSet[i]][columnsSet[i]] = 0;
        }
/*
        for (int i=0, rowi=row; i<currPieceLayout.length; i++, rowi++) {
            int columnj = column-currPiece.getFirstSquarePos();
            for (int j=0; j<currPieceLayout[i].length; j++, columnj++)
                if (currPieceLayout[i][j] == 1)
                    grid[rowi][columnj] = 0;
        }
*/
        availInGrid += currPiece.getTotalThisFill();
    }

    public boolean canPut(int []rowsSet, int []columnsSet) {
        try {
            int setSoFar = 0;
            int columnj;
            int j = currPiece.getFirstSquarePos();
            int columnjj = column - j;
            int rowi;

            for (int i=0; i<currPiece.getTotalThisFill(); i++) {
                rowi = row+currPiece.getRowSet(setSoFar);
                columnj = columnjj+currPiece.getColumnSet(setSoFar); // column+currPiece.getColumnSet(setSoFar)-j;
                if (grid[rowi][columnj] != 0) {
//            for (int i=0, rowi=row; i<currPieceLayout.length; i++, rowi++) {
//                for (; j<currPieceLayout[i].length; j++, columnj++)
//                    if (currPieceLayout[i][j] == 1)
//                        if (/*columnj >= COLUMNS || columnj < 0 || rowi >= ROWS*/
//                                grid[rowi][columnj] != 0)
                            return false;
                        }
                        else {
                            rowsSet[setSoFar] = rowi;
                            columnsSet[setSoFar] = columnj;
                            setSoFar++;
                        }
                //columnj = column-currPiece.getFirstSquarePos();
                //j = 0;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }

        triedPieces++;

        return true;
    }

    public int solve() {
        long start = System.currentTimeMillis();

        if (totalFillInGrid != Piece.getTotalFill()) {
            LOGGER.error("Id " + ROWS + "_" + COLUMNS + ". Invalid config, grid {} pieces {}", totalFillInGrid, Piece.getTotalFill());
        } else {

            Piece.totalFill = 0;
            LOGGER.info("Starting rows {} cols {}", ROWS, COLUMNS);

            put();
        }

        long elapsedTime = (System.currentTimeMillis() - start) / 1000;
        NumberFormat nf = NumberFormat.getInstance();

//        System.out.println("\nelapsed time in seconds " + elapsedTime);
        LOGGER.info("tried {} pieces", nf.format(triedPieces));

        if (elapsedTime > 0) {
            LOGGER.info("at {} pieces per sec", nf.format(triedPieces / elapsedTime));
        }

        LOGGER.info("number of solutions {}", totalSolutions * ((ROWS == COLUMNS) ? 8 : 4)); // TODO compute this symmetric magic number

        return totalSolutions;
    }

}
