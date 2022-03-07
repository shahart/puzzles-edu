package edu.generalpuzzle.examples.tangram;

import edu.generalpuzzle.infra.CellId;
import edu.generalpuzzle.infra.ICellPart;
import edu.generalpuzzle.infra.IGrid;
import edu.generalpuzzle.infra.IPart;

/**
 * Created by IntelliJ IDEA.
 * Date: 13/06/2008
 */
public class GridTang extends IGrid {

    protected int sizeX, sizeY;

    @Override
    public int calcId(int x, int y, int z) { // TODO 10->100, 100>10000. change also in the EdgeTang::mark
        return 1*(z+1)+10*(x+1)+100*(y+1);
    }

//    private void privateAdd(int x, int y, int direction) {
//        ICellPart cell = new CellPartTang(new CellId(calcId(x,y,direction)));
//        ((CellPartTang)cell).direction = direction;
//        add(new CellId(calcId(x,y,direction)), cell);
//    }

    public void buildAnother() {

        buildSquare(4,3);

        for (int y=0; y<sizeY; ++y)
            for (int x=0; x<sizeX; ++x)
                if ((x == 0 || x == 2) && (y == 0 || y == 3))
                    for (int z=0; z<8; ++z) 
                        remove(new CellId(calcId(x,y,z)));

        for (int z=5; z<3+8; ++z)
            remove(new CellId(calcId(1,0,z%8)));

        for (int z=5; z<1+8; ++z)
            remove(new CellId(calcId(0,1,z%8)));

        for (int z=7; z<3+8; ++z)
            remove(new CellId(calcId(2,1,z%8)));

        for (int z=3; z<7; ++z)
            remove(new CellId(calcId(0,2,z%8)));

        for (int z=1; z<5; ++z)
            remove(new CellId(calcId(2,2,z%8)));

        for (int z=1; z<7; ++z)
            remove(new CellId(calcId(1,3,z%8)));

        remove(new CellId((calcId(1,1,4))));

        for (int z=5; z<8; ++z)
            remove(new CellId(calcId(1,2,z%8)));


    }

    public void buildDigit4() {
	
        buildSquare(3, 4);
		
        for (int z=5; z<3+8; ++z)
            remove(new CellId(calcId(0,0,z%8)));
        for (int z=3; z<1+8; ++z)
            remove(new CellId(calcId(1,0,z%8)));
        for (int z=7; z<3+8; ++z)
            remove(new CellId(calcId(2,0,z%8)));
        for (int z=0; z<8; ++z)
            remove(new CellId(calcId(3,0,z%8)));
			
        for (int z=3; z<7; ++z)
            remove(new CellId(calcId(0,1,z%8)));
        for (int z=7; z<1+8; ++z)
            remove(new CellId(calcId(1,1,z%8)));
        for (int z=3; z<5; ++z)
            remove(new CellId(calcId(2,1,z%8)));
        for (int z=7; z<8+3; ++z)
            remove(new CellId(calcId(3,1,z%8)));

		for (int z=0; z<8; ++z)
            remove(new CellId(calcId(0,2,z%8)));
        for (int z=1; z<7; ++z)
            remove(new CellId(calcId(1,2,z%8)));
        for (int z=0; z<8; ++z)
            remove(new CellId(calcId(2,2,z%8)));
        for (int z=1; z<7; ++z)
            remove(new CellId(calcId(3,2,z%8)));

		
//		ICellPart cell = new CellPartTang(new CellId(calcId(x,y,z)));
//		((CellPartTang)cell).direction = z;
//		add(new CellId(calcId(x,y,z)), cell);
//
//        addAndValidate(cell, EdgeTang.UP, calcId(col,row-1,3));

		

    }

    public void buildSquare(int sizeY, int sizeX) { // size=2 in triangle_puz.jpg
        grid.clear();
        currCell = null;
        leftCells = -1;

        this.sizeY = sizeY;
        this.sizeX = sizeX;

        for (int y=0; y<sizeY; ++y)
            for (int x=0; x<sizeX; ++x)
                for (int z=0; z<8; ++z) {
                    ICellPart cell = new CellPartTang(new CellId(calcId(x,y,z)));
                    cell.setSpecial(z);
                    add(new CellId(calcId(x,y,z)), cell);
                }

        currCell = cells.get(0);

        int z;

        z=0;
        for (int row=0; row<sizeY; ++row)
            for (int col=0; col<sizeX; ++col) {
                ICellPart cell = grid.get(new CellId(calcId(col,row,z)));
                addAndValidate(cell, EdgeTang.UP, calcId(col,row-1,3));
                addAndValidate(cell, EdgeTang.RIGHT_DOWN, calcId(col, row,1));
                addAndValidate(cell, EdgeTang.LEFT, calcId(col, row,7));
            }

        z=1;
        for (int row=0; row<sizeY; ++row)
            for (int col=0; col<sizeX; ++col) {
                ICellPart cell = grid.get(new CellId(calcId(col,row,z)));
                addAndValidate(cell, EdgeTang.LEFT_UP, calcId(col,row,0));
                addAndValidate(cell, EdgeTang.DOWN, calcId(col, row,2));
                addAndValidate(cell, EdgeTang.RIGHT, calcId(col+1, row,6));
            }

        z=2;
        for (int row=0; row<sizeY; ++row)
            for (int col=0; col<sizeX; ++col) {
                ICellPart cell = grid.get(new CellId(calcId(col,row,z)));
                addAndValidate(cell, EdgeTang.RIGHT, calcId(col+1,row,5));
                addAndValidate(cell, EdgeTang.UP, calcId(col, row,1));
                addAndValidate(cell, EdgeTang.DOWN_LEFT, calcId(col, row,3));
            }

        z=3;
        for (int row=0; row<sizeY; ++row)
            for (int col=0; col<sizeX; ++col) {
                ICellPart cell = grid.get(new CellId(calcId(col,row,z)));
                addAndValidate(cell, EdgeTang.LEFT, calcId(col,row,4));
                addAndValidate(cell, EdgeTang.UP_RIGHT, calcId(col, row,2));
                addAndValidate(cell, EdgeTang.DOWN, calcId(col, row+1,0));
            }

        z=4;
        for (int row=0; row<sizeY; ++row)
            for (int col=0; col<sizeX; ++col) {
                ICellPart cell = grid.get(new CellId(calcId(col,row,z)));
                addAndValidate(cell, EdgeTang.DOWN, calcId(col,row+1,7));
                addAndValidate(cell, EdgeTang.LEFT_UP, calcId(col, row,5));
                addAndValidate(cell, EdgeTang.RIGHT, calcId(col, row,3));
            }

        z=5;
        for (int row=0; row<sizeY; ++row)
            for (int col=0; col<sizeX; ++col) {
                ICellPart cell = grid.get(new CellId(calcId(col,row,z)));
                addAndValidate(cell, EdgeTang.LEFT, calcId(col-1,row,2));
                addAndValidate(cell, EdgeTang.UP, calcId(col, row,6));
                addAndValidate(cell, EdgeTang.RIGHT_DOWN, calcId(col, row,4));
            }

        z=6;
        for (int row=0; row<sizeY; ++row)
            for (int col=0; col<sizeX; ++col) {
                ICellPart cell = grid.get(new CellId(calcId(col,row,z)));
                addAndValidate(cell, EdgeTang.DOWN, calcId(col,row,5));
                addAndValidate(cell, EdgeTang.LEFT, calcId(col-1, row,1));
                addAndValidate(cell, EdgeTang.UP_RIGHT, calcId(col, row,7));
            }

        z=7;
        for (int row=0; row<sizeY; ++row)
            for (int col=0; col<sizeX; ++col) {
                ICellPart cell = grid.get(new CellId(calcId(col,row,z)));
                addAndValidate(cell, EdgeTang.RIGHT, calcId(col,row,0));
                addAndValidate(cell, EdgeTang.UP, calcId(col, row-1,4));
                addAndValidate(cell, EdgeTang.DOWN_LEFT, calcId(col, row,6));
			}

	}

    @Override
    public StringBuffer showBuf(int newLoc[]) { // TODO the 2nd show for the snowFlake 

        StringBuffer sb = new StringBuffer(cells.size()*5); // *2 by presentation, *3 by showId

        sb.append("\n");
        sb.append(super.presentation(newLoc));
        sb.append("\n");
        for (int row=0; row<sizeY; ++ row) {
            sb.append("\n  ");
            for (int col=0;  col<sizeX; ++col) {
                ICellPart cell;// = grid.get(new CellId(calcId(col,row,z)));
                int newLocation = newLoc[ cellIdToIndex.get(calcId(col,row,7))];
                cell = cells.get(newLocation); //grid.get(new CellId(newLocation));
                String s = showId(cell);
                sb.append("\t" + s);
                // System.out.print(newLocation);// + " " + cell2.getId().getId() + " ");
                newLocation = newLoc[ cellIdToIndex.get(calcId(col,row,0))];
                cell = cells.get(newLocation); //grid.get(new CellId(newLocation));
                String s2 = showId(cell);
                sb.append(s2 + "\t");
                // System.out.print(newLocation);// + " " + cell2.getId().getId() + " ");
            }
            sb.append("\n");
            for (int col=0;  col<sizeX; ++col) {
                ICellPart cell;// = grid.get(new CellId(calcId(col,row,z)));
                int newLocation = newLoc[ cellIdToIndex.get(calcId(col,row,6))];
                cell = cells.get(newLocation); //grid.get(new CellId(newLocation));
                String s = showId(cell);
                sb.append(s+"\t\t");
                // System.out.print(newLocation);// + " " + cell2.getId().getId() + " ");
                newLocation = newLoc[ cellIdToIndex.get(calcId(col,row,1))];
                cell = cells.get(newLocation); //grid.get(new CellId(newLocation));
                String s2 = showId(cell);
                sb.append(s2);
                // System.out.print(newLocation);// + " " + cell2.getId().getId() + " ");
            }
            sb.append("\n");
            for (int col=0;  col<sizeX; ++col) {
                ICellPart cell;// = grid.get(new CellId(calcId(col,row,z)));
                int newLocation = newLoc[ cellIdToIndex.get(calcId(col,row,5))];
                cell = cells.get(newLocation); //grid.get(new CellId(newLocation));
                String s = showId(cell);
                sb.append(s+"\t\t");
                // System.out.print(newLocation);// + " " + cell2.getId().getId() + " ");
                newLocation = newLoc[ cellIdToIndex.get(calcId(col,row,2))];
                cell = cells.get(newLocation); //grid.get(new CellId(newLocation));
                String s2 = showId(cell);
                sb.append(s2);
                // System.out.print(newLocation);// + " " + cell2.getId().getId() + " ");
            }
            sb.append("\n");
            for (int col=0;  col<sizeX; ++col) {
                ICellPart cell;// = grid.get(new CellId(calcId(col,row,z)));
                int newLocation = newLoc[ cellIdToIndex.get(calcId(col,row,4))];
                cell = cells.get(newLocation); //grid.get(new CellId(newLocation));
                String s = showId(cell);
                sb.append("\t"+s);
                // System.out.print(newLocation);// + " " + cell2.getId().getId() + " ");
                newLocation = newLoc[ cellIdToIndex.get(calcId(col,row,3))];
                cell = cells.get(newLocation); //grid.get(new CellId(newLocation));
                String s2 = showId(cell);
                sb.append(s2+"\t");
                // System.out.print(newLocation);// + " " + cell2.getId().getId() + " ");
            }
        }

        return sb;
    }

    @Override
    public void show(int newLoc[]) {
        System.out.print(showBuf(newLoc));
    }

//    @Override
//    public boolean verifyMiddle() {
//        for (int j=currCellIndex; j>=0; j--) {
//            ICellPart cell = cells.get(j);
//            if (((CellPartTang)cell).direction != (cell.getId().getId()-1) % 10)
//                return false;
//        }
//        return true;
//    }

    @Override
    public boolean canPut(IPart part) {
        boolean canPut = super.canPut(part);

        if (canPut) {
            for (ICellPart cell: part.getCells())
                if (cell.getGridCell().getSpecial()!= cell.getSpecial())
                    canPut = false; // TODO bug?
        }

        return canPut;
    }

}
