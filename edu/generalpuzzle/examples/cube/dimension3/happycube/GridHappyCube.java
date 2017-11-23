package edu.generalpuzzle.examples.cube.dimension3.happycube;

import edu.generalpuzzle.examples.cube.dimension3.Edge3D;
import edu.generalpuzzle.examples.cube.dimension3.CellPart3D;
import edu.generalpuzzle.infra.CellId;
import edu.generalpuzzle.infra.ICellPart;
import edu.generalpuzzle.infra.IGrid;

/**
 * Created by IntelliJ IDEA.
 * Date: 27/06/2008
 */
public class GridHappyCube extends IGrid {

    public int calcId(int x, int y, int z) {
        return 100*(x+1)+10*(y+1)+(z+1);
    }

    public void build() {
        grid.clear();
        currCell = null;
        leftCells = -1;

        for (int row=0; row<5; ++row)
            for (int col=0; col<5; ++col)
                for (int z=0; z<5; ++z) {

                    int condi=0;
                    if (col>0 && col<4) condi++;
                    if (row>0 && row<4) condi++;
                    if (z>0 && z<4) condi++;

                    if (condi < 3) { //2
                        ICellPart cell = new CellPart3D(new CellId(calcId(row,col,z)));
                        add(new CellId(calcId(row,col,z)), cell);
                    }
                }

        currCell = cells.get(0);

        for (int row=0; row<5; ++row)
            for (int col=0; col<5; ++col)
                for (int z=0; z<5; ++z) {
                    ICellPart cell = grid.get(new CellId(calcId(row,col,z)));
                    if (cell != null) {
                        addAndValidate(cell, Edge3D.UP, calcId(row-1, col, z));
                        addAndValidate(cell, Edge3D.LEFT, calcId(row, col-1, z));
                        addAndValidate(cell, Edge3D.RIGHT, calcId(row, col+1, z));
                        addAndValidate(cell, Edge3D.DOWN, calcId(row+1, col, z));
                        addAndValidate(cell, Edge3D.FRONT, calcId(row, col, z-1));
                        addAndValidate(cell, Edge3D.BACK, calcId(row, col, z+1));
                    }
                }

    }

    @Override
    public StringBuffer showBuf(int newLoc[]) {

        StringBuffer sb = new StringBuffer(cells.size()*5); // *2 by presentation, *3 by showId

        sb.append("\n");
        sb.append(super.presentation(newLoc));
        sb.append("\n");
        for (int z=0; z<5; ++ z) {
            sb.append("\n");

            // same as GridSpheresPyramid-show
            for ( int row=0; row<5; ++ row) {
                for (int col=0;  col<5; ++col) {
                    ICellPart cell;// = grid.get(new CellId(calcId(row,col,z)));
                    Integer idx = cellIdToIndex.get(calcId(row,col,z));
                    if (idx != null) {
                        int newLocation = newLoc[ idx];
                        cell = cells.get(newLocation); //grid.get(new CellId(newLocation));
                        String s = showId(cell);
                        sb.append(s);
                    }
                    else
                        sb.append("  ");
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
