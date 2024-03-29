package edu.generalpuzzle.kickoff;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.text.NumberFormat;

// TODO tests
// TODO Piece to self compute number of its variants (symmetric, rotations)

/**
 * POC before diving into the adventure
 * <p>
 * Tiling polyominoes in a finite container.
 * A simple back-tracking algorithm.
 * Next into the thesis:
 * - support more than a planar orthogonal lattice (pie, spheres, hex...);
 * - a language to describe a puzzle (beanShell script - and at 2023 did also config/2d_ascii)
 * <p>
 * Date: 05/02/2008
 * <pre>
 * 12 5 ->
 * 1010
 * I T V V V
 * I T T T V
 * I T W W V
 * I W W P P
 * I W Z P P
 * Z Z Z P Y
 * Z N N N Y
 * N N X Y Y
 * L X X X Y
 * L F X U U
 * L F F F U
 * L L F U U
 * I T V W P Z Y N X L F U
 * elapsed time in seconds 4 (at 2020 on i7, was 193 at 2007 hardware)
 * tried 3,537,414 pieces
 * at 76,900 pieces per second
 * TODO concurrency
 * 5 12 -> Minutes
 * <b>Profiling results</b>
 * 39.7% Puzzle2D.canPut
 * 19    Puzzle2D.put
 * 5.4   Piece.getFirstSquarePos
 * 4     Puzzle2D.putPieceIndex
 * 3.6   Piece.getLayout
 * 2.7   Puzzle2D.removeLast
 * 2.5   Piece.rotate
 * </pre>
 */
public class Puzzle2D {
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

    Puzzle2D(int rows, int columns)
    {
        ROWS = rows;
        COLUMNS = columns;

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
            System.out.println("warning- using first pieces from the whole set");
        }

        // prepare grid (the board)

        if (ROWS == 0 && COLUMNS == 0) {
            buildFromFile();
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
        System.out.println("Found " + ROWS + " rows, " + COLUMNS + " cols, with total of cells " + availInGrid);
        showGrid();
    }

    private void buildFromFile() {

        try {
            List<String> lines = Files.readAllLines(Path.of("myPzl.txt")); // todo another argument
            // Grid2DExamples
            String header = lines.get(0);
            if (!header.startsWith("#")) {
                System.err.println("Found no grid header");
            }
            header = header.substring(header.lastIndexOf("#")+1).trim();
            ROWS = Integer.parseInt(header.split(",")[0].trim());
            COLUMNS = Integer.parseInt(header.split(",")[1].trim());
            grid = new int[ROWS][COLUMNS];
            for (int row = 0; row < ROWS; ++row) {
                boolean cellFound = false;
                String line = lines.get(row+1);
                int col;
                for (col = 0; col < line.length(); ++col) {
                    if (line.charAt(col) == 'X' || line.charAt(col) == 'x') {
                        grid[row][col] = 0;
                        cellFound = true;
                    } else if (line.charAt(col) == ' ') {
                        grid[row][col] = -1;
                    }
                }
                if (col > COLUMNS) {
                    System.err.println("Declared rows " + COLUMNS + "/columns " + ROWS + " misconfig");
                }
                if (!cellFound) {
                    if (col < COLUMNS) {
                        System.err.println("Declared rows " + COLUMNS + "/columns " + ROWS + " misconfig");
                    }
                    break;
                }
            }
            // Parts2D_Examples
            names = "";
            int pieceIdx = 0;
            int rotations = 4;
            int symmetric = 2;
            List<String> pieceLines = new ArrayList<>();
            for (int i=ROWS+1; i<lines.size(); ++i) {
                String line = lines.get(i);
                if (line.startsWith("#end of grid. Pieces")) {
                    PIECES = Integer.parseInt(line.substring("#end of grid. Pieces".length()));
                    System.out.println("Found pieces " + PIECES);
                    pieces = new Piece[PIECES];
                    for (int k=0; k<PIECES; ++k) {
                        piecesIndices.add(k);
                    }
                }
                else if (line.toLowerCase().startsWith("#piece")) {
                    if (! pieceLines.isEmpty()) {
                        int[][] layout;
                        layout = new int[pieceLines.size()][];
                        for (int j = 0; j < pieceLines.size(); ++j) {
                            layout[j] = new int[pieceLines.get(j).length()];
                            for (int k = 0; k < pieceLines.get(j).length(); ++k) {
                                layout[j][k] = pieceLines.get(j).charAt(k) != ' ' ? 1 : 0;
                            }
                        }
                        pieces[pieceIdx] = new Piece(pieceIdx, layout, rotations, symmetric, names.charAt(pieceIdx));
                        ++pieceIdx;
                        pieceLines.clear();
                    }
                    String[] s = line.split(" ");
                    if (s.length > 1) {
                        if (s[1].startsWith("R")) {
                            rotations = Integer.parseInt(s[1].substring("R".length()));
                            if (rotations < 1 || rotations > 4) {
                                System.err.println("Wrong rotations 1..4");
                            }

                        }
                        if (s.length > 2) {
                            if (s[2].startsWith("S")) {
                                symmetric = Integer.parseInt(s[2].substring("S".length()));
                                if (symmetric != 1 && symmetric != 2) {
                                    System.err.println("Wrong symmetric 1..2");
                                }
                            }
                        }
                    }
                    if (line.toLowerCase().startsWith("#piece-end")) {
                        break;
                    }
                    names += line.charAt("#Piece".length());
                    System.out.println("Parsing piece " + line.charAt("#Piece".length()));
                }
                else if (line.length() > 0) {
                    if (line.toLowerCase().startsWith("#unique=")) {
                        // char uniqueId = line.charAt("#unique=".length());
                        System.out.println(line);
                        rotations = 1;
                        symmetric = 1;
                    }
                    else {
                        pieceLines.add(line);
                    }
                }
            }
            // todo ChatGPT said to sort by piece size
            Collections.shuffle(piecesIndices);
            System.out.println("shuffle: " + piecesIndices);
            Piece []piecesCopy = new Piece[PIECES];
            System.arraycopy(pieces, 0, piecesCopy, 0, piecesIndices.size());
            for (int i=0; i<piecesIndices.size(); ++i) {
                pieces[i]= piecesCopy[piecesIndices.get(i)];
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

/*
    public void putIter()
    {
        int k = 0;
        List<Integer> []S = new ArrayList[PIECES];

        S[0] = new ArrayList<Integer>(piecesIndices);

        while (k >= 0) {
            while (S[k].size() > 0) {
                Integer piece = S[k].get(0);
                //if (canPut(piece)) {
                    S[k].remove(0);
                    solutionArr[k] = piece;
                    if (k == PIECES-1) {
                        // System.out.println(solutionArr.toString());
                        count++;
                    }
                //}
                else {
                k += 1;
                S[k] = new ArrayList<Integer>(piecesIndices);
                for (int i= 0; i< k; i++)
                    if (S[k].contains(solutionArr[i]))
                        S[k].remove(solutionArr[i]);
                if (canPut(piece)) {
                }
                }
            }
            k -= 1;
        }
    }
*/

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

    public void showPieces()
    {
        String line = "";
        for (int i=0; i<this.PIECES - this.piecesIndices.size(); i++) {
            line += names.charAt(solution[i]) + " ";
        }
        System.out.println(line);
    }

    public void put()
    {
        int leftPieces = piecesIndices.size();

        if (leftPieces == 0) { //  && availInGrid == 0) {
            totalSolutions++;
            System.out.println("\n"+ totalSolutions);
            if (totalSolutions == 1) {
                System.out.println("Found a solution");
                showGrid();
                showPieces();
            }
        }

        if (totalSolutions >= 1) {
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

                    if (triedPieces % 50000 == 0 /* || leftPieces <= 2 */) {
                        this.showGrid();
                        this.showPieces();
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

    public boolean solve() {
        long start = System.currentTimeMillis();

        if (totalFillInGrid != Piece.getTotalFill()) {
            System.out.println("invalid config, grid " + totalFillInGrid + " pieces " + Piece.getTotalFill());
        } else {
            put();
        }

        long elapsedTime = (System.currentTimeMillis() - start) / 1000;
        NumberFormat nf = NumberFormat.getInstance();

        System.out.println("\nelapsed time in seconds " + elapsedTime);
        System.out.println("tried " + nf.format(triedPieces) + " pieces");

        if (elapsedTime > 0) {
            System.out.println("at " + nf.format(triedPieces / elapsedTime) + " pieces per second");
        }

        System.out.print("number of solutions ");
        System.out.println(totalSolutions * ((ROWS == COLUMNS) ? 8 : 4)); // TODO compute this symmetric magic number

        return totalSolutions >= 1;
    }

    public static void main(String[] args)
    {
        if (args.length < 2) {
            System.out.println("usage: MAIN rows columns");
        }
        else {
            Puzzle2D puzzle2d = new Puzzle2D(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
            puzzle2d.solve();
        }
    }
}
