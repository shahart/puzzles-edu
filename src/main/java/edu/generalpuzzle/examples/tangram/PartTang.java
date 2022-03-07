package edu.generalpuzzle.examples.tangram;

import edu.generalpuzzle.infra.CellId;
import edu.generalpuzzle.infra.ICellPart;
import edu.generalpuzzle.infra.IPart;
import edu.generalpuzzle.examples.tangram.CellPartTang;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * Date: 18/06/2008
 */
public class PartTang extends IPart {

    public PartTang() {
        super(new CellPartTang());
        setRotations(new CellPartTang(new CellId(0)));
        setReflective(1); 
    }

    public PartTang(char id) {
        super(id, new CellPartTang());
        setRotations(new CellPartTang(new CellId(0)));    // 6*.6*
        setReflective(1);
    }

    @Override
    protected ICellPart createCell(int id) {
        ICellPart cell = new CellPartTang(new CellId(id));
        cell.setPartId(getId());
        return cell;
    }

    @Override
    public List<Integer> completeRotations(boolean notCheckDup) {

        Set<Integer> directions = new HashSet<Integer>();
        for (ICellPart cell: cells)
            directions.add(((CellPartTang)cell).getSpecial());

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
                    if (! directions.contains(((CellPartTang)cell).getSpecial()))
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

    @Override
    public void markGridIds(int id, ICellPart cell) {

        if (cell.getId().getId() == Integer.MAX_VALUE) {
            cell.getId().setId(((CellPartTang)cell).getSpecial() + id + 1); // (id
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


}
