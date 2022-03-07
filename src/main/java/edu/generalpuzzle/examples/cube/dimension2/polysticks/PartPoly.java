package edu.generalpuzzle.examples.cube.dimension2.polysticks;

import edu.generalpuzzle.infra.CellId;
import edu.generalpuzzle.infra.ICellPart;
import edu.generalpuzzle.infra.IPart;
import edu.generalpuzzle.infra.engines.EngineStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * Created by IntelliJ IDEA.
 * Date: 31/08/2010
 */
public class PartPoly extends IPart {
                              
    public PartPoly() {
        super(new CellPartPoly());
        setRotations(new CellPartPoly(new CellId(0)));   // 2*2
        setReflective(1); // turn on
    }

    /** creates Part2D with reflections, and preparing to future: NO ORIENTATIONS */
	public PartPoly(char id) {
        super(id, new CellPartPoly());
        if (! EngineStrategy.NO_ORIENTATIONS) {
            setRotations(new CellPartPoly(new CellId(0)));   // 2*2         // 2^2 dimensions
            setReflective(1); // turn on
        }
        else {  // NO_ORIENTATIONS
            setRotations(1); 
            setReflective(0);
        }

    }

    @Override
    protected ICellPart createCell(int id) {
        ICellPart cell = new CellPartPoly(new CellId(id));
        cell.setPartId(getId());
        return cell;
    }

    // notCheckDup- in case the curret part is the unique
    @Override
    public List<Integer> completeRotations(boolean notCheckDup) {
        List<Integer> toDel = new ArrayList<Integer>();
        rotationIndex = 0;
        for (int sym = 0; sym<reflective;sym++) {
            for (int rot=0; rot< rotationCycle(new CellPartPoly(new CellId(0)), CellPartPoly.edge.XY.ordinal()); rot++) {
                if (sym == 1)
                    reflect(preparedRotations[rotationIndex]);
                for (int r=0; r<rot; r++)
                    rotate(preparedRotations[rotationIndex], CellPartPoly.edge.XY.ordinal());

                anchorIndices[rotationIndex] = computeAnchorIndex();

                if (checkDuplicity(notCheckDup) == -1)
                    ++ rotationIndex;
                else
                    toDel.add(rotationIndex);
            }
        }
        totalRotations = rotationIndex;
        rotationIndex = -1;
        return Collections.unmodifiableList(toDel);
    }

    /*

    @Override
    public List<Integer> completeRotations(boolean notCheckDup) {

        Set<Integer> directions = new HashSet<Integer>();
        for (ICellPart cell: cells)
            directions.add(((CellPartTang)cell).direction);

        List<Integer> toDel = new ArrayList<Integer>();
        rotationIndex = 0;
        for (int sym = 0; sym<reflective;sym++) {

            if (sym == 1)
                reflect(preparedRotations[rotationIndex]);

            for (int i=0; i<rotationCycle(new CellPartTang(new CellId(0)), CellPartTang.edge.XY.ordinal()) ; i++) {


                for (int ii=0; ii<i; ii++)
                    rotate(preparedRotations[rotationIndex], CellPartTang.edge.XY.ordinal());

                anchorIndices[rotationIndex] = computeAnchorIndex();

                boolean dup = true;

                for (ICellPart cell : preparedRotations[rotationIndex])
                    if (! directions.contains(((CellPartTang)cell).direction))
                        dup = false;

                if (//! dup &&
                        checkDuplicity(notCheckDup) == -1)
                    ++ rotationIndex;
                else
                    toDel.add(rotationIndex);
            }
        }

        totalRotations = rotationIndex;
        rotationIndex = -1;
        return Collections.unmodifiableList(toDel);
    }

    public void markGridIds(int id, ICellPart cell) {

        if (cell.getId().getId() == Integer.MAX_VALUE) {
            cell.getId().setId(((CellPartTang)cell).direction + id + 1); // (id
            for (int edge=0; edge<cell.getCell().length; edge++)
                if (cell.getCell(edge)!=null) {
                    int newId = id;
                    newId += dummyCellPart.dummyEdge.mark(edge);
                    if (Math.abs(newId-id) < 10)
                        markGridIds(id, cell.getCell(edge));
                    else
                        markGridIds(newId, cell.getCell(edge));
                }
        }
    }

     */

}
