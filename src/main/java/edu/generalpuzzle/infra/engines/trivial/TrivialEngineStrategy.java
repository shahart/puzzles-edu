package edu.generalpuzzle.infra.engines.trivial;

import edu.generalpuzzle.infra.ICellPart;
import edu.generalpuzzle.infra.IGrid;
import edu.generalpuzzle.infra.IPart;
import edu.generalpuzzle.infra.Parts;
import edu.generalpuzzle.infra.engines.ParallelEngineStrategy;

/**
 * Created by IntelliJ IDEA.
 * Date: 04/09/2008
 */
public abstract class TrivialEngineStrategy extends ParallelEngineStrategy {

    protected IPart currPart; // do not convert into local var as the recursion needs it

    public TrivialEngineStrategy(Parts parts, IGrid grid) {
        super(parts, grid);
    }


    @Override
    public float getRatio() {
        if (! isAlive())
            return 1;

        IPart p = parts.get(currBranch[0]);
        if (p == null)
            return 0;
        int pindex = parts.getParts().indexOf(p);
        if (pindex >= fromPart)
            pindex -= fromPart;
        float ratio = pindex +  (float) p.getRotationIndex() / p.getTotalRotations(); //(parts.getParts().size()-1);
        ratio /= toPart - fromPart;

        if (depth >= 1) {
            IPart p2 = parts.get(currBranch[1]);
            int pindex2 = parts.getParts().indexOf(p2);
            if (pindex2 > pindex)
                pindex2--;
            ratio += ((float) (pindex2) / (partsAmount-1)  + (float) p2.getRotationIndex() / p2.getTotalRotations() / (partsAmount - 1)) / (toPart - fromPart ) / p.getTotalRotations();
        }

        return ratio;
    }

    /** roo */
    protected boolean rollback(int leftParts) {
        ICellPart cell = grid.getCells().get(grid.getCurrCellIndex());
        return ! grid.verifyMiddle() || // TODO if all the parts are the same
                (leftParts != partsInSolution+1 &&islandSize(cell) /*%5 != 0*/< smallestPart && ! partWithPlaceHolderExist);
    }


    protected void track(int leftParts, int i, int partIndex) {

//                                    String s2="";
//                                    for (IPart p: parts.getParts())
//                                        if (p.getCells()[0].getGridCellId() < 500)
//                                            s2 += (char)p.getId() +" " + p.getRotationIndex() + "\t";
//                                    s2 += " - ";
//                                    for (int k=0; k<partsAmount-leftParts; k++)
//                                            s2 += (solution[k]);
//                                    s2 += (partIndex);
//                                    System.out.println(s2);
//////
//////                                    if (s2.startsWith("A 0\tD 6"))
//////                                        System.out.println("777");
//////                                    if (grid.verify() && leftParts == 1)
//////                                        System.out.println("***");
////
//        int newLoc[] = new int[grid.getCells().size()];
//        for (int j=0; j<newLoc.length; j++)
//            newLoc[j] = j;
//        System.out.println(grid.showBuf(newLoc));

        depth = partsAmount - leftParts;
        currBranch[depth] = (char)parts.getParts().get(partIndex).getId();

        ++branches[partsAmount - leftParts];
        ++totalNodes;
        ++ triedParts;
        partsIndices.remove(i);
        solution[partsAmount-leftParts] = partIndex;
    }

    protected void backTrack(int leftParts, int oldCellIndex, int i, int partIndex) {
        depth--;
        currPart = parts.getParts().get(partIndex);
        grid.setCurrCellIndex(oldCellIndex);
        grid.removeLast(currPart);
        partsIndices.add(i, partIndex);
        solution[partsAmount-leftParts] = 0; // partIndex
        
    }

    /** Deprecated - as verifyMiddle needs the keys */
    @Deprecated
    private void putKeys() {
        for (ICellPart cell: grid.getCells())
            if (cell.getcPartId() >= 'A' && cell.getcPartId() <= 'Z') { // cell.isEmptyPartId() is NOT good ?
                IPart part= parts.get((char)cell.getPartId());
//                if (part == null)
//                    part.getId();
                ICellPart pcell = part.getCells()[cell.getCellInPart()];
                cell.keysExists = pcell.keysExists;
                for (int i=0; i<pcell.keys.length; i++)
                    cell.keys[i] = pcell.keys[i];
            }
            else {
                ; //cell.keysExists = false; // no need as in IGrid.verify matches againt non occupied grid cells is not being checked
            }

    }

    @Override
    protected boolean preSolved() {
        lastSolution = "";
        for (int i=0; i<solution.length - partsInSolution; i++) {
            IPart part = parts.getParts().get(solution[i]);
            lastSolution += " " + (char)(part.getId()) + rotationChar(part.getRotationIndex());
        }
//        putKeys();
        return grid.verify();
    }


}
