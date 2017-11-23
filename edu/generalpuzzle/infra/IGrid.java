package edu.generalpuzzle.infra;

import java.util.*;

import edu.generalpuzzle.infra.engines.EngineStrategy;

/**
 * Created by IntelliJ IDEA.
 * Date: 29/05/2008
 */
public class IGrid {

    protected final Map<CellId, ICellPart> grid; // the state

    public Memento createMemento() {
        return new Memento(this);
    }

    public void setMemento(Memento memento) {
        memento.restore(this);
    }

    public static class Memento {

        public int[] state;

        public Memento() {
        }

        private Memento(IGrid originator) {
            // this.state = originator.state;
            int []result =new int[originator.grid.size()*2];
            Collection<ICellPart> gridCells = (originator.grid.values());
            int j=0;
            for (ICellPart part: gridCells) {
                    result[j++] = part.getId().getId();
                    result[j++] = part.getPartId();
            }
            state = result;
        }

        private void restore(IGrid originator) {
            // originator.state = this.state;
            for (int j=0; j<state.length;j+=2) {
                int flatId = state[j];
                originator.grid.get(new CellId(flatId)).setPartId(state[j+1]);
            }
        }

    }

//    public void setState(int[] gridState) {
//        for (int j=0; j<gridState.length;j+=2) {
//            int flatId = gridState[j];
//            grid.get(new CellId(flatId)).setPartId(gridState[j+1]);
//        }
//    }
//
//    public int[] getState() {
//        int []result =new int[grid.size()*2];
//        Collection<ICellPart> gridCells = (grid.values());
//        int j=0;
//        for (ICellPart part: gridCells) {
//                result[j++] = part.getId().getId();
//                result[j++] = part.getPartId();
//        }
//        return result;
//    }

    public static final char GRID_ID = '#';

    protected final Map<Integer, Integer> cellIdToIndex;

    protected List<ICellPart> cells;
    protected ICellPart cellsArr[];
    protected int cellsSizeMinus1;
    protected int leftCells;

    public boolean considerKeys = false;

    protected ICellPart currCell = null; // this is the row, column from the puzzle2D project
    protected int currCellIndex = 0;

    private ICellPart cellTrips[];

//    public static boolean keysExists = false;

    private int placeHolders = 0;

    public List<ICellPart> getCells() {
        return (cells);
    }

    public int getPlaceHolders() {
        return placeHolders;
    }

    public ICellPart get(int cellId) {
        return grid.get(new CellId(cellId));
    }

    public int getCellIndex(int id) {
        Integer idToIndex = cellIdToIndex.get(id);
        if (idToIndex != null)
            return idToIndex;
        else
            return -1;
    }


    public IGrid() {
        grid = new TreeMap<CellId, ICellPart>(); // javolution's FastMap- not better
        cellIdToIndex = new TreeMap<Integer, Integer>();
        cells = new ArrayList<ICellPart>();
    }

    public int getLeftCells() {
        if (leftCells == -1) {
            leftCells = 0;
            for (ICellPart cell: grid.values())
                if (! cell.isPlaceHolder()) // TODO 2d_poly
                    ++ leftCells;
            cellsSizeMinus1 = cells.size()-1;

            cellsArr = new ICellPart[cells.size()];
            cells.toArray(cellsArr);
        }

        return leftCells;
    }

    protected String showId(ICellPart cell) {
        String str;
//        if (cell == currCell)
//            str = ".";
//        else
            str = " ";

        if (cell == null)
            return(str+"_");        
        else
            return(str + (cell.getcPartId()));
    }

    /** default, justs shows grid cells
     *  override to show solution in text
     * @param newLoc
     */
    public void show(int newLoc[]) {
        System.out.println(presentation(newLoc));
    }

    public StringBuffer showBuf(int newLoc[]) {
        return new StringBuffer(0);
    }

    public String toString2() {
        return showBuf(new int[0]).toString();
    }

    public StringBuffer presentation(int newLoc[]) {
        StringBuffer sol = new StringBuffer(cells.size()*2+1);
        for (ICellPart cell : cells) {
            int newLocation ;
            if (newLoc.length != 0)
                newLocation = newLoc[ cellIdToIndex.get(cell.getId().getId()) ];
            else
                newLocation = cellIdToIndex.get(cell.getId().getId());
            ICellPart cell2 = cells.get(newLocation);
            sol.append(showId(cell2));
//            sol.append(newLocation); // + " " + cell2.getId().getId() + " ");
        }
        return sol;
    }

    public String toString() {
        StringBuffer sol = new StringBuffer(cells.size()*2+1);
        for (ICellPart cell : cells) {
            int newLocation = cellIdToIndex.get(cell.getId().getId()) ;
            ICellPart cell2 = cells.get(newLocation);
            sol.append(showId(cell2));
//            sol.append(newLocation); // + " " + cell2.getId().getId() + " ");
        }
        return sol.toString();
    }

    /** setPlaceHolder for that cell */
    public void remove(CellId id) {
        ICellPart toDel = grid.get(id);

        if (toDel == null || toDel.isPlaceHolder()) {
            System.out.println(" error: IGrid::remove - delete id " + id.getId() + (toDel == null ? " - not found" : " - already deleted"));
            return;
        }
//        else
//            System.out.println(" info:  IGrid::remove - delete id " + id.getId());

        toDel.setPlaceHolder();

        if (currCell == toDel) {

            // almost like goFroward
            currCell = cells.get(currCellIndex);
//        final int cellsSizeMinus1 = cells.size() -1;
            while (currCellIndex< cells.size() - 1 && (! currCell.isEmptyPartId()) ) {
                ++currCellIndex;
                currCell = cells.get(currCellIndex);
            }


        }

        ++ placeHolders;

    }

    /**
     * Returns true if goForward is no longer needed
     //* @param
     //* @see
     //* @return
     */
    public void goForward() {

        currCell = cellsArr[currCellIndex];
//        currCell = cells.get(currCellIndex);
//        final int cellsSizeMinus1 = cells.size() -1;
        while (
                currCellIndex< cellsSizeMinus1 &&
                (! currCell.isEmptyPartId()) ) { // TODO 2d_poly - comment, by true
            ++currCellIndex;
            currCell = cellsArr[currCellIndex];
//            currCell = cells.get(currCellIndex);
        }
    }

    protected boolean match(int key1, int key2) {
        if (key1 == 0 && key2 == 0)
            return true;

        int lock = 0, key = 0;

        if (key1 <= 0)
            lock = -key1;
        else
            key = key1;

        if (key2 <= 0)
            lock = - key2;
        else
            key = key2;

//        if (key == 0)
//            return Parts.matches[key][lock];

        return Parts.matches[key][lock];

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
    }

    public /*static*/  boolean doMark(ICellPart partCell, ICellPart gridCell) {

        if (considerKeys) {
//
//            if (! keysMatchFromGrid(gridCell, partCell.keys))
//                return false;
//
            if (partCell.keysExists) {
                for (int i=0; i<partCell.keys.length; i++)
//                    if (partCell.keys[i] != 0 && gridCell.cell[i] != null) {// &&  ! gridCell.cell[i].isPlaceHolder() && ! gridCell.cell[i].isEmptyPartId() ) {
////                        int symmetricEdge = partCell.dummyEdge.symmetricEdge(i);
//                        // if there is neighour cell, check its symmetric key
//                        if (
//                                //gridCell.cell[i] != null &&
//                         ! gridCell.cell[i].isPlaceHolder() && ! gridCell.cell[i].isEmptyPartId() &&
////                        if (
//                                ! match(gridCell.cell[i].keys[partCell.dummyEdge.symmetricEdge(i)], partCell.keys[i]))
//                            return false;
                        gridCell.keys[i] = partCell.keys[i];
                        gridCell.keysExists = true;
//                    }
            }
        }

        if (! partCell.isPlaceHolder()) {
            gridCell.setPartId(partCell.getPartId());
            gridCell.setCellInPart(partCell.getId().getId()-1 );
        }

        partCell.setGridCellId(gridCell.getId().getId());
        partCell.setGridCell(gridCell);

        return true;
    }

//    public boolean isStranded() {
//        ICellPart currCell = cells.get(currCellIndex);
//        for (int i=0; i<currCell.getCell().length; i++)
//            if (currCell.getCell()[i]!=null) {
//                if (currCell.getCell()[i].isEmptyPartId())
//                    return false;
//            }
//        return true;
//    }

    private int size = 0; // helper for islandRecursive
    private int smallestPart = 0;

    public void setSmallestPart(int smallestPart) {
        this.smallestPart = smallestPart;
    }

    public int islandSize(ICellPart gridCell) {
        if (! EngineStrategy.ST_HEURISTIC)
            return Integer.MAX_VALUE;
        
        if (gridCell.isEmptyPartId()) {
            size = 1; // in this point we know the island is at least at size 1
            islandRecursive(gridCell);
        }
        else
            size = Integer.MAX_VALUE; // why there it is zero TODO var8
        return size;
    }

    public void islandRecursive(ICellPart gridCell) {
        gridCell.setBreadcrumb(1);

        for (int edge=0;edge<gridCell.getCell().length;edge++) {
            ICellPart neighbour = gridCell.getCell(edge);

            if (neighbour != null && neighbour.isEmptyPartId() && ! neighbour.isBreadcrumb()) {
                size ++;
                if (size >= smallestPart)
                    break;
                islandRecursive( neighbour);
            }
        }

        gridCell.clearBreadcrumb();
    }

    private int put(IPart part, ICellPart gridCell) {
        int matchesCells = 0;
        IPart.Trip trip = part.getTrip(part.getRotationIndex());

        ICellPart partCell = part.getCells()[trip.fromCell[matchesCells]];

        if (/*partCell.getGridCell() == null && */gridCell == null ||
                (
//                        partCell.isClearGridCellId() &&
                        (gridCell.getPartId() != part.getId() && (! gridCell.isEmptyPartId())  && ! gridCell.isPlaceHolder())  ||
                   false)) //     !(! considerKeys || ! partCell.keysExists || keysMatch(partCell)))) // || gridCell.isPlaceHolder())) {

            return matchesCells;

        if (!doMark(partCell, gridCell))
            return matchesCells;

        cellTrips[ trip.fromCell[matchesCells]] = gridCell;

        while (/*partCell.getGridCell() == null && */ //gridCell != null &&
                (gridCell.getPartId() == part.getId()  || gridCell.isEmptyPartId() ) ) {
//                        && (! considerKeys || ! partCell.keysExists || keysMatch(partCell))) { // || gridCell.isPlaceHolder()) {

            int edge = trip.edge[matchesCells];

            ++ matchesCells;
            if (matchesCells == part.getCellsAmount())
                return matchesCells;

            gridCell = gridCell.getCell()[edge];
            cellTrips[ partCell.getCell()[edge].getId().getId()-1 ] = gridCell;

            partCell = partCell.getCell()[edge];

            if (/*partCell.getGridCell() == null && */ // can't be null - since we use removeImpossible gridCell == null ||
                    (gridCell.getPartId() != part.getId() && ! gridCell.isEmptyPartId()) ||
              false) //              (! (! considerKeys || ! partCell.keysExists || keysMatch(partCell)))) // || gridCell.isPlaceHolder())) {
                return matchesCells;

            if (gridCell.special != partCell.special)
                return matchesCells;

            if (!doMark(partCell, gridCell))
                return matchesCells;

            gridCell = cellTrips[trip.fromCell[matchesCells]];
            partCell = part.getCells()[trip.fromCell[matchesCells]];
        }

        return matchesCells;
    }

    /** @deprecated  used verfiy instead */
    private boolean keysMatch(ICellPart partCell) {
//        if (! considerKeys)
//            return true;
//
//        if (! partCell.keysExists)
//            return true;
        
        for (int i=0; i<partCell.keys.length; i++) {
//            int symmetricEdge = partCell.dummyEdge.symmetricEdge(i);
            ICellPart gridCell = partCell.getGridCell();
            if (gridCell != null && gridCell.cell[i] != null && ! gridCell.cell[i].isPlaceHolder() && ! gridCell.cell[i].isEmptyPartId() &&
                    ! match(gridCell.cell[i].keys[partCell.dummyEdge.symmetricEdge(i)], partCell.keys[i]))
                return false;
        }

        return true;
    }

    /** @deprecated  used verfiy instead */
    private boolean keysMatchFromGrid(ICellPart gridCell, int partKeys[]) {
        for (int i=0; i<gridCell.keys.length; i++)
            if (gridCell.getCell(i) != null && gridCell.getCell(i).keysExists) {
                int symmetricEdge = gridCell.dummyEdge.symmetricEdge(i);
                if (! match(gridCell.getCell()[i].keys[symmetricEdge], partKeys[i]))
                    return false;
            }

        return true;
    }

    public /*static*/ int dfsPut(ICellPart partCell, ICellPart gridCell) {
       int matchesCells = 0;

        if (/*partCell != null && */ // partCell.getGridCell() == null &&
             (partCell.isClearGridCellId() &&
                /*gridCell != null && */gridCell.isEmptyPartId() || gridCell.partId == /* TODO 2d_poly 2147483646l */GRID_ID)  && //  || gridCell.isPlaceHolder()) {
            true) {//(! considerKeys || ! partCell.keysExists ||  keysMatch(partCell))) {

            if (gridCell.special != partCell.special)
                return matchesCells;

            if (!doMark(partCell, gridCell))
                return matchesCells;

             ++ matchesCells; // count all, also the partWithPlaceHolderExist

            for (int edge=0;edge<gridCell.getCell().length;edge++)
                if (partCell.getCell(edge)!=null &&gridCell.getCell(edge)!= null)
                    matchesCells +=
                        dfsPut(partCell.getCell(edge), gridCell.getCell(edge));
        }

        return matchesCells;
    }

//    public boolean anyPut(IPart part) {
//        currCell = cells.get(currCellIndex);
//        for (int i=0; i<part.getCellsAmount(); i++) {
//            int cellId = i; // part.getReferenceCells(i);
//            ICellPart cell = part.getCells() [cellId];
//
//            int matchesCells = dfsPut(cell, currCell);
////                int matchesCells = put(part, currCell);
//
//            if (matchesCells == part.getCellsAmount()) { //.length) { //getCellsAmount() + part.getPlaceHolders()) {  // matchSuccess(part)) {
//                goForward();
//                leftCells -= part.getCellsAmount();
//
//                return true;
//            }
//            else
//                remove(part);
//        }
//        return false;
//    }

    public boolean new2CanPut(IPart part, int pos[]) {

        int matchesCells = 0;

//        for (Integer i: pos) {

        for (ICellPart partCell: part.getCells()) {
            int i = pos[matchesCells];

//            ICellPart gridCell = cells.get(i); //
            ICellPart gridCell = cellsArr[i];//cells.get(i) ;

            if (/*partCell.getGridCell() == null && */ // can't be null - since we use removeImpossible gridCell == null ||
                    (gridCell.getPartId() != part.getId() && ! gridCell.isEmptyPartId()) ||
              false) //              (! (! considerKeys || ! partCell.keysExists || keysMatch(partCell)))) // || gridCell.isPlaceHolder())) {
                break;

            if (gridCell.special != partCell.special)
                break;
            if (!doMark(partCell, gridCell))
                break;

            ++ matchesCells;
        }

        if (matchesCells == part.getCellsAmount()) { // part.getCellsAmount()) {  // matchSuccess(part)) { // Todo and not nextAlone()
            goForward();
            leftCells -= part.getCellsAmount();
            return true;
        }
        else
            remove2(part, matchesCells);

        return false;

    }

    public boolean newCanPut(IPart part) {

        currCell = cells.get(currCellIndex);

        if (cellTrips == null || cellTrips.length < part.getCellsAmount()) // .length) // Amount())
            cellTrips = new ICellPart[part.getCellsAmount()]; // .length];

        int matchesCells = put(part, currCell);

        if (matchesCells == part.getCellsAmount()) { // part.getCellsAmount()) {  // matchSuccess(part)) { // Todo and not nextAlone()
            goForward();
            leftCells -= part.getCellsAmount();
            return true;
        }
        else
            remove(part);

        return false;
    }

    public boolean canPut(IPart part) {

        currCell = cells.get(currCellIndex);

        //if (currCell.isEmptyPartId())
        {
//            for (int i=0; i<part.getReferenceCellsAmount(); i++) {
//                int cellId = part.getReferenceCells(i);
//            for (int i=0; i<part.getCellsAmount(); i++) {
//                int cellId = i; //part.getReferenceCells(i);
            {
                int cellId = part.getAnchorIndex();
                ICellPart cell = part.getCells() [cellId];

                int matchesCells = dfsPut(cell, currCell);
//                int matchesCells = put(part, currCell);

                if (matchesCells == part.getCellsAmount()) { //.length) { //getCellsAmount() + part.getPlaceHolders()) {  // matchSuccess(part)) {
                    goForward();
                    leftCells -= part.getCellsAmount();

                    return true;
                }
                else
                    remove(part);
            }
        }

        return false;
    }

    public static void remove(IPart part) {
        for (ICellPart cell: part.getCells()) {
            if (cell.getGridCell() != null) {

                ICellPart gridCell = cell.getGridCell();
                if (cell.keysExists) {
                    gridCell.keysExists = false;
                    for (int i=0; i<gridCell.keys.length; i++)
                        gridCell.keys[i] = 0;
                }

                cell.getGridCell().clearPartId();
                cell.clearGridCellId();
                cell.setGridCell(null);
            }
        }
    }

    public static void remove2(IPart part, int how) {
        for (int j=0; j<how; j++) {
            ICellPart cell = part.getCells()[j];
            if (cell.getGridCell() != null) {

                ICellPart gridCell = cell.getGridCell();
                if (cell.keysExists) {
                    gridCell.keysExists = false;
                    for (int i=0; i<gridCell.keys.length; i++)
                        gridCell.keys[i] = 0;
                }

                cell.getGridCell().clearPartId();
                cell.clearGridCellId();
                cell.setGridCell(null);
            }
        }
    }

    public void removeLast(IPart part) {
        remove(part);
        leftCells += part.getCellsAmount();
    }

     public int getCurrCellIndex() {
         return currCellIndex;
    }

     public void setCurrCellIndex(int currCellIndex) {
         this.currCellIndex = currCellIndex;
    }

     protected void add(CellId cellId, ICellPart cell) {
         grid.put(cellId, cell);
         cells.add(cell);
//         cellsSizeMinus1 ++;
         cellIdToIndex.put(cellId.getId(), cells.size() - 1);
    }

    // newOrder for example: 30 10 20
    public void reorder(ICellPart newOrder[]) {
        cellIdToIndex.clear();

        // copy of grid cells - to be in the right order
        List<ICellPart> newCellsOrders = new ArrayList<ICellPart>(newOrder.length);

        for (int i=0;i<newOrder.length; i++) {

            // find minimum index
            int minIndex = -1;
            int minId = Integer.MAX_VALUE;
            for (int j=0; j<newOrder.length; j++) {
                int newOrderCurrentCellId = newOrder[j].getId().getId();
                if (newOrderCurrentCellId < minId) {
                    minId = newOrderCurrentCellId;
                    minIndex = j;
                }
            }

            // put the "new" minimun cell back into the grid
            ICellPart a = cells.get(minIndex);
            newCellsOrders.add(i, a);

            cellIdToIndex.put(a.getId().getId(), i);

            newOrder[minIndex].getId().setId(Integer.MAX_VALUE);
        }

        // override the original grid with its copy
        cells = newCellsOrders;
    }

     public void setPlaceHolder(int id) {
         ++ placeHolders;
         grid.get(new CellId(id)).setPlaceHolder();
    }

    protected void addAndValidate(ICellPart cell, int edge, int id) {
        ICellPart a = grid.get(new CellId(id));
        if (a != null) {
            cell.getCell()[edge] = a;
            a.getCell()[a.dummyEdge.symmetricEdge(edge)] = cell;
        }
    }

    public void show() {
        GraphIt.getInstance().showGrid(this);        
    }

    public boolean verifyMiddleDLX() {
        for (int j=cellsSizeMinus1; j>=0; j--) {
            ICellPart cell = cells.get(j);
            if (! cell.isEmptyPartId())
                if (cell.keysExists)
                    for (int i=0; i<cell.keys.length; i++)
                        if (cell.keys[i] != 0)
                            if (cell.cell[i] != null && ! cell.cell[i].isPlaceHolder() && ! cell.cell[i].isEmptyPartId() && cell.cell[i].getcPartId() != '_') {
                                    if ( ! match(cell.cell[i].keys[cell.dummyEdge.symmetricEdge(i)], cell.keys[i]))
                                        return false;
                            }
        }
        return true;
    }


    public boolean verifyMiddle() {
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
        if (considerKeys)
            for (int j=start; j>=0; j--) {
                ICellPart cell = cells.get(j);
                if (! cell.isEmptyPartId())
                    if (cell.keysExists)
                        for (int i=0; i<cell.keys.length; i++)
                            if (cell.keys[i] != 0)
                                if (cell.cell[i] != null && ! cell.cell[i].isPlaceHolder() && ! cell.cell[i].isEmptyPartId() && cell.cell[i].getcPartId() != '_') {
                                        if ( ! match(cell.cell[i].keys[cell.dummyEdge.symmetricEdge(i)], cell.keys[i]))
                                            return false;
                                }
            }
        return true;
    }
                                                               
    public boolean verify() {

        int start = cells.size()-1; // (leftCells > 0) ? currCellIndex : cells.size()-1; // ,size?
        if (considerKeys)
            for (int j=start; j>=0; j--) {
                ICellPart cell = cells.get(j);
                if (! cell.isEmptyPartId())
                    if (cell.keysExists)
                        for (int i=0; i<cell.keys.length; i++)
                            if (cell.keys[i] != 0)
                                if (cell.cell[i] == null || cell.cell[i].isPlaceHolder() || cell.cell[i].isEmptyPartId() || cell.cell[i].getcPartId() == '_') {
                                    ;
                                    if (cell.keys[i] > 0 && ! Parts.matches[cell.keys[i]][ 0]) // the key needs a lock
                                        return false;
                                }
                                else
                                    if ( ! match(cell.cell[i].keys[cell.dummyEdge.symmetricEdge(i)], cell.keys[i]))
                                        return false;
            }
        return true;
    }

    public int calcId(int x, int y, int z) { // TODO 10->100, 100>10000. change also in the EdgeTang::mark
        return 0;
    }


}
