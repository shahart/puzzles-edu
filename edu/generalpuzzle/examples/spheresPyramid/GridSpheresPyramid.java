package edu.generalpuzzle.examples.spheresPyramid;

import edu.generalpuzzle.infra.CellId;
import edu.generalpuzzle.infra.ICellPart;
import edu.generalpuzzle.infra.IGrid;

/**
 * Created by IntelliJ IDEA.
 * Date: 13/06/2008
 */
public class GridSpheresPyramid extends IGrid {

    protected int size;

    public int calcId(int x, int y, int z) {
        return 100*(z+1)+10*(y+1)+(x+1);
    }


    public void buildPyramid(int size) {
        grid.clear();
        currCell = null;
        leftCells = -1;

        this.size = size;

        for (int z=0; z<size; ++z)
            for (int y=0; y<size-z; ++y)
                for (int x=0; x<size-z-y; ++x) {
                    ICellPart cell = new CellPartSphere(new CellId(calcId(x,y,z)));
                    add(new CellId(calcId(x,y,z)), cell);
                }

        currCell = cells.get(0);

        for (int z=0; z<size; ++z) {
            for (int row=0; row<size-z; ++row)
                for (int col=0; col<size-z-row; ++col) {
                    ICellPart cell = grid.get(new CellId(calcId(col,row,z)));
                    addAndValidate(cell, EdgeSphere.WEST_M120, calcId(col,row-1,z));
                    addAndValidate(cell, EdgeSphere.WEST, calcId(col-1, row,z));
                    addAndValidate(cell, EdgeSphere.WEST_M60, calcId(col+1, row-1,z));
                    addAndValidate(cell, EdgeSphere.BACK_NORTH, calcId(col, row+1,z-1));
                    addAndValidate(cell, EdgeSphere.BACK_M150, calcId(col, row,z-1));
                    addAndValidate(cell, EdgeSphere.BACK_M30, calcId(col+1, row,z-1));
                }
        }

    }

    @Override
    public StringBuffer showBuf(int newLoc[]) {

        StringBuffer sb = new StringBuffer(cells.size()*5); // *2 by presentation, *3 by showId

        sb.append("\n");
        sb.append(super.presentation(newLoc));
        sb.append("\n");
        for (int z=0; z<size; ++ z) {
            sb.append("\n");
            for (int row=0; row<size-z; ++ row) {
                for (int col=0;  col<size-z-row; ++col) {
                    ICellPart cell;// = grid.get(new CellId(calcId(col,row,z)));
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
