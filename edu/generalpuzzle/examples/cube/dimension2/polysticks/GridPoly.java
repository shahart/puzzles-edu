package edu.generalpuzzle.examples.cube.dimension2.polysticks;

import edu.generalpuzzle.examples.tangram.CellPartTang;
import edu.generalpuzzle.examples.tangram.EdgeTang;
import edu.generalpuzzle.infra.CellId;
import edu.generalpuzzle.infra.ICellPart;
import edu.generalpuzzle.infra.IGrid;

/**
 * Created by IntelliJ IDEA.
 * Date: 31/08/2010
 */
public class GridPoly extends IGrid {

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

    public void build(int rows, int columns) { // TODO like tangram, remove the not necessary cells, because of it is grid being rotated by 45
        grid.clear();
        currCell = null;
        leftCells = -1;

        this.rows = rows;
        this.columns = columns;

        for (int row=0; row<rows; ++row)
            for (int col=0; col<columns; ++col) {
                ICellPart cell = new CellPartPoly(new CellId(calcId(row,col)));
                add(new CellId(calcId(row,col)), cell);

                if ((row+col) % 2 == 0)
                    cell.setSpecial(1);

            }

        currCell = cells.get(0);

        for (int row=0; row<rows; ++row)
            for (int col=0; col<columns; ++col) {
                ICellPart cell = grid.get(new CellId(calcId(row,col)));
                addAndValidate(cell, EdgePoly.UP, calcId(row-1, col));
                addAndValidate(cell, EdgePoly.LEFT, calcId(row, col-1));
                addAndValidate(cell, EdgePoly.RIGHT, calcId(row, col+1));
                addAndValidate(cell, EdgePoly.DOWN, calcId(row+1, col));
                // diag
                addAndValidate(cell, EdgePoly.UP_RIGHT, calcId(row-1, col+1));
                addAndValidate(cell, EdgePoly.LEFT_UP, calcId(row-1, col-1));
                addAndValidate(cell, EdgePoly.RIGHT_DOWN, calcId(row+1, col+1));
                addAndValidate(cell, EdgePoly.DOWN_LEFT, calcId(row+1, col-1));
            }

        // remove the corners

        for (int y=0; y<4; ++y)
            for (int x=0; x<4-y; ++x)
                remove(new CellId(calcId(y,x)));

        for (int y=6; y<10; ++y)
            for (int x=0; x<y-5; ++x)
                remove(new CellId(calcId(y,x)));

        for (int y=0; y<4; ++y)
            for (int x=6+y; x<10; ++x)
                remove(new CellId(calcId(y,x)));

        for (int y=6; y<10; ++y)
            for (int x=15-y; x<10; ++x)
                remove(new CellId(calcId(y,x)));
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
                int newLocation = newLoc[ cellIdToIndex.get(calcId(row,col))];
                cell = cells.get(newLocation); //grid.get(new CellId(newLocation));
                String s = showId(cell);
                sb.append(s);
                sb.append(cell.getSpecial()  != 0 ? /*((CellPartPoly)cell).direction == 0?*/ "/" : "\\");
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

    public String toString() {
        return super.toString();
    }
//
//    public boolean canPut(IPart part) {
//        boolean canPut = super.canPut(part);
//
//        if (canPut) {
//            for (ICellPart cell: part.getCells())
//                if (((CellPartPoly)cell.getGridCell()).direction != ((CellPartPoly)cell).direction)
//                    canPut = false;
//        }
//
//        return canPut;
//    }


}
