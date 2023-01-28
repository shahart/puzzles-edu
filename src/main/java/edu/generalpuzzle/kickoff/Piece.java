package edu.generalpuzzle.kickoff;

public class Piece {
    protected static int totalFill = 0;
    static public int getTotalFill() { return totalFill; }

    private int totalThisFill = 0;
    public int getTotalThisFill() { return totalThisFill; }

    private int index;
    public int getIndex() { return index; }

    private char name = 'x';
    public char getName() { return name; }

    @Override
    public String toString() { // for debug
        return
                "id " + name + // Integer.toString(index) +
                " rotation " + (90*currRotation) +
                " used " + (row != -1);
    }

    private int []firstSquarePos;
    public int getFirstSquarePos() { return firstSquarePos[currRotation]; }

    private static int [][][]layouts;

    private int [][]rowsSet;
    private int [][]columnsSet;
    public int getRowSet(int i) { return rowsSet[currRotation][i]; }
    public int getColumnSet(int i) { return columnsSet[currRotation][i]; }

    public int getAvailRotations() { return firstSquarePos.length; }
    private int currRotation;

    public int [][]getLayout() { return layouts[currRotation]; }

    private int row = -1;
    private int column = -1;

    public Piece(int index, int [][]layout, int availRotations, int symmetric, char name) {
        this.index = index;
        this.name = name;
        currRotation = 0;

        layouts = new int[availRotations*symmetric][][];
        firstSquarePos = new int[availRotations*symmetric];
        layouts[0]  = layout;

        rowsSet = new int[availRotations*symmetric][];
        columnsSet = new int [availRotations*symmetric][];

        firstSquarePos[0] = 0;
        while (firstSquarePos[0]<layout[0].length && layout[0][firstSquarePos[0]] == 0) {
            firstSquarePos[0]++;
        }

        int maxColumns = -1;

        for (int i=0; i<layout.length; i++) {
            for (int j=0; j<layout[i].length; j++) {
                if (layout[i][j] == 1) {
                    totalFill++;
                    totalThisFill++;
                }
            }
            if (layout[i].length > maxColumns) {
                maxColumns = layout[i].length;
            }
        }

        if (availRotations > 1) {
            layouts[1] = realRotate(layouts[0], maxColumns, layout.length,1);
            if (availRotations > 2) {
                layouts[2] = realRotate(layouts[1], layout.length, maxColumns,2);
                if (availRotations > 3) {
                    layouts[3] = realRotate(layouts[2], maxColumns, layout.length,3);
                    if (availRotations > 4) {
                        System.err.println("rotations is up to 4");
                    }
                }
            }
        }

        if (symmetric == 2) {
            for (int i = 0; i < availRotations; i++) {
                layouts[i + availRotations] = copySymmetric(layouts[i]);

                firstSquarePos[i + availRotations] = 0;
                while (firstSquarePos[i + availRotations] < layouts[i + availRotations][0].length && layouts[i + availRotations][0][firstSquarePos[i + availRotations]] == 0) {
                    firstSquarePos[i + availRotations]++;
                }
            }
        }
        else if (symmetric > 2) {
            System.err.println("symmetric is up to 2");
        }

        for (int rot=0; rot<availRotations*symmetric; rot++) {
            rowsSet[rot]= new int [totalThisFill];
            columnsSet[rot]= new int [totalThisFill];
            int setSoFar = 0;
            for (int i=0; i<layouts[rot].length; i++) {
                for (int j = 0; j < layouts[rot][i].length; j++) {
                    if (layouts[rot][i][j] == 1) {
                        rowsSet[rot][setSoFar] = i;
                        columnsSet[rot][setSoFar] = j;
                        setSoFar++;
                    }
                }
            }
        }
    }

    private int [][]copySymmetric(int [][]original) {
        int rows = original.length;
        int [][]result = new int [rows][];

        for (int i=0; i<rows; i++) {
            result[i] = new int[original[rows-i-1].length];
            for (int j=0; j<result[i].length; j++) {
                result[i][j] = original[rows - i - 1][j];
            }
        }

        return result;
    }

    private int [][] realRotate(int [][]original, int rows, int columns, int index) {
        int [][]result = new int[rows][columns]; //[maxColumns][originalLayout.length];

        for (int i=0; i<columns; i++) {
            for (int j = 0; j < rows; j++) {
                try {
                    result[rows - j - 1][i] = original[i][j];
                } catch (ArrayIndexOutOfBoundsException ignored) {
                }
            }
        }

        firstSquarePos[index] = 0;
        while (firstSquarePos[index]<result[0].length && result[0][firstSquarePos[index]] == 0) {
            firstSquarePos[index]++;
        }

        return result;
    }

    public void rotate() { // vs. clock-wise
        currRotation++;
        if (currRotation == firstSquarePos.length) {
            currRotation = 0;
        }
    }

    public void setPosition(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() { return row; }
    public int getColumn() { return column; }

}
