package edu.generalpuzzle.examples.cube.dimension3;

import edu.generalpuzzle.infra.CellId;
import edu.generalpuzzle.infra.ICellPart;
import edu.generalpuzzle.infra.IGrid;

/**
 * Created by IntelliJ IDEA.
 * Date: 13/06/2008
 */
public class Grid3D extends IGrid {

    protected int rows;
    protected int columns;
    protected int height;

    public int getRows() { return rows; }
    public int getColumns() { return columns; }
    public int getHeight() { return height; }

    public int calcId(int x, int y, int z) { // public for custom use (option 6)
        return 10000*(x+1)+100*(y+1)+(z+1);
    }

    public void build3d(int rows, int columns, int height) {
        grid.clear();
        currCell = null;
        leftCells = -1;

        this.rows = rows;
        this.columns = columns;
        this.height = height;

        for (int row=0; row<rows; ++row)
            for (int col=0; col<columns; ++col)
                for (int z=0; z<height; ++z) {
                    ICellPart cell = new CellPart3D(new CellId(calcId(row,col,z)));
                    add(new CellId(calcId(row,col,z)), cell);
                }

        currCell = cells.get(0);

        for (int row=0; row<rows; ++row)
            for (int col=0; col<columns; ++col)
                for (int z=0; z<height; ++z) {
                    ICellPart cell = grid.get(new CellId(calcId(row,col,z)));
                    addAndValidate(cell, Edge3D.UP, calcId(row-1, col, z));
                    addAndValidate(cell, Edge3D.LEFT, calcId(row, col-1, z));
                    addAndValidate(cell, Edge3D.RIGHT, calcId(row, col+1, z));
                    addAndValidate(cell, Edge3D.DOWN, calcId(row+1, col, z));
                    addAndValidate(cell, Edge3D.FRONT, calcId(row, col, z-1));
                    addAndValidate(cell, Edge3D.BACK, calcId(row, col, z+1));
                }
    }

    @Override
    public StringBuffer showBuf(int newLoc[]) {

        StringBuffer sb = new StringBuffer(cells.size()*5); // *2 by presentation, *3 by showId

        sb.append("\n");
        sb.append(super.presentation(newLoc));
        sb.append("\n");
        for (int z=0; z<height; ++ z) {
            sb.append("\n");

            // same as GridSpheresPyramid-show
            for (int row=0; row<rows; ++ row) {
                for (int col=0;  col<columns; ++col) {
                    ICellPart cell;// = grid.get(new CellId(calcId(row,col,z)));
                    int newLocation = newLoc[ cellIdToIndex.get(calcId(row,col,z))];
                    cell = cells.get(newLocation); //grid.get(new CellId(newLocation));
                    String s = showId(cell);
                    sb.append(s);
                    // System.out.print(newLocation);// + " " + cell2.getId().getId() + " ");
                }
                sb.append("\n");
            }
        }

        return sb;
    }

    @Override
    public void show(int newLoc[]) {

        System.out.print(showBuf(newLoc));
    }

}
