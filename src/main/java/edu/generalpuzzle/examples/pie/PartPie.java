package edu.generalpuzzle.examples.pie;

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
public class PartPie extends IPart {

    public PartPie() {
        super(new CellPartPie());
        setRotations(new CellPartPie(new CellId(0)));
        setReflective(1);
    }

    public PartPie(char id) {
        super(id, new CellPartPie());
        setRotations(new CellPartPie(new CellId(0)));
        setReflective(1);
    }

    @Override
    protected int rotationCycle(ICellPart a, int r) {
        // super.rotationCylce is not good as this part is being rotated by diff' way
        return (r == CellPartPie.edge.REF.ordinal()) ? 2 : 4;
    }

    /** rotate, each cell independently */
    @Override
	public void rotate(ICellPart cells[], int r) {
        if (r == CellPartPie.edge.REF.ordinal())
            reflect();
        else {
            int keys[] = cells[0].keys;
            boolean keysExist = cells[0].keysExists;
            for (int i=0; i<cells.length-1; i++) {
                cells[i].keys = cells[i+1].keys;
                cells[i].keysExists = cells[i+1].keysExists;
            }
            cells[cells.length-1].keys = keys;
            cells[cells.length-1].keysExists = keysExist;
        }
    }

    /** rotate, each cell independently */
    @Override
	public void reflect(ICellPart cells[]) {
        int keys[] = cells[1].keys;
        boolean keysExist = cells[1].keysExists;
        cells[1].keysExists = cells[3].keysExists;
        cells[1].keys = cells[3].keys;
        cells[3].keysExists = keysExist;
        cells[3].keys = keys;
        for (ICellPart cell: cells)
            cell.rotate(0);
    }

    @Override
    protected ICellPart createCell(int id) {
        ICellPart cell = new CellPartPie(new CellId(id));
        cell.setPartId(getId());
        return cell;
    }

    @Override
    public List<Integer> completeRotations(boolean notCheckDup) {
        List<Integer> toDel = new ArrayList<Integer>();
        rotationIndex = 0;
        for (int sym = 0; sym<reflective;sym++) {

            for (int i=0; i<rotationCycle(new CellPartPie(new CellId(0)), CellPartPie.edge.CW.ordinal()) ; i++) {

                if (sym == 1)
                    reflect(preparedRotations[rotationIndex]);

                        for (int ii=0; ii<i; ii++)
                            rotate(preparedRotations[rotationIndex], CellPartPie.edge.CW.ordinal());

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
