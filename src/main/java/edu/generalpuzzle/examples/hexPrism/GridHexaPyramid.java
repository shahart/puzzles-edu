package edu.generalpuzzle.examples.hexPrism;

import edu.generalpuzzle.infra.CellId;
import edu.generalpuzzle.infra.ICellPart;
import edu.generalpuzzle.infra.IGrid;

/**
 * Created by IntelliJ IDEA.
 * Date: 13/06/2008
 */
public class GridHexaPyramid extends IGrid {

    protected int size;
    protected int depth;

    @Override
    public int calcId(int x, int y, int z) {
        return 10000*(z+1)+100*(y+1)+(x+1);
    }

    public void buildPyramid(int size, int depth) {
        grid.clear();
        currCell = null;
        leftCells = -1;

        this.size = size;
        this.depth = depth;

        for (int z=0; z<depth; ++z)
            for (int y=0; y<size; ++y)
                for (int x=0; x<size-y; ++x) {
                    ICellPart cell = new CellPartHexPrism(new CellId(calcId(x,y,z)));
                    add(new CellId(calcId(x,y,z)), cell);

                    if ((x == 0 && (y == 0 || y == 4)) || (x == 4) || (x == 1 && (y == 1 || y == 2) || (x == 2 && y == 1)))
                        cell.setSpecial(1);
                }

        currCell = cells.get(0);

        for (int z=0; z<depth; ++z) {
            for (int row=0; row<size; ++row)
                for (int col=0; col<size-row; ++col) {
                    ICellPart cell = grid.get(new CellId(calcId(col,row,z)));
                    addAndValidate(cell, HexagonalEdge.WEST_M120, calcId(col,row-1,z));
                    addAndValidate(cell, HexagonalEdge.WEST, calcId(col-1, row,z));
                    addAndValidate(cell, HexagonalEdge.WEST_M60, calcId(col+1, row-1,z));
                    addAndValidate(cell, HexagonalEdge.BACK, calcId(col, row,z-1));
                }
        }

    }

    @Override
    public StringBuffer showBuf(int newLoc[]) { // TODO the 2nd show for the snowFlake 

        StringBuffer sb = new StringBuffer(cells.size()*5); // *2 by presentation, *3 by showId

        sb.append("\n");
        sb.append(super.presentation(newLoc));
        sb.append("\n");
        for (int z=0; z<depth; ++ z) {
            sb.append("\n");
            for (int row=0; row<size; ++ row) {
                for (int col=0;  col<size-row; ++col) {
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

    public void buildHexCluster() {
        grid.clear();
        currCell = null;
        leftCells = -1;

        this.size = 4;
        this.depth = 1;

        for (int z=0; z<depth; ++z) {
            for (int y=0; y<size; ++y)
                for (int x=0; x<size+y; ++x) {
                    ICellPart cell = new CellPartHexPrism(new CellId(calcId(x,y,z)));
                    add(new CellId(calcId(x,y,z)), cell);
                }
            for (int y=size; y<size+size-1; ++y)
                for (int x=0; x<size+size-2-(y-size); ++x) {
                    ICellPart cell = new CellPartHexPrism(new CellId(calcId(x+y-size+1,y,z)));
                    add(new CellId(calcId(x+y-size+1,y,z)), cell);
                }
        }

        currCell = cells.get(0);

        for (int z=0; z<depth; ++z) {
            for (int row=0; row<size; ++row)
                for (int col=0; col<size+row; ++col) {
                    ICellPart cell = grid.get(new CellId(calcId(col,row,z)));
                    addAndValidate(cell, HexagonalEdge.WEST_M120, calcId(col,row+1,z));
                    addAndValidate(cell, HexagonalEdge.EAST, calcId(col+1, row,z));
                    addAndValidate(cell, HexagonalEdge.WEST_M60, calcId(col+1, row+1,z));
                }
            for (int row=size; row<size+size-1; ++row)
                for (int col=0; col<size+size-2-(row-size); ++col) {
                    ICellPart cell = grid.get(new CellId(calcId(col+row-size+1,row,z)));
                    addAndValidate(cell, HexagonalEdge.WEST_M120, calcId(col+row-size+1,row+1,z));
                    addAndValidate(cell, HexagonalEdge.EAST, calcId(col+row-size+1+1, row,z));
                    addAndValidate(cell, HexagonalEdge.WEST_M60, calcId(col+row-size+1+1, row+1,z));
                }
        }

    }

//    public void buildSnowFlake() {
//        grid.clear();
//        currCell = null;
//        leftCells = -1;
//
//        this.size = 5;
//        this.depth = 1;
//
//        for (int z=0; z<depth; ++z) {
//            for (int y=0; y<size; ++y)
//                for (int x=0; x<size+y; ++x) {
//                    ICellPart cell = new CellPartHexPrism(new CellId(calcId(x,y,z)));
//                    add(new CellId(calcId(x,y,z)), cell);
//                }
//            for (int y=size; y<size+size-1; ++y)
//                for (int x=0; x<size+size-2-(y-size); ++x) {
//                    ICellPart cell = new CellPartHexPrism(new CellId(calcId(x+y-size+1,y,z)));
//                    add(new CellId(calcId(x+y-size+1,y,z)), cell);
//                }
//        }
//
//        currCell = cells.get(0);
//
//        for (int z=0; z<depth; ++z) {
//            for (int row=0; row<size; ++row)
//                for (int col=0; col<size+row; ++col) {
//                    ICellPart cell = grid.get(new CellId(calcId(col,row,z)));
//                    addAndValidate(cell, HexagonalEdge.WEST_M120, calcId(col,row+1,z));
//                    addAndValidate(cell, HexagonalEdge.EAST, calcId(col+1, row,z));
//                    addAndValidate(cell, HexagonalEdge.WEST_M60, calcId(col+1, row+1,z));
//                }
//            for (int row=size; row<size+size-1; ++row)
//                for (int col=0; col<size+size-2-(row-size); ++col) {
//                    ICellPart cell = grid.get(new CellId(calcId(col+row-size+1,row,z)));
//                    addAndValidate(cell, HexagonalEdge.WEST_M120, calcId(col+row-size+1,row+1,z));
//                    addAndValidate(cell, HexagonalEdge.EAST, calcId(col+row-size+1+1, row,z));
//                    addAndValidate(cell, HexagonalEdge.WEST_M60, calcId(col+row-size+1+1, row+1,z));
//                }
//        }
//
//        setPlaceHolder((calcId(1,0,0)));
//        setPlaceHolder((calcId(2,0,0)));
//        setPlaceHolder((calcId(3,0,0)));
////        remove(new CellId(calcId(1,0,0)));
//
//
//    }

}
