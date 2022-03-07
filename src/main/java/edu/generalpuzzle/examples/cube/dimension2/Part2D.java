package edu.generalpuzzle.examples.cube.dimension2;

import edu.generalpuzzle.infra.CellId;
import edu.generalpuzzle.infra.ICellPart;
import edu.generalpuzzle.infra.IPart;
import edu.generalpuzzle.infra.engines.EngineStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * Created by IntelliJ IDEA.
 * Date: 18/06/2008
 */
public class Part2D extends IPart {
                              
    public Part2D() {
        super(new CellPart2D());
        setRotations(new CellPart2D(new CellId(0)));   // 2*2
        setReflective(1); // turn on
    }

    /** creates Part2D with reflections, and preparing to future: NO ORIENTATIONS */
	public Part2D(char id) {
        super(id, new CellPart2D());
        if (! EngineStrategy.NO_ORIENTATIONS) {
            setRotations(new CellPart2D(new CellId(0)));   // 2*2         // 2^2 dimensions
            setReflective(1); // turn on
        }
        else {  // NO_ORIENTATIONS
            setRotations(1); 
            setReflective(0);
        }

    }

    @Override
    protected ICellPart createCell(int id) {
        ICellPart cell = new CellPart2D(new CellId(id));
        cell.setPartId(getId());
        return cell;
    }

    // notCheckDup- in case the curret part is the unique
    @Override
    public List<Integer> completeRotations(boolean notCheckDup) {
        List<Integer> toDel = new ArrayList<Integer>();
        rotationIndex = 0;
        for (int sym = 0; sym<reflective;sym++) {
            for (int rot=0; rot< rotationCycle(new CellPart2D(new CellId(0)), CellPart2D.edge.XY.ordinal()); rot++) {
                if (sym == 1)
                    reflect(preparedRotations[rotationIndex]);
                for (int r=0; r<rot; r++)
                    rotate(preparedRotations[rotationIndex], CellPart2D.edge.XY.ordinal());

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


}
