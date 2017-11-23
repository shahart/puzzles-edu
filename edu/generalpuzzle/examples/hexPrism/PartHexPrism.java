package edu.generalpuzzle.examples.hexPrism;

import edu.generalpuzzle.infra.CellId;
import edu.generalpuzzle.infra.ICellPart;
import edu.generalpuzzle.infra.IPart;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * Created by IntelliJ IDEA.
 * Date: 18/06/2008
 */
public class PartHexPrism extends IPart {

    public PartHexPrism() {
        super(new CellPartHexPrism());
        setRotations(new CellPartHexPrism(new CellId(0))); // TODO  *2 because the reflective?
        setReflective(0); // TODO?
    }

    public PartHexPrism(char id) {
        super(id, new CellPartHexPrism());
        setRotations(new CellPartHexPrism(new CellId(0)));    // 6*.6*
        setReflective(0);
    }

    @Override
    protected ICellPart createCell(int id) {
        ICellPart cell = new CellPartHexPrism(new CellId(id));
        cell.setPartId(getId());
        return cell;
    }

    @Override
    public List<Integer> completeRotations(boolean notCheckDup) {
        List<Integer> toDel = new ArrayList<Integer>();
        rotationIndex = 0;
        for (int sym = 0; sym<reflective;sym++) {

            if (sym == 1)
                reflect(preparedRotations[rotationIndex]);

            for (int i=0; i<rotationCycle(new CellPartHexPrism(new CellId(0)), CellPartHexPrism.edge.XY.ordinal()) ; i++) {
                for (int j=0; j<rotationCycle(new CellPartHexPrism(new CellId(0)), CellPartHexPrism.edge.XZ.ordinal()) ; j++) {


                        for (int ii=0; ii<i; ii++)
                            rotate(preparedRotations[rotationIndex], CellPartHexPrism.edge.XY.ordinal());
                        for (int ij=0; ij<j; ij++)
                            rotate(preparedRotations[rotationIndex],  CellPartHexPrism.edge.XZ.ordinal());

                            anchorIndices[rotationIndex] = computeAnchorIndex();

                            if (checkDuplicity(notCheckDup) == -1)
                                ++ rotationIndex;
                            else
                                toDel.add(rotationIndex);
                        }
            }
        }

        totalRotations = rotationIndex;
        rotationIndex = -1;
        return Collections.unmodifiableList(toDel);
    }

}
