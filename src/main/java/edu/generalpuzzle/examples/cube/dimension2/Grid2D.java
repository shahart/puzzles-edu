package edu.generalpuzzle.examples.cube.dimension2;

import edu.generalpuzzle.infra.CellId;
import edu.generalpuzzle.infra.ICellPart;
import edu.generalpuzzle.infra.IGrid;

/**
 * Created by IntelliJ IDEA.
 * Date: 13/06/2008
 */
public class Grid2D extends IGrid {

    protected int rows;
    protected int columns;

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    // was protected, public for removeCorner
    public int calcId(int row, int col) {
        return 100*(row+1)+(col+1);
    }

    public void build2d(int rows, int columns) {
        grid.clear();
        currCell = null;
        leftCells = -1;

        this.rows = rows;
        this.columns = columns;

        for (int row=0; row<rows; ++row)
            for (int col=0; col<columns; ++col) {
                ICellPart cell = new CellPart2D(new CellId(calcId(row,col)));
                add(new CellId(calcId(row,col)), cell);
            }

        currCell = cells.get(0);

        for (int row=0; row<rows; ++row)
            for (int col=0; col<columns; ++col) {
                ICellPart cell = grid.get(new CellId(calcId(row,col)));
                addAndValidate(cell, Edge2D.UP, calcId(row-1, col));
                addAndValidate(cell, Edge2D.LEFT, calcId(row, col-1));
                addAndValidate(cell, Edge2D.RIGHT, calcId(row, col+1));
                addAndValidate(cell, Edge2D.DOWN, calcId(row+1, col));
            }

    }

    @Override
    public StringBuffer showBuf(int newLoc[]) {
        StringBuffer sb = new StringBuffer(cells.size()*5); // *2 by presentation, *3 by showId

        sb.append("\n");
        sb.append(super.presentation(newLoc));
        sb.append("\n\n");
        for (int row=0; row<rows; ++ row) {
            for (int col=0;  col<columns; ++col) {
                ICellPart cell;// = grid.get(new CellId(calcId(row,col)));
                if (cellIdToIndex.get(calcId(row,col)) == null) {
                    System.err.println("Something bad, no calcId row " + row + " col " + col); // todo why ?!
                    return sb;
                }
                int newLocation = newLoc[ cellIdToIndex.get(calcId(row,col))];
                cell = cells.get(newLocation); //grid.get(new CellId(newLocation));
                String s = showId(cell);
                sb.append(s);
                // System.out.print(newLocation);// + " " + cell2.getId().getId() + " ");
            }
            sb.append("\n");
        }

        return sb;
    }

    @Override
    public void show(int newLoc[]) {
        System.out.print(showBuf(newLoc));
    }

    @Override
    public String toString() {
        return super.toString();
    }


}
