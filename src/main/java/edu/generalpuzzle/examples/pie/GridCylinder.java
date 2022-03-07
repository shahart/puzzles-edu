package edu.generalpuzzle.examples.pie;

import edu.generalpuzzle.infra.CellId;
import edu.generalpuzzle.infra.ICellPart;
import edu.generalpuzzle.infra.IGrid;
import edu.generalpuzzle.infra.Parts;

import java.util.Set;
import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * Date: 13/06/2008
 */
public class GridCylinder extends IGrid {

    protected int size;

    protected int calcId(int z, int y) {
        return 100*(z+1)+(y+1);
    }

    public void build(int size) {
        grid.clear();
        currCell = null;
        leftCells = -1;
        this.size = size;

        for (int z=0; z<4; ++z)
            for (int y=0; y<size; ++y) {
                ICellPart cell = new CellPartPie(new CellId(calcId(y,z)));
                add(new CellId(calcId(y,z)), cell);
            }

        currCell = cells.get(0);

        for (int z=0; z<4; ++z)
            for (int row=0; row<size; ++row) {
                    ICellPart cell = grid.get(new CellId(calcId(row,z)));
                    switch (z) {
                        case 0:addAndValidate(cell, PieEdge.CW_0, calcId(row,z+1)); break;
                        case 1:addAndValidate(cell, PieEdge.CW_1, calcId(row,z+1)); break;
                        case 2:addAndValidate(cell, PieEdge.CW_2, calcId(row,z+1)); break;
                        case 3:addAndValidate(cell, PieEdge.CW_3, calcId(row,0)); break;
                    }
                    addAndValidate(cell, PieEdge.BACK, calcId(row-1,z));
                }

    }

    @Override
    public StringBuffer showBuf(int newLoc[]) { // TODO

        StringBuffer sb = new StringBuffer(cells.size()*5); // *2 by presentation, *3 by showId

        sb.append("\n");
        sb.append(super.presentation(newLoc));
        sb.append("\n");
        for (int z=0; z<4; ++ z) {
            sb.append("\n");
            for (int row=0; row<size; ++ row) {
                ICellPart cell;// = grid.get(new CellId(calcId(col,row,z)));
                int newLocation = newLoc[ cellIdToIndex.get(calcId(row,z))];
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

    private Set<CellPartPie> occupiedSet = new HashSet<CellPartPie>();    

    @Override
    public boolean verifyMiddleDLX() {
        occupiedSet.clear();
        boolean result = verifyMiddlePrivateDLX();
        if (occupiedSet.size() > 0) {
            for (CellPartPie cell : occupiedSet)
                cell.occupied = null;
        }
        return result;

    }

    private boolean verifyMiddlePrivateDLX() {
        for (int j=cellsSizeMinus1; j>=0; j--) {
            ICellPart cell = cells.get(j);
            if (! cell.isEmptyPartId())
                if (cell.keysExists)
                    for (int i=0; i<cell.keys.length; i++)
                        if (cell.keys[i] != 0)
                            if (cell.cell[i] != null && ! cell.cell[i].isPlaceHolder() && ! cell.cell[i].isEmptyPartId() && cell.cell[i].getcPartId() != '_') {
                                if (cell.keys[i] > 0) {
                                    if ( ! match(cell, i, cell.cell[i].keys[cell.dummyEdge.symmetricEdge(i)], cell.keys[i]))
                                        return false;
                                }
                                else
                                    if ( ! match(cell.cell[i], cell.dummyEdge.symmetricEdge(i), cell.keys[i], cell.cell[i].keys[cell.dummyEdge.symmetricEdge(i)]))
                                        return false;
                            }
                            else {
//                                 IRRELEVANT in DLX since the previous part might not being there for now

                                if (cell.keys[i] > 0) {
//                                // if (/* MUST HAVE A LOCK - I.E. NO TRUE IN THE MATCHES[0] ARRAY AND                                       // i goes back ON THE GRID
                                    if ((i == PieEdge.FRONT && j+4*cell.keys[i] >= cells.size()) ||
                                            (i == PieEdge.BACK && j-4*cell.keys[i] < 0))
                                        return false;
                                }

                            }
        }
        return true;
    }


    @Override
    public boolean verifyMiddle() {
        occupiedSet.clear();
        boolean result = verifyMiddlePrivate();
        if (occupiedSet.size() > 0) {
            for (CellPartPie cell : occupiedSet)
                cell.occupied = null;
        }
        return result;
    }

    private boolean verifyMiddlePrivate() {
//        boolean res = super.verifyMiddle(); // TODO keys with length>1
//        return res;
        int start; // = cells.size() - 1;
//        if (currCellIndex == 0)
//            start = cells.size() - 1;// unreachable code
//        else

            if (currCellIndex < cellsSizeMinus1)
                start = currCellIndex-1;
            else
                return true;//verify();
//                start = cells.size()-1;
        // int start = (currCellIndex > 0) ? currCellIndex-1 : cells.size()-1; // ,size?
//        if (considerKeys)
            for (int j=start; j>=0; j--) {
                ICellPart cell = cells.get(j);
//                if (! cell.isEmptyPartId())
                    if (cell.keysExists)
                        for (int i=PieEdge.FRONT; i<cell.keys.length; i++)
                            if (cell.keys[i] != 0)
                                if (cell.cell[i] != null && ! cell.cell[i].isPlaceHolder() && ! cell.cell[i].isEmptyPartId() && cell.cell[i].getcPartId() != '_') {
                                    if (cell.keys[i] > 0) {
                                        if ( ! match(cell, i, cell.cell[i].keys[cell.dummyEdge.symmetricEdge(i)], cell.keys[i]))
                                            return false;
                                    }
                                    else
                                        if ( ! match(cell.cell[i], cell.dummyEdge.symmetricEdge(i), cell.keys[i], cell.cell[i].keys[cell.dummyEdge.symmetricEdge(i)]))
                                            return false;
                                }
                                else {
                                    if (cell.keys[i] > 0) {
                                    // if (/* MUST HAVE A LOCK - I.E. NO TRUE IN THE MATCHES[0] ARRAY AND                                       // i goes back ON THE GRID
                                        if ((i == PieEdge.FRONT && j+4*cell.keys[i] >= cells.size()) ||
                                            (i == PieEdge.BACK )) // && j-4*cell.keys[i] < 0))
//                                            if (cell.keys[i] == 1 && cells.get(j-4) == null || cell.keys[i] == 2 && (cells.get(j-4) == null || cells.get(j-8) == null))
//                                                return false;

                                            return false;
                                    }

                                }
            }
        return true;
    }

    @Override
    public boolean verify() {
        occupiedSet.clear();
        for (ICellPart cell: cells)
            ((CellPartPie)cell).matchLock = false;
        boolean result = verifyPrivate();
        if (occupiedSet.size() > 0) {
            for (CellPartPie cell: occupiedSet)
                cell.occupied = null;
        }
        return result;
    }

    private boolean verifyPrivate() {
//        return super.verify(); // TODO the same
        int start = cells.size()-1; // (leftCells > 0) ? currCellIndex : cells.size()-1; // ,size?
//        if (considerKeys)
            for (int j=start; j>=0; j--) {
                ICellPart cell = cells.get(j);
                if (cell.isEmptyPartId())
                    System.out.println("unreachable code");
                else
                    if (cell.keysExists)
                        for (int i=0/*PieEdge.FRONT*/; i<cell.keys.length; i++) {
                            if (cell.keys[i] != 0)
                                if (cell.cell[i] == null || cell.cell[i].isPlaceHolder() || cell.cell[i].isEmptyPartId() || cell.cell[i].getcPartId() == '_') {
                                    if (cell.keys[i] > 0) {
                                    // if (/* MUST HAVE A LOCK - I.E. NO TRUE IN THE MATCHES[0] ARRAY AND                                       // i goes back ON THE GRID
                                        if (i == PieEdge.BACK || i == PieEdge.FRONT)
                                            return false;
                                    }

                                    ;
                                    if (cell.keys[i] > 0 && ! Parts.matches[cell.keys[i]][ 0]) // the key needs a lock
                                        return false;
                                }
                                else
                                    if (cell.keys[i] > 0) {
                                        if ( ! match(cell, i, cell.cell[i].keys[cell.dummyEdge.symmetricEdge(i)], cell.keys[i]))
                                            return false;
                                    }
                                    else
                                        if ( ! match(cell.cell[i], cell.dummyEdge.symmetricEdge(i), cell.keys[i], cell.cell[i].keys[cell.dummyEdge.symmetricEdge(i)]))
                                            return false;
                        }
            }

        // check orphan lock
        for (ICellPart cell: cells)
            if (cell.keysExists)
                for (int i=0; i<cell.keys.length; i++)
                    if (cell.keys[i] < 0 && ((CellPartPie)cell).occupied == null )
                          //   || cell.keys[i] > 0 && !  ((CellPartPie)cell).matchLock )
                        return false;

        for (ICellPart cell: cells)
            if (cell.keysExists)
                for (int i=0; i<cell.keys.length; i++)
//                    if (cell.keys[i] < 0 && ((CellPartPie)cell).occupied == null )
                  if (cell.keys[i] > 0 && !  ((CellPartPie)cell).matchLock ) {
                      System.out.println("walla");
                      return false;
                  }

        return true;
    }


    protected boolean match(ICellPart cell, int i, int key1, int key2) {
            if (key1 == 0 && key2 == 0)
                return true;

            int lock = 0, key = 0;

            if (key1 < 0)
                lock = -key1;
            else
                key = key1;

            if (key2 < 0)
                lock = - key2;
            else
                key = key2;

        CellPartPie nextcell = (CellPartPie)cell.cell[i]; // the first next is okay

        if (key == 0) {
            if (lock == 0)
                return true;
            else
//            return true;
//            if (nextcell != null)
            return Parts.matches[key][lock];
        }


        else
        if (key == 1) {
            if (! Parts.matches[key][lock] || (nextcell.occupied != cell && nextcell.occupied != null))
                return false;
        }
        else
        if (! Parts.matches[key-1][lock])
            return false;

        else {
//                if (cell.keys[i] == key) {

                    if (nextcell.keys[i] != -1 || (nextcell.occupied != cell && nextcell.occupied != null)) // no key in the next
                        return false;

                    CellPartPie nextcell2 = (CellPartPie) nextcell.cell[i];

                    if (nextcell2 == null || nextcell2.isPlaceHolder() || nextcell2.isEmptyPartId() || nextcell2.getcPartId() == '_')
                        return false;
                    else {
                        if (! (nextcell2.keys[cell.dummyEdge.symmetricEdge(i)] == -1) || (nextcell2.occupied != nextcell && nextcell2.occupied != null))
                            return false;

                        nextcell2.occupied = nextcell;

                        occupiedSet.add(nextcell2);

                    }

//                }
//                else {
//
//                }
            }

//        if (Parts.locksUsedSize == 0) { // no locks, so these are positive numbers
//            return Parts.matches[key1][key2]; // middle version
//        }
//        else {
//            else
//                return false;
////            if (Parts.matches == null || Parts.matches[key] == null)
////                return key == lock; // trivial version: A to a, B to b
////            else
//            return Parts.matches[key][lock]; // full version- bi-partite graph
//        }

        nextcell.occupied = (CellPartPie)cell;

        ((CellPartPie)cell).matchLock = true;

        occupiedSet.add(nextcell);

        return true;
        }


}
