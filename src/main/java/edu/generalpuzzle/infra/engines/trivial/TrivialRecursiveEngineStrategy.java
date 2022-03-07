package edu.generalpuzzle.infra.engines.trivial;

import edu.generalpuzzle.infra.IGrid;
import edu.generalpuzzle.infra.Parts;

/**
 * Created by IntelliJ IDEA.
 * Date: 03/09/2008
 */
public class TrivialRecursiveEngineStrategy extends TrivialEngineStrategy {

    public TrivialRecursiveEngineStrategy(Parts parts, IGrid grid) {
        super(parts,  grid);
    }

    @Override
    public void solve() {
        preSolve();
        putRecursive(partsAmount);
        postSolve();
    }

    public void putRecursive(int leftParts) {

        // if (toSave) { - can't, all data is on the stack

        if (inPause >= 1) pause();

        if (leftParts == partsInSolution) {
            solved();
        }

        int oldCellIndex;// = grid.getCurrCellIndex();
  /*
        if (EngineStrategy.S_HEURISTIC) {
//            System.out.println(grid.showBuf(implied[0]));
            int bestLoc = -1;
            int minimumCountPlacedPieces = Integer.MAX_VALUE;
            int cells = grid.getCells().size();

            int how=0;
            // on all cells
            for (int gridAtom=0; gridAtom<cells; gridAtom++) {

                // atom is use?
                if (grid.getCells().get(gridAtom).getPartId() != Integer.MAX_VALUE)
                    continue;

                // ! up - i-1 continue
                if (grid.getCells().get(gridAtom).getCell(Edge1D.LEFT) != null && grid.getCells().get(gridAtom).getCell(Edge1D.LEFT).getPartId() == Integer.MAX_VALUE)
                    continue;
                    
                // ! right - i-8 continue
                if (grid.getCells().get(gridAtom).getCell(Edge2D.UP) != null && grid.getCells().get(gridAtom).getCell(Edge2D.UP).getPartId() == Integer.MAX_VALUE)
                    continue;

                // i+1, i-7 ok

                // ! i-15 continue
//                how++;

                int countPlacedPieces = 0;
                // on all pieces

                for (int pieceToPlace = 0; pieceToPlace < partsAmount; ++pieceToPlace) {

                    // piece already in use
                    if (! partsIndices.contains(pieceToPlace))
                        continue;

                    currPart = parts.getParts().get(pieceToPlace);

                    ++ updates[partsAmount - leftParts]; // TODO is it right?

                    int origR = currPart.getRotationIndex();
                    for (int r2 = 0; r2< currPart.getTotalRotations() ; r2++) {

//                            ++ updates[partsAmount - leftParts]; // TODO is it right?

                        if (possibleRotations[pieceToPlace][r2][gridAtom] != null) {// process, r, gridCell
                            currPart.rotate(r2);

                            grid.setCurrCellIndex(gridAtom);
                            if (grid.newCanPut(currPart)) {

//                                ++branches[partsAmount - leftParts];
//                                ++totalNodes;
//                                ++ triedParts;

                                grid.removeLast(currPart);
                                ++ countPlacedPieces;

                                if (countPlacedPieces >= minimumCountPlacedPieces) {
                                    pieceToPlace = partsAmount;
                                    break;
                                }
                            }
                        }
                    }
                    currPart.rotate(origR);
                }
                if (countPlacedPieces < minimumCountPlacedPieces && countPlacedPieces > 0) {
                    bestLoc = gridAtom;
                    minimumCountPlacedPieces = countPlacedPieces;
                }
            }

            if (minimumCountPlacedPieces == Integer.MAX_VALUE) {
                grid.setCurrCellIndex(oldCellIndex);
                return;
            }

//            System.out.println(how);

            grid.setCurrCellIndex(bestLoc);
        }
*/
//        int
        oldCellIndex = grid.getCurrCellIndex();
        
        int i = (leftParts == partsAmount) ? fromPart : 0;

        for (; i< leftParts; i++) {

            int partIndex = partsIndices.get(i);
            currPart = parts.getParts().get(partIndex);

            if (leftParts == partsAmount && i == toPart)
                return;

            for (int r=0; r< currPart.getTotalRotations(); r++) {// while (! currPart.rotate()) {

                if (leftParts == partsAmount)
                    lastSolution = "" + (char)currPart.getId() + rotationChar(r); 

                currPart.rotate(r);

                if (! GENERATE_BY_ALL && currPart == unique) { //.getId() == unique.getId()) { // parts.getUnique()) {
                    int newLocation = implied[uniqueInImplied[r]][ (oldCellIndex) ];

                    if (newLocation < oldCellIndex)
                        // if (uniqsFound[oldCellIndex][r] != -1)
                        continue;
                }

                if (possibleRotations[partIndex][r][grid.getCurrCellIndex()] != null) {// process, r, gridCell // TODO 2d_poly


                    ++ updates[partsAmount - leftParts];
//                if (grid.anyPut(currPart)) {
//                    if (grid.newCanPut(currPart)) {
                        if (grid.new2CanPut(currPart, possibleRotations[partIndex][r][grid.getCurrCellIndex()])) { // TODO 2010
//                    if (grid.canPut(currPart)) { // TODO 2d_poly
                    

                        if (rollback(leftParts)) {

                            grid.removeLast(currPart);
                            grid.setCurrCellIndex(oldCellIndex);

                        }
                        else
                        {

                            if (trace) // TODO 2d_poly
                                grid.show(implied[0]);

                            track(leftParts, i, partIndex);
                            putRecursive(leftParts-1);
                            backTrack(leftParts, oldCellIndex, i, partIndex);
                        }
                    }
                }
            }
        }
    }


}
