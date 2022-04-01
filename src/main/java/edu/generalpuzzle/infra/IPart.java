package edu.generalpuzzle.infra;

import org.apache.log4j.Logger;

import java.util.List;

import edu.generalpuzzle.main.PuzzleException;
import edu.generalpuzzle.examples.tangram.CellPartTang;

/**
 * Created by IntelliJ IDEA.
 * Date: 29/05/2008
 */
public class IPart {

    public static final Logger LOG = Logger.getLogger(IPart.class.getName());

    public final class Trip {
        public final int[] fromCell;
        // public int toCell[];
        public final int[] edge;

        public Trip(int cellsAmount) {
            fromCell = new int[cellsAmount];
            edge = new int[cellsAmount];
        }
    }

    private Trip trips[];
    private int tripIndex;

    protected static ICellPart dummyCellPart;

    private char id;
    public char masterid;
    protected ICellPart cells[]; // any Collection - like Set - will result in performance penalty
    protected int anchorIndex;

    public ICellPart preparedRotations[][];
    protected int anchorIndices[];

    private static char lastId = 0;

    public int rotations = 1; // no rotations allowed.
    public char anotherOne = 0;
    protected int reflective = 1; // is not reflective, like in 3d
    public int totalRotations = reflective * rotations;

    protected int rotationIndex;

    private int cellsAmount;
//    private int placeHolders;


    private int implied[][];
    private int impliedSize;

    public Trip getTrip(int idx) {
        return trips[idx];
    }

    public int[][] getImplied() {
        return implied;
    }

    /** number of symmetries */
	public int getImpliedSize() {
        return impliedSize;
    }

    public int getAnchorIndex() {
        return anchorIndices[rotationIndex];
    }

    public int getRotationIndex() {
        return rotationIndex;
    }

    public String toString2() {
        return "Part " + id + rotationIndex;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Part " + id + " ");
        if (anotherOne != 0 && anotherOne != id) {
            sb.append("(=" + anotherOne + ") ");
            return sb.toString();
        }
        if (cells == null)
            sb.append("cells: none");
        else {
            sb.append(cells.length + " cells: ");
            for (ICellPart cell: cells)
                sb.append(cell.toString());
        }
        sb.append(" rotations: ");
        sb.append(totalRotations);
        boolean keysExists = false;
        if (cells != null)
            for (ICellPart cell: cells)
                if (cell.keysExists)
                    keysExists = true;
        if (keysExists)
                sb.append(" keysExists");
//        sb.append("\n");
        return sb.toString();
    }

    public IPart(char id) { // empty for Reflections
        this.id = id;
    }

    public IPart(ICellPart dummyCellPart) {
        this.id = lastId;
        ++ lastId;
        this.dummyCellPart = dummyCellPart;
    }

    public IPart(char id, ICellPart dummyCellPart) {
        this.id = Character.toUpperCase(id);
        this.dummyCellPart = dummyCellPart;
    }

    /** marks this part as identical to a previous one, the IDs has gap of offset */
	public void anotherOne(int offset) {
        anotherOne = id;
        id += offset;
    }

    public int getId() {
        return id;
    }

    public void setId(char id) {
        this.id = id;
    }

    /** reflective- 0/1 */
    public void setReflective(int reflective) {
        totalRotations /= this.reflective;
        this.reflective = reflective+1;
        totalRotations *= this.reflective;
    }

    public ICellPart[] getCells() {
        return cells;
    }

    public int getCellsAmount() {
        return cellsAmount;
    }

//    public int getPlaceHolders() {
//        return placeHolders;
//    }

	/** moves to the r rotation */
    public void rotate(int r) {
        if (r >= totalRotations)
            throw new PuzzleException("invalid rotate " + r + " on part " + toString());
        else {
            rotationIndex = r;
            cells = preparedRotations[r];
            anchorIndex = anchorIndices[r];
        }
    }

    public int getTotalRotations() {
        return totalRotations;
    }

    public void reflect() {
        for (ICellPart cell: cells)
            cell.rotate(0);
    }

    // no more rotations
	/** moves to the next available rotation */
    public boolean rotate() {

        ++ rotationIndex;

        if (rotationIndex  == totalRotations) {
            cells = preparedRotations[0];
            anchorIndex = anchorIndices[0];
            rotationIndex = -1;
            return true;
        }
        else {
            cells = preparedRotations[rotationIndex];
            anchorIndex = anchorIndices[rotationIndex];
        }

        return false;
    }

//    public int getRotations() {
//        return rotations;
//    }

    /** hard init of the rotations */
	public final void setRotations(int rotations) {
        if((rotations <= 0))
            throw new PuzzleException("invalid rotations");
        totalRotations /= this.rotations;
        this.rotations = rotations;
        totalRotations *= this.rotations;
    }

    /** computed init of the rotations based on the defined transformations */
	public void setRotations(ICellPart a) {
        int r = 1;
        for (int i=0; i<a.availRotations(); i++)
            r *= rotationCycle(a, i+1);
        setRotations(r); // *reflective);
    }

    /** see isPlaceHolder */
	public void setPlaceHolder(int id) {
        if((id <= 0 || id > preparedRotations[0].length))
            throw new PuzzleException("invalid cell");

        ICellPart cell = cells[id-1];

        if( cell.isPlaceHolder())
            throw new PuzzleException("already placeHolder");

        cell.setPlaceHolder();
        -- cellsAmount; // TODO 2d_poly
//        ++ placeHolders;

        for (int i=0; i< totalRotations; i++)
            preparedRotations[i]  [id-1].setPlaceHolder();
    }

    /** used by computeAnchorIndex. reset the marked IDs based on the order of the cells in the lattice,
	for example: LEFT, UP, RIGHT and finally DOWN */
	public void markGridIds(int id, ICellPart cell) {

        if (cell.getId().getId() == Integer.MAX_VALUE) {
            cell.getId().setId(id);
            for (int edge=0; edge<cell.getCell().length; edge++)
                if (cell.getCell(edge)!=null) {
                    int newId = id;
                    newId += dummyCellPart.dummyEdge.mark(edge); // TODO var8 - fix according to the concrete edge
                    markGridIds(newId, cell.getCell(edge));
                }
        }
    }

    /** based on the order of cells in the lattice, determine the first cell to put when a try is begun */
	protected int computeAnchorIndex() {
        int originalIds[] = new int[cells.length];
        for (int i=0; i< cells.length; i++) {
            originalIds[i] =  preparedRotations[rotationIndex][i].getId().getId();
            preparedRotations[rotationIndex][i].getId().setId(Integer.MAX_VALUE);
        }

        // preparedRotations[rotationIndex][0].getId().setId(0);
        markGridIds(0, // ((CellPartTang)preparedRotations[rotationIndex][0]).direction, // 0 TODO implement markGridIds in our PartTang which will calc the real new id, based on the direction field
                 preparedRotations[rotationIndex][0]);

        int anchorIndex = 0;
        for (int i = 1; i < cells.length; i++)
            if (preparedRotations[rotationIndex][i].getId().getId() <
                    preparedRotations[rotationIndex][anchorIndex].getId().getId())
                anchorIndex = i;

        for (int i=0; i< cells.length; i++)
            preparedRotations[rotationIndex][i].getId().setId(originalIds[i]);

        return anchorIndex; //preparedRotations[rotationIndex][anchorIndex].getId().getId() ; // as index 0 to n is needed and not the temporary id
    }

    /** allocate memory for the part presentation (orientations...), which contains cellsAmount cells */
    public void prepareRotations(int cellsAmount) {

        if((totalRotations < 1))
            throw new PuzzleException("can't prepare rotations");

        this.cellsAmount = cellsAmount;

        preparedRotations = new ICellPart[totalRotations][];
        anchorIndices = new int[totalRotations];
        implied = new int[totalRotations][];
        implied[0] = new int[cellsAmount];
        for (int idx=0; idx<cellsAmount; idx++)
            implied[0][idx] = idx;
        impliedSize = 1;

        for (int i=0; i< totalRotations; i++) {
            preparedRotations[i] = new ICellPart[cellsAmount];
            for (int j=0; j< cellsAmount; j++) {
//                String concretePart = dummyCellPart.getClass().getName();
//                ICellPart cell;
//                try {
//                    Class concreteClass = Class.forName(concretePart);
////                  Constructor constructor = concreteClass.getConstructor(new Class[] {CellId.class, IEdge.class});
////                  cell  = (ICellPart) constructor.newInstance(new Object[] {new CellId(j+1), dummyEdge});
//                    cell = (ICellPart)concreteClass.newInstance();
//                    Class concreteCellId = Class.forName(cell.dummyCellId.getClass().getName());
//                    CellId cellId = (CellId)concreteCellId.newInstance();
//                    cellId.setId(new Integer(j+1));
//                    cell.init(cellId);
//                    cell.setPartId(getId());
//                    preparedRotations[i] [j] = cell;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                preparedRotations[i][j] = createCell(j+1);
            }
        }

        cells = preparedRotations[0];

        // test- keys on all
//        for (int i=0; i<totalRotations; i++) {
//            for (int j=0; j<cellsAmount; j++) {
//                preparedRotations[i][j].keysExists = true;
//                for (int k=0; k<preparedRotations[i][j].keys.length;k++)
//                    preparedRotations[i][j].keys[k] = 1;
//            }
//        }


    }

    /** see addEdge, but with no symmetry */
	public void addEdgeMono(int partId, int from, int edge, int to) {
        for (int i=0; i< totalRotations; i++) {
            ICellPart cellsRot[] = preparedRotations[i];
            cellsRot[from].setPartId(partId); // in case we're placeHolder
            cellsRot[from].put(edge, cellsRot[to], false);
        }
    }

    /** turn on this TODO to be special as in the HexPrism */
	public void specialCell(int edge) {
        for (int i=0; i< totalRotations; i++) {
            ICellPart cellsRot[] = preparedRotations[i];
            cellsRot[edge-1].setSpecial(1);
        }
    }

    /** putKey, positive is a key, negative is a lock */
	public void putKey(int cell, int edge, int key) {
        // TODO if (edge > availKeys) throw new PuzzleException
        Parts.updateKeys(key);
        for (int i=0; i< totalRotations; i++) {
            ICellPart cellsRot[] = preparedRotations[i];
            cellsRot[cell-1].putKey(edge, key); // Math.abs(key)); // why abs
        }
    }

    /** addEdge, also in the symmetric way (from "to" to "from, opposite edge <p>
	the "edge" must be legal to that lattice type */
	public void addEdge(int from, int edge, int to) {
        if((from <= 0 || from > preparedRotations[0].length))
            throw new PuzzleException("invalid 'from' cell");
        if((to <= 0 || to > preparedRotations[0].length))
            throw new PuzzleException("invalid 'to' cell");
        if((edge < 0 || edge >= dummyCellPart.dummyEdge.getSize()))
            throw new PuzzleException("'edge' out of bounds");

        // more checks can be added here, like symmetric conn, connected to cell outside of this part (can't be), etc

//        assert(! (preparedRotations[0] [from-1].getCell(edge) != null));
        if( (preparedRotations[0] [from-1].getCell(edge) != null))
            throw new PuzzleException("some connection is already there");

        for (int j=0; j<preparedRotations[0][0].cell.length; j++) {
            ICellPart cellsRot[] = preparedRotations[0];
//            if (cellsRot[from-1].getCell(j) != null) // cellsRot[to-1] && j != edge ) //.preparedRotations[0][from-1].getCell(j) == )
            if (cellsRot[from-1].getCell(j) == cellsRot[to-1])
                throw new PuzzleException("impossible connection");
        }

        for (int i=0; i< totalRotations; i++) {
            ICellPart cellsRot[] = preparedRotations[i];
            cellsRot[from-1].put(edge, cellsRot[to-1], true);
        }
    }

    public void deleteRotation(int rot) {
        -- totalRotations;

        if (totalRotations == 0)
            throw new PuzzleException("part " + id + " - no rotations available");

        for (int i = rot; i< totalRotations; i++) {
            preparedRotations[i] = preparedRotations[i+1];
            anchorIndices[i] = anchorIndices[i+1];
    //        trips[i] = trips[i+1];
        }
        preparedRotations[totalRotations] = null;
        anchorIndices[totalRotations] = -1;
    }

    /** used by completeRotations, determine how many duplicates there are in the orientations, unless notCheckDup */
	public int checkDuplicity(boolean notCheckDup) {
        if (notCheckDup)
            return -1;

        boolean identical = false;
        boolean identicalFound = false;

        int partId = getId();

        if (partId == IGrid.GRID_ID)
            partId = ICellPart.CLEAR_PIECE_ID;

        for (int i=0; i< rotationIndex && !identical; i++) {

            // all cells, in case the part is not connected
            for (int j=0; j<cellsAmount && !identical; j++)
                // run over all, maybe it's 1>2>3 vice 3>2>1 -> same
                for (int z=0; z<cellsAmount && !identical; ++z) {
                    identical = Utils.dfsComparePart(preparedRotations[rotationIndex][j], preparedRotations[i][z], partId);

                    // check for non real duplicate
                    if (identical && getId() == IGrid.GRID_ID) {
                        LOG.debug(preparedRotations[rotationIndex][j].toString());
                        LOG.debug(preparedRotations[i][z].toString());
                        LOG.debug(rotationIndex + " " + i);


//                        System.out.println("iden");
//                        if (preparedRotations[rotationIndex][j].getId().getId() == preparedRotations[i][z].getId().getId()) {
//                            identical = false;
//                        }
                    }

                    // check for unique implied
                    if (identical) {
                        if (partId == ICellPart.CLEAR_PIECE_ID)
                            LOG.debug("f");

identicalFound = true;

                        boolean newImplied = true;
                        for (int impliedIdx=0;impliedIdx<impliedSize && (newImplied); impliedIdx++) {
                            int same = 0;
                            for (int idx=0; idx<cellsAmount; idx++)
                                if (implied[impliedIdx][idx] == preparedRotations[rotationIndex][idx].getBreadcrumb()-1)
                                    same++;
                            if (same == cellsAmount)
                                newImplied = false;
                        }

                        if (newImplied) {
                            implied[impliedSize] = new int[cellsAmount];
                            for (int idx=0; idx<cellsAmount; idx++)
                                implied[impliedSize][idx] = preparedRotations[rotationIndex][idx].getBreadcrumb() - 1;
                            ++ impliedSize;
                        }
                    }

                    // revert
                    Utils.dfsUncompare(preparedRotations[rotationIndex][j]);
                    Utils.dfsUncompare(preparedRotations[i][z]);
                }

//                if (getId() == IGrid.GRID_ID && identical) // && reflective == 1
//                {
//                    identical = false;
//                    System.out.println("identical");
//                }
        }

        if (identicalFound) {
            deleteRotation(rotationIndex);
            return rotationIndex;
        }
        else
            return -1;
    }

    public boolean equals(Object o) {
        final IPart that = (IPart) o;
        return id == that.id;
    }

    // defined, as equals was defined
    public int hashCode() {
        return id;
    }

    private void dfsPut(int cellIndex, int rotation) {

//        if (cells[cellIndex].isBreadcrumb())
//            return;

        cells[cellIndex].setBreadcrumb(1);

        trips[rotation].fromCell[tripIndex] = cellIndex;
//        trips[rotation].toCell[tripIndex] = cellIndex;
        trips[rotation].edge[tripIndex] = -1;

        for (int edge=0;edge<cells[cellIndex].getCell().length;edge++) {
            if (cells[cellIndex].getCell(edge)!=null) {
                int newCellIndex = cells[cellIndex].getCell(edge).getId().getId()-1;
                if (! cells[newCellIndex].isBreadcrumb()) {
                    trips[rotation].fromCell[tripIndex] = cellIndex;
                    trips[rotation].edge[tripIndex] = edge;
                    tripIndex ++;
                    dfsPut(newCellIndex, rotation);
                }
            }
        }

    }

    /** save the DFS result - save recursive calls - speed improvement when putting a part */
	public void calcTrips() {
        trips = new Trip[getTotalRotations()];
        for  (int rotationIndex =0; rotationIndex <getTotalRotations(); rotationIndex++) {
            trips[rotationIndex] = new Trip(cellsAmount); // cells.length); // cellsAmount+ placeHolders);
            rotate(rotationIndex);
            tripIndex = 0;
            dfsPut(anchorIndices[rotationIndex], rotationIndex);

            for (ICellPart cell: getCells())
                cell.clearBreadcrumb();
            //remove();
        }
        rotate(0);
    }

    /** determine if there is a cell which just "occupies" a cell in the lattice with no need to put a part on */
	public boolean isPlaceHoldersExist() {
        for (ICellPart cell: cells)
            if (cell.isPlaceHolder())
                return true;
        return false;
    }

    ///// ABSTRACTs

    /** reflect LEFT-RIGHT, this is always transformation index 0 */
	protected void reflect(ICellPart cells[]) {
        for (ICellPart cell: cells)
            cell.rotate(0);
    }

    /** rotate, each cell independently */
	public void rotate(ICellPart cells[], int r) {
        for (ICellPart cell: cells)
            cell.rotate(r);
    }

    /** find how many transformation from this kind is needed to come back to the original cell */
	protected int rotationCycle(ICellPart a, int r) {
        int friends = a.getCell().length;
        ICellPart orig[] = new ICellPart[friends];
        for (int i=0; i<friends; i++) {
            a.getCell()[i] = orig[i] = new ICellPart();  // TODO
//            if (i==0) {
//                orig[i].keys = new int[friends];
//                orig[i].keys[0] = 1;
//            }
        }
        int cycle = 0;
        boolean iden;
        do {
            a.rotate(r);
            ++cycle;
            iden = true;
            for (int i=0; i<friends; i++)
                if (a.getCell()[i] != orig[i]) // || (a.getCell()[i].keys != null && orig[i].keys != null && ! Arrays.equals(a.getCell()[i].keys,orig[i].keys)))
                    iden = false;
        }
        while (! iden);
        return cycle;
    }

	/** returns a new Cell of the concrete Part */
    protected ICellPart createCell(int id) {
//        template
//        ICellPart cell = new CellPart2D(new CellId(id));
//        cell.setPartId(getId());
//        return cell;
        return null;
    }

    /** creates all the orientations, returning a list of the redundant, unldess notCheckDup is true <PRE>
    template:
	
    List<Integer> toDel = new ArrayList<Integer>();
    rotationIndex = 0;
    for (int sym = 0; sym<reflective;sym++)
    for (int rot=0; rot< rotations; rot++) {
        if (sym == 1)
            reflect(preparedRotations[rotationIndex]);
        for (int r=0; r<rot; r++)
            realRotate(preparedRotations[rotationIndex]);
    
        anchorIndices[rotationIndex] = computeAnchorIndex();
    
        if (checkDuplicity(notCheckDup) == -1)
            ++ rotationIndex;
        else
            toDel.add(rotationIndex);
    
    }
    totalRotations = rotationIndex;
    rotationIndex = -1;
    return toDel;
	*/
	public List<Integer> completeRotations(boolean notCheckDup) {
//        template
//        List<Integer> toDel = new ArrayList<Integer>();
//        rotationIndex = 0;
//        for (int sym = 0; sym<reflective;sym++) {
//            if (sym == 1)
//                reflect(preparedRotations[rotationIndex]);
//            for (int rot=0; rot< rotations; rot++) {
//                for (int r=0; r<rot; r++)
//                    realRotate(preparedRotations[rotationIndex]);
//
//                anchorIndices[rotationIndex] = computeAnchorIndex();
//
//                if (checkDuplicity(notCheckDup) == -1)
//                    ++ rotationIndex;
//                else
//                    toDel.add(rotationIndex);
//            }
//        }
//        totalRotations = rotationIndex;
//        rotationIndex = -1;
//        return toDel;
        return null;
    }



}
