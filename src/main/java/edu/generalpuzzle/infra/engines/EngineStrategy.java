package edu.generalpuzzle.infra.engines;

import edu.generalpuzzle.infra.*;
import org.apache.log4j.Logger;

import java.text.NumberFormat;
import java.util.*;
import java.util.logging.Level;

/**
 * Created by IntelliJ IDEA.
 * Date: 01/06/2008
 */
public abstract class EngineStrategy extends IEngineStrategy {

    public static final Logger LOG = Logger.getLogger(EngineStrategy.class.getName());

    // comm with outer world

    private final Shared shared;

    public static boolean GENERATE_BY_ALL = false;

    public static final int ENGINE_TYPE_DLX = 0;
    public static final int ENGINE_TYPE_RECURSIVE = 1;
    public static final int ENGINE_TYPE_ITERATIVE = 2;

    public static int ENGINE_TYPE = ENGINE_TYPE_DLX;
    public static boolean GRAPH_FOR_ALL = false;

    public static boolean INTERNAL_VIEWER = true;
    public static void set_INTERNAL_VIEWER(boolean b) { INTERNAL_VIEWER = b;}

    public static int DL_SPLITS = 0;
    public static void set_DL_SPLITS(int b) { DL_SPLITS = b; }

    public static boolean NO_ORIENTATIONS = false;
    public static void set_NO_ORIENTATIONS(boolean b) { NO_ORIENTATIONS = b; }

     public static int get_ENGINE_TYPE() { return ENGINE_TYPE; }
    // get_GENERATE_BY_ALL; } // TODO

    public static boolean trace = false;

    public static void set_GENERATE_BY_ALL(boolean b) { GENERATE_BY_ALL = b; }       // call this before "new Engine"
    public static void set_GRAPH_FOR_ALL(boolean b) { GRAPH_FOR_ALL = b; }       // call this before "new Engine"
    public static void set_ENGINE_TYPE(int b) { ENGINE_TYPE = b; }

    public static void setTrace(boolean b) { trace = b; }

    public static boolean S_HEURISTIC = true;

    public static void set_S_HEURI(boolean b) { S_HEURISTIC = b; }

    public static boolean ST_HEURISTIC = true;

    public static void set_ST_HEURI(boolean b) { ST_HEURISTIC = b; }
    public static void set_AUTO_GRAPH_IT(boolean b) { graphIt = b;}

    protected static boolean toSave = false;
    protected int fromPart;
    protected int toPart;

    private final StringBuffer sbStatistics = new StringBuffer(200);

    // end of comm

    protected IPart unique;

    // current solution


    protected List<Integer> partsIndices;
    protected final int[] solution;

    protected char [] currBranch;
    protected int depth;

    // input to EngineStrategy

    protected int partsInSolution;


    // helpers


    // optimizations

    protected /*static*/ int possibleRotations[][][][]; // [part id] [rotation index] [gridCell] - can be "canPut"
    // private static boolean initialized = false;

    // duplications

//    private int uniqsFound[][]; // breadCrumbs, this [ cell index] [rotation index] was seen before
    public static int[][] implied; // [implied index] [cell index] - new location for that cell // TODO public
//    private int origImpliedSize;
    public static int uniqueInImplied[];
//    private StringBuffer solutionSoFarBuffer;

//    private List<Integer> all = new ArrayList<Integer>();

    NumberFormat nf = NumberFormat.getInstance();

    ///////////////////////////////////////////////////////

    // comm

    public static void save() {
        toSave = true;
    }


    public void setRange(int fromPart, int toPart) {
        if (toPart > partsAmount)
            toPart = partsAmount;

        this.fromPart = fromPart;
        this.toPart = toPart;
    }

    // end of comm

    public EngineStrategy(Parts parts, IGrid grid) {

        super(parts, grid);

        for (ICellPart a: parts.getParts().get(0).getCells())
            if (a != null) {
                System.out.println("initializing engine" +id);
                break;
            }

        parts.checkUnique();
        partsIndices = new ArrayList<Integer>();

        for (int i=0; i<partsAmount; i++)
            partsIndices.add(i);

        smallestPart = Integer.MAX_VALUE;
        for (int i=0; i<partsAmount; i++) {
            int partSize = parts.getParts().get(i).getCellsAmount();
            if (partSize < smallestPart)
                smallestPart = partSize;
        }
        grid.setSmallestPart(smallestPart);

        partWithPlaceHolderExist = false;
        for (int i=0; i<partsAmount; i++)
            if (parts.getParts().get(i).isPlaceHoldersExist())
                partWithPlaceHolderExist = true;

        solution = new int[partsAmount];
        currBranch = new char[partsAmount+1];
        toPart = partsAmount;

        shared = Shared.getInstance();

        implied = helper(grid.getCells());

//        solutionSoFarBuffer = new StringBuffer(partsAmount*2);

        if (parts.getUniquePart() != null) {
            int uniqRotations = parts.getUniquePart().getTotalRotations();
//              uniqsFound = new int[grid.getCells().size()][uniqRotations];
//                uniqsFoundSolutions = new Set[grid.getCells().size()][uniqRotations];
//                Collections.synchronizedSet(new Set[grid.getCells().size()][uniqRotations]);

            shared.init(grid.getCells().size(), uniqRotations);
        }

        partsInSolution = parts.getPartsInSolution();


    }

    public int getTotalSolutions() {
        return uniqueSolutions * impliedSize;
    }


    public abstract boolean isAlive();

    @Override
    public String getStatistics() {

        sbStatistics.setLength(0);

//        NumberFormat nf = NumberFormat.getInstance();

//        Date end = Calendar.getInstance().getTime();
        long end = System.currentTimeMillis();

        this.elapsedTime = end - startTime;

        if (! isAlive()) {
            sbStatistics.append("DONE. ");
            if (endTime == 0)
                endTime = end;
            this.elapsedTime = endTime - startTime;
        }

//        elapsedTime /= 1000;

        if (startTime == 0) {
            elapsedTime = 0;
        }

        if (toPart - fromPart != partsAmount)
            sbStatistics.append(partsAmount).append(" parts" + ", 'working' on ").append(fromPart).append("..").append(toPart-1);

//        sbStatistics.append(grid.getLeftCells());


        if (get_ENGINE_TYPE() != EngineStrategy.ENGINE_TYPE_DLX)
            sbStatistics.append("\nBT: " + getLastSolution());
        else
            sbStatistics.append("\nDL: " + lastSolutionDL);

        sbStatistics.append("\nelapsed time ").append(nf.format(elapsedTime)).append(" seconds");
        sbStatistics.append("\nnumber of ");
        if (! GENERATE_BY_ALL)
            sbStatistics.append("unique ");
        sbStatistics.append("solutions ").append(nf.format(uniqueSolutions)).append(" ");
        sbStatistics.append("\ntried ").append(nf.format(totalNodes)).append(" parts");
//        if (elapsedTime != 0)
//            sbStatistics.append("\nat " + nf.format(triedParts/elapsedTime) + " parts per milli-second");

        sbStatistics.append("\n");
        sbStatistics.append(threadName);//Thread.currentThread().getId());

        return sbStatistics.toString();
    }


    public void preSolve() {

        threadName = Thread.currentThread().getName();

        startTime = Calendar.getInstance().getTime().getTime();
        
        totalNodes = 0;
       for (int i=0; i<branches.length; i++) { //parts.getParts().size(); i++) {
           totalNodes += branches[i];
       }

        if (parts.keysUsed.size() > 0 || parts.locksUsed.size() > 0) {
            grid.considerKeys = true;
            LOG.debug("considerKeys = true");
        }
        
        if (grid.getLeftCells() != parts.getTotalFill() // TODO 2d_poly
                - partsInSolution * parts.getParts().get(0).getCellsAmount()) { // TODO maybe not all the same size
            System.out.println("error: invalid config, size of parts " + (parts.getTotalFill() - partsInSolution * parts.getParts().get(0).getCellsAmount())
                    + " vs. grid " + grid.getLeftCells());
             System.exit(1);
        }
    }

    public void postSolve() {
        getRatio();
        System.out.println("\nEngine" + id + " - DONE");


        showSummary();
    }

    @Override
    protected void solved() {

        boolean preSolved = preSolved();
        if (! preSolved) {
            // LOG.debug("solved, but keys not match");
            return;
        }

//        if (lastSolution.length() < 12*2)
//            return;

        if (parts.getSamePartExist() > 0) { // divide into groups... the implied are the N! of each group
            int last[] = new int[parts.groups.length];

            System.arraycopy(parts.groups, 0, last, 0, last.length);

            for (int i=0; i< lastSolution.length(); i+=3) {
                char current = lastSolution.charAt(i);

                for (int j=0;j<parts.getSamePartExist();j++)
                    if (current >= parts.groups[j] && current < parts.groups[j] + parts.groupsSize[j]) {
                        if (current < last[j])
                            return;
                        last[j] = current;
                    }
            }

            if (sameSolution())
                return;

        }

//        if (LOG.isDebugEnabled()) {
            LOG.debug("EngineStrategy" + id + " - found solution " + (uniqueSolutions+1) + " - branches " + nf.format(triedParts) + " - " + lastSolution + " - " + grid.presentation((implied[0])));
//        }

        // parts.getUnique()) { // && /* leftParts= */ partsAmount - i <= partsAmount_half) { // in > we continued, so the <= can be solutions
        int uniqueRot = unique != null ? unique.getRotationIndex() : 0;
        if (uniqueInImplied[uniqueRot] != 0) {
            int gridId = unique.getCells()[ unique.getAnchorIndex() ].getGridCell().getId().getId() ;
            int newLocation = implied[uniqueInImplied[uniqueRot]][ grid.getCellIndex(gridId) ];

            if (newLocation == grid.getCellIndex(gridId)) {

                StringBuffer sDup = grid.presentation(implied[uniqueInImplied[uniqueRot]]);

                Map<String, Integer> set = shared.uniqsFoundSolutions[newLocation][uniqueRot];

//                synchronized (set) { //uniqsFoundSolutions[newLocation][uniqueRot]) {

                    if (set.get(sDup.toString()) != null) {
                        LOG.debug("duplicate found at EngineStrategy" + id);
                        return;
                    }

                    StringBuffer s = grid.presentation(implied[0]);
                    set.put(s.toString() ,0);
                // System.out.println("another one to set " + newLocation + " " + uniqueRot);
//                }

            }
        }

        if (parts.getSamePartExist() > 0)
            shared.sames.put(grid.presentation(implied[0]).toString(), 0);

//        lastSolution = solutionSoFarBuffer.toString();

        if ((GRAPH_FOR_ALL || graphIt)) { //&& id == 0) { // var8
//        if (id==0 && uniqueSolutions%10 == 0) { // keys debug
            synchronized(shared) {
                if (graphIt || GRAPH_FOR_ALL) {
                    EngineStrategy.solutionPresentation =
                            "Parts order " + lastSolution + "\n" +
//                    grid.presentation(implied[0]).toString() + "\n" +
                            grid.showBuf(implied[0]).toString() + "\n";

                    GraphIt.getInstance().graphIt(cell1st);
                    GraphIt.getInstance().buildXml(lastSolution);

                    graphIt = false; // shoule be synchronized
                }
            }
        }

        uniqueSolutions++;
        static_uniqueSolutions++;

        // new original place
        
//        if (timeTo1st == -1) {
//            timeTo1st =  (System.currentTimeMillis()- startTime) / 1000.0;
//            System.out.println("\ntimeTo1st: " + timeTo1st);
//            LOG.debug("timeTo1st: " + timeTo1st);
//        }

        if (uniqueSolutions == 1 && // TODO as before - unique_Solutions?
                timeTo1st == -1 && ! showAll) {// time to first solution
            timeTo1st =  (System.currentTimeMillis()- startTime) / 1000.0;
            System.out.println("\ntimeTo1st: " + timeTo1st);
            LOG.debug("timeTo1st: " + timeTo1st);
            first_sol = grid.presentation(implied[0]).toString();
            first_sol_short = lastSolution;

            System.out.println("\n1st solution statistics");
            showSummary();

//            for (int i=1; i<impliedSize;i++) {
//                System.out.println();
//                System.out.print/*ln*/("Solution #" + (uniqueSolutions) + ", copy " + i);
//                grid.show(implied[i]);
//            }

            // System.exit(0); // profiling
        }


        if (! FULL_OUTPUT && (uniqueSolutions > 1 || id >= 1) && ! showAll)
            return;

        System.out.println();
        System.out.println("Solution #" + uniqueSolutions);// + ", copy " + 0);
        System.out.print("\nParts order ");
        System.out.println(lastSolution);

        grid.show(implied[0]);


//        StringBuffer s = grid.presentation(implied[0]);
//        all.add(s.toString().hashCode());

        if (showAll)
            for (int i=1; i<impliedSize;i++) {
                System.out.println();
                System.out.println("Solution #" + (uniqueSolutions) + ", copy " + i);
                grid.show(implied[i]);

//                // TODO if verify...
//                s = grid.presentation(implied[i]);
//                if (all.contains(s.toString().hashCode()))
//                    System.out.println("DUP +NOT VERFIFIED!!! " + s.toString()); // TODO DO NOT TRUST IT
            }

        // original place

    }

    public void removeImpossible() {
        int realCellIndex = grid.getCurrCellIndex();
        possibleRotations = new int[ parts.getParts().size() ][][][];

        int total = 0;
        int totalReduced = 0;

        for (int p: partsIndices) {
            IPart part = parts.getParts().get(p);
            int origRotations = part.getTotalRotations();
            total += origRotations;
            if (id == 0) LOG.debug( part.toString());
            for (int r=part.getTotalRotations()-1;r>=0; r--) {// while (! currPart.rotate()) {
                part.rotate(r);
                int failures = 0;
                for (int i=0; i<grid.getCells().size(); i++) {
                    // WHAT ABOUT PLACE_HOLDERS TODO
                    grid.setCurrCellIndex(i);
                    boolean canPut = false;
                    if (! grid.getCells().get(i).isPlaceHolder()) // TODO 2d_poly
                        canPut  = grid.canPut(part);
                    if (canPut)
                        grid.removeLast(part);
                    else
                        ++failures;
                }
                if (failures == grid.getCells().size())
                    part.deleteRotation(r); // TODO poly???
            }

            possibleRotations[p] = new int[origRotations][][];
            for (int r=part.getTotalRotations()-1;r>=0; r--) {
                part.rotate(r);
                possibleRotations[p][r] = new int[grid.getCells().size()][];
                for (int i=0; i<grid.getCells().size(); i++) {
                    grid.setCurrCellIndex(i);
                    boolean canPut = grid.canPut(part);
                    if (! canPut)
                        possibleRotations[p][r][i] = null; // TODO 2010
                    else {
                        possibleRotations[p][r][i] = new int[part.getCellsAmount()];
                        int cellindex = 0;
                        for (ICellPart cell: part.getCells()) {
                            possibleRotations[p][r][i][cellindex]=grid.getCellIndex(cell.getGridCellId()) ; // * 100 + cellindex); // up to 10 cells per // TODO 2d_poly
                            ++ cellindex;
                        }
                    }
                    if (canPut) {
                        grid.removeLast(part);
                    }
                }
            }

            if (part.getTotalRotations() < origRotations && id == 0)
                LOG.debug("  global reduced to " + part.getTotalRotations());
            totalReduced += part.getTotalRotations();
        }

        totalOrientations = total;
        totalReducedOrientations = totalReduced;

        if (id == 0) {
            LOG.debug("total orientations: " + total);
            LOG.debug("total reduced orientations: " + totalReduced);
        }

        grid.setCurrCellIndex(realCellIndex);

        grid.goForward();
    }

    public int[][] helper(List<ICellPart> gridCells2) {

        // do all of this to get the implied

        // treat the grid as a part, to calculate duplicates

        IPart gridPart = parts.getGridPart();
        List<ICellPart> gridCells = grid.getCells();

        gridPart.prepareRotations(gridCells.size());

        int implied[][];

        // edu.generalpuzzle.test mode?
        if (gridPart.getTotalRotations() == 1) {
            implied = new int[1][gridPart.getCellsAmount()];
            for (int i=0; i< gridPart.getCellsAmount(); i++)
                implied[0][i] = i;
        }
        else {

            for (ICellPart cell: gridCells)
                for (int i=0; i<cell.getCell().length; i++)
                    if (cell.getCell(i) != null) {
                        ICellPart cellPartHelper = new ICellPart();

                        CellId from = cell.getId();
                        cellPartHelper.setId(from);
                        int intFrom = gridCells.indexOf(cellPartHelper);

                        CellId to = cell.getCell(i).getId();
                        cellPartHelper.setId(to);
                        int intTo = gridCells.indexOf(cellPartHelper);

                        gridPart.addEdgeMono(cell.getPartId(), intFrom, i, intTo); /// since we're coping the cells, no need to creates the symmetrics
                    }

            this.parts.getParts().add(gridPart);

            List<Integer> toDel = gridPart.completeRotations(false);

            implied = gridPart.getImplied();
            impliedSize = gridPart.getImpliedSize();

            for (int i=0; i<implied[0].length; i++)
                implied[0][i] = i;

            //  TODO remove

            if ( implied[0].length == 60 && 
                    grid.getClass().getName().endsWith("Grid3D")) // x!=y!=z, add reflective to make it 8
                for (int i=0; i<4; i++) {
                    impliedSize++;
                    implied[i+4] = new int[implied[0].length];
                    for (int j=0; j<implied[0].length ;j++)
                        implied[i+4][implied[0].length -1-j]= (implied[i][j]);
                }

//             reorder grid cell ids according to the desired order

            for (int i=0; i< gridPart.getCellsAmount(); i++)
                gridPart.getCells()[i].getId().setId(Integer.MAX_VALUE);

            gridPart.markGridIds(0, gridPart.getCells()[0]);

            grid.reorder(gridPart.getCells());

            // remove non unique rotations from the uniquePart

            unique = parts.getUniquePart();

//            origImpliedSize = impliedSize;

            if (! (unique != null && ! GENERATE_BY_ALL)) {
                if (unique != null)
                    impliedSize = 1;
            }
            else
            if (toDel != null && unique != null)
                for (Integer i: toDel)
                    unique.deleteRotation(i);


            if (id == 0)
                LOG.debug("Unique " + unique);
        }

        // calc the duplications of the unique part in the implied

        unique = parts.getUniquePart();

        if (unique != null && id == 0)
            System.out.println("\n" + unique + " <- unique, after processing\n");

        // if (! initialized) { initialized = true; // TODO can be calc once

        removeImpossible();

        uniqueInImplied = new int[unique != null ? unique.getTotalRotations(): 1];

        if (unique != null && gridPart.getTotalRotations() != 1) { // for tests

            for (int u=0; u<unique.getTotalRotations(); u++) {

                // where is the unique in the indices
                int uniqueInIndices = -1;

                for (int i=0; i<partsIndices.size(); i++)
                    if (parts.getParts().get(i) == unique) {
                        uniqueInIndices = i;
                        break;
                    }

                unique.rotate(u);

                // find the first place we can put unique
                int canPutIndex = 0;
                while (possibleRotations[uniqueInIndices][u][canPutIndex] == null)
                    ++ canPutIndex;

                // put
                grid.dfsPut(unique.getCells()[unique.getAnchorIndex()], gridPart.getCells()[canPutIndex]);

                // find cells with unique
                int uniquePlaceInGrid[] = new int[unique.getCellsAmount()];
                int index = 0;
                for (int i=0; i< gridPart.getCellsAmount(); i++)
                    if (gridPart.getCells()[i].getPartId() == unique.getId())
                        uniquePlaceInGrid[index++] = i;

                // remove the previous put
                IGrid.remove(unique);
                for (int i=0; i<index; i++)
                    gridPart.getCells()[uniquePlaceInGrid[i]].setPartId(IGrid.GRID_ID);

                // put
                int originalCurrCellIndex = grid.getCurrCellIndex();
                grid.setCurrCellIndex(canPutIndex);
                grid.canPut(unique);

                // show the implied
                if (LOG.isDebugEnabled() && id == 0) {
                    LOG.debug("all implied of rotation " + u);
                    for (int z=0; z<impliedSize; z++) {
                        LOG.debug("implied " + z);
//                        if (LOG.isDebugEnabled())
                            LOG.debug(grid.showBuf(implied[z]).toString());
                    }
                }

                int rotationPlaceInCopies = 0;

                for (int i=1; i<impliedSize; i++) {

                    // put in the implied
                    for (int j=0; j<uniquePlaceInGrid.length; j++) {
                        ICellPart cell = gridPart.getCells()[implied[i][ uniquePlaceInGrid[j]]];
                        cell.setPartId(unique.getId());
//                        if (cell.isPlaceHolder())
//                            cell.setPlaceHolder();
                    }

                    // canPutIndex equals to uniquePlaceInGrid[ anchorIndex]

                    if (Utils.dfsComparePart(
                            grid.getCells().get(canPutIndex),
                            gridPart.getCells()[implied[i][ canPutIndex]], unique.getId()))
                    {
                        rotationPlaceInCopies = i;
                        if (id == 0) LOG.info("rotationPlaceInCopies for rotation " + u + " = " + i);
                    }

                    // undo the previous put
                    Utils.dfsUncompare( grid.getCells().get(canPutIndex));
                    Utils.dfsUncompare( gridPart.getCells()[implied[i][ canPutIndex]]);

                    for (int j=0; j<uniquePlaceInGrid.length; j++) {
                        ICellPart cell = gridPart.getCells()[implied[i][ uniquePlaceInGrid[j]]];
                        cell.setPartId(IGrid.GRID_ID);
//                        if (cell.isPlaceHolder())
//                            cell.setPlaceHolder();
                    }

                }

                // remove the previous put
                grid.removeLast(unique);
                grid.setCurrCellIndex(originalCurrCellIndex);

                uniqueInImplied[u] = rotationPlaceInCopies;
            }

        }

        // more helpers

        parts.getParts().remove(gridPart);

        // end of: if (! initializ/ed) initialized = true; // TODO can be calc once

        for (IPart part: parts.getParts()) // TODO 2d_poly
            part.calcTrips();

        return implied;
    }

    protected int size = 0; // helper for islandRecursive
    protected int smallestPart = 0;
    protected boolean partWithPlaceHolderExist;

    public void setSmallestPart(int smallestPart) {
        this.smallestPart = smallestPart;
    }

    public int islandSize(ICellPart gridCell) { // var8 move to Igrid
        if (! EngineStrategy.ST_HEURISTIC)
            return Integer.MAX_VALUE;

        if (gridCell.isEmptyPartId()) {
            size = 1; // in this point we know the island is at least at size 1
            islandRecursive(gridCell); // grid.getCells().get(grid.getCurrCellIndex()));
        }
        else
            size = 0;
        return size;
    }

    private void islandRecursive(ICellPart gridCell) {
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

    protected abstract boolean preSolved();

    private boolean sameSolution() {

        Map<Character,Character> marks = new HashMap<Character,Character>();

        for (Map.Entry<String,Integer> origentry: shared.sames.entrySet()) {
            String orig= origentry.getKey();

            for (int i=0; i<impliedSize;i++) {
                String s = grid.presentation((implied[i])).toString();
                boolean same = true;
                marks.clear();
                for (int j=0; j<orig.length(); j+=2) {
                    char origcurrent = orig.charAt(j+1);
                    char current = s.charAt(j+1);

//                    Character currentMark = marks.get(current);

                    if (current == origcurrent) {
//                        if (currentMark == null)
//                            marks.put(current,  origcurrent);
//                        else if (currentMark != origcurrent) {
//                            same = false;
//                            break;
//                        }
                        continue;
                    }

//                    if (currentMark != null && currentMark != origcurrent) {
//                        same = false;
//                        break;
//                    }


                    IPart spart = parts.get(current);
                    IPart origpart = parts.get(origcurrent);

                    boolean sDup = spart.anotherOne != 0;
                    boolean origDup =origpart.anotherOne != 0;

                    if (sDup && origDup && spart.anotherOne == origpart.anotherOne) {
                        Character currentMark = marks.get(current);
                        if (currentMark == null)
                            marks.put(current,  origcurrent);
                        else if (currentMark != origcurrent) {
                            same = false;
                            break;
                        }
                    }
                }

                if (same) {
//                    LOG.debug("duplicate found, current solution [" + s + " implied " + i + "] equals to [" + orig + "]");
                    return true;
                }
            }
        }

        return false;
    }

    protected char rotationChar(int rot) {
        if (rot < 10)
            return (char)('0' + rot);
        else
            return (char)('a' + rot-10);
    }

    public String getLastSolution() {
        return lastSolution;
//        StringBuffer s = new StringBuffer(depth+1);
//        if (depth == 0)
//            s.append(".");
//        else
//            for (int i=0; i<depth; i++)
//                s.append(currBranch[i]);
//        return s.toString(); //lastSolution;
    }


}
