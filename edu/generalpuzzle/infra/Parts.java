package edu.generalpuzzle.infra;

import edu.generalpuzzle.main.PuzzleException;
import edu.generalpuzzle.infra.engines.EngineStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * Date: 29/05/2008
 */
public class Parts {
    protected final List<IPart> parts;
    protected final Map<Integer,IPart> partsMap;
    protected int totalFill = 0;
    protected int uniqueId = '_';

    protected int samePartExist;
    public int groups[] = new int[12];
    public int groupsSize[] = new int[12];

    public static List<Integer> keysUsed = new ArrayList<Integer>();
    public static List<Integer> locksUsed = new ArrayList<Integer>();
    public static int locksUsedSize;

    public static boolean matches[][];

    /** add a key to keys repository */
	public static void updateKeys(int key) {
        if (key > 0) {
            if (! keysUsed.contains(key))
                keysUsed.add(key);
        }
        else if (key < 0) {
            if (! locksUsed.contains(-key)) {
                locksUsed.add(-key);
                locksUsedSize ++;
            }
        }
        else
            System.out.println("no key");
    }

    /** in STAIRS the partsInSolution is like -1, there is a spare part */
	protected int partsInSolution = 0;

    public IPart getGridPart()
    {
        System.out.println("error: default gridPart");
        return new IPart(IGrid.GRID_ID);
    }

    public IPart getUniquePart() {
        return get((char)uniqueId);
    }

    /** validity of the chosen unique part, it should be the one with the maximum orientations  */
	public void checkUnique() {
        int found = -1;
        int maxRotations = -1;
        for (IPart part: parts) {
            if (part.getId() == uniqueId)
                found = parts.indexOf(part);
            if (part.getTotalRotations() > maxRotations)
                maxRotations = part.getTotalRotations();
        }
        if (found != -1) {
            if (parts.get(found).getTotalRotations()  < maxRotations || parts.get(found).getCellsAmount() == 1)
                System.out.println("error: you should choose other unique");
        }
        else
            System.out.println("error: no unique is used");
    }

    public int getUnique() {
        return uniqueId;
    }

//    // remove from the unique part all the symmetrics derived by the grid symmetrics
//    public Integer[][] helper(List< ICellPart> gridCells) {
//        return new Integer[0][];
//    }
//
    public void add(IPart part) {

        if (part == null)
            return;

        // edu.generalpuzzle.test validity
        List<ICellPart> partCells = new ArrayList<ICellPart>();
        for (ICellPart cell: part.getCells())
            partCells.add(cell);

        for (ICellPart cell : part.getCells()) {
            if( (cell.getCell().length == 0 && part.getCells().length != 1))
                throw new PuzzleException("part contains empty cell");

            for (ICellPart a : cell.getCell())
                if ((a != null && ! partCells.contains(a))) // assertTrue( part.getCells().contains(edge.getValue()));
                    throw new PuzzleException("part contains invalid cells");

            int nullElements = 0;
            for (int edge =0; edge<cell.getCell().length; edge++)
                if (cell.cell[edge] == null && cell.keys[edge] == 0)
                    nullElements++;
            if (nullElements == cell.getCell().length && partCells.size()>1)
                throw new PuzzleException("too many cells, " + cell + " is empty\nreduce cellsAmount in the prepareRotations");
        }

        // TODO

        for (IPart i: parts) {// TODO ?
            if( (i.getId() ==part.getId() ) && part.anotherOne == 0)
                throw new PuzzleException("error: parts already contains such id");
        }

        parts.add(part);
        totalFill += part.getCellsAmount();// - part.getPlaceHolders();

        testRotate(part);

        partsMap.put(part.getId(), part);

    }

    /** complete the addition of the part */
	public void complete(IPart part) {

        if (part.totalRotations > 1)
            part.completeRotations(part.getId() == uniqueId && ! EngineStrategy.GENERATE_BY_ALL);
        else
            part.rotationIndex = -1;

        if (part.anotherOne == 0)
            checkAnotherOne(part);

        testRotate(part);

    }

    /** validity of the parts, is there another identical part? */
	private void checkAnotherOne(IPart part) {
        int exists = -1;

        int origId = part.getId();

        for (IPart anotherPart: parts) {
            if (part != anotherPart)
            if ((anotherPart.anotherOne != part.anotherOne) && anotherPart.getCellsAmount() == part.getCellsAmount()) {
                part.setId((char)anotherPart.getId());
                CellId cellsId[] = new CellId[part.getCellsAmount()];

                for (int i=0; i<cellsId.length; i++) {
                    cellsId[i] = part.getCells()[i].getId();
                    for (int j=0; j<part.totalRotations; j++) {
                        part.preparedRotations[j][i].setId(anotherPart.getCells()[i].getId());
                        part.preparedRotations[j][i].setPartId(anotherPart.getId());
                                // part.getCells()[i].setId(anotherPart.getCells()[i].getId()); // TODO on all the prepartdRot
                    }
                }

                for (int j=0; j<part.getCellsAmount(); j++)
                    for (int i=0; i<part.totalRotations; i++) {
                        if (Utils.dfsComparePart(part.preparedRotations[i][j], anotherPart.preparedRotations[0][j], part.getId())) {
                            exists = anotherPart.getId();
                        }
                        Utils.dfsUncompare(part.preparedRotations[i][j]);
                        Utils.dfsUncompare(anotherPart.preparedRotations[0][j]);
                    }

                for (int i=0; i<cellsId.length; i++) {
                    part.getCells()[i].setId(cellsId[i]);
                    for (int j=0; j<part.totalRotations; j++) {
                        part.preparedRotations[j][i].setId(cellsId[i]); 
                        part.preparedRotations[j][i].setPartId(origId);
                                // part.getCells()[i].setId(anotherPart.getCells()[i].getId()); // TODO on all the prepartdRot
                    }
                }

                if (exists != -1) {
                    boolean keymatch = true;
                    for (int i=0; i<cellsId.length; i++) {
                        if (part.getCells()[i].keys.length == anotherPart.getCells()[0].keys.length)
                            for (int j=0; j<part.getCells()[i].keys.length; j++)
                                if (part.getCells()[i].keys[j] != anotherPart.getCells()[0].keys[j])
                                    keymatch = false;
                    }

                    if (! keymatch)
                        exists = 0;
                    else
                        break;
                }
            }
        }

        part.setId((char)origId);

        if (exists != -1)
            throw new PuzzleException("part " + (char)part.getId() + " already exists as " + (char)exists + ". Use the \"anotherOne\" syntax");
    }

    /** for tests only */
	private void testRotate(IPart part) {
        String origPart = part.toString();
        boolean isRotationsFinised = false;
        while (! isRotationsFinised)
            isRotationsFinised =  part.rotate();
        if(! (part.toString().equals(origPart))) // use -ea as jvm param
            throw new PuzzleException("testRotate failed");
    }

    /** how many cells all the parts consumes - check if a solution is feasible */
	public int getTotalFill() {
        return totalFill;
    }

    /**  helps finding the 1st solution more quickly
     * @param neworder the new order <p>
	 */
	 @Deprecated
    public void reorder(String neworder) {
        if( (neworder.length() != parts.size()))
            throw new PuzzleException("reorder & parts not match");

        for (int i=neworder.length()-1; i>=0; -- i) {
            IPart found = get(neworder.charAt(i));
            if ((found == null))
                throw new PuzzleException("reorder & parts not match");
            parts.remove(found);
            parts.add(0,found);
        }
    }

    public IPart get(char id) {
        return partsMap.get((int)id);
//        IPart found = null;
//        for (IPart part: parts)
//            if (part.getId() ==  id) {
//                found = part;
//                break;
//            }
//
//        return found;
    }

    /** rare use, allows removing a part after it was added */
	public boolean remove(char id) { // for the bsh files - TODO edu.generalpuzzle.test
        IPart found = get(id);

        if (found != null) {
            totalFill -= found.getCellsAmount();
            return parts.remove(found);
        }
        else
            return false;
    }

    public Parts() {
        totalFill = 0;
        parts = new ArrayList<IPart>();
        partsMap = new HashMap<Integer, IPart>();
    }

    /** @param uniqueId the unique part which we don't "rotate". 0 for none */
    public Parts(int uniqueId) {
        totalFill = 0;
        this.uniqueId = Character.toUpperCase(uniqueId);
        parts = new ArrayList<IPart>();
        partsMap = new HashMap<Integer, IPart>();
    }

    public void clearUnique() {
        uniqueId = '_';
    }

    public List<IPart> getParts() {
        return (parts);
    }

    /** TODO(? relevant)  handel auto even if parts are not one after the another */
    public void anotherOne(int amount) {
        groups[samePartExist] = parts.get(parts.size()-1).getId() - amount + 1; // id of the first part, for example: A from A B C
        groupsSize[samePartExist] = amount;
        ++ samePartExist;
    }

    public int getSamePartExist() {
        return samePartExist;
    }

    /** x3d of the piece */
	public void show(char id) {
        IPart part = get(id);
        if (part != null) {
            System.out.println(part);
            GraphIt.setArgs("customPartIndex");
            GraphIt.getInstance().graphIt(part.getCells()[0]);
            GraphIt.getInstance().buildXml(Character.toString(id)); // solution is the single part
        }
        else {
            System.out.println("No such part");
        }
    }

    /** in STAIRS, 11 parts in solution, don't know who */
	public void setPartsInSolution(int partsInSolution) {
        this.partsInSolution = partsInSolution;
//        totalFill -= 5 * partsInSolution; // var8
    }

    public int getPartsInSolution() {
        return partsInSolution;
    }


}
