package edu.generalpuzzle.examples.spheresPyramid;

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
public class PartSphere extends IPart {

    public PartSphere() {
        super(new CellPartSphere());
        setRotations(new CellPartSphere(new CellId(0)));;//setRotations(6); // new CellPart3D(new CellId(0))); // TODO  *2 because the reflective?
        setReflective(0); // TODO?
    }

    public PartSphere(char id) {
        super(id, new CellPartSphere());
        setRotations(new CellPartSphere(new CellId(0)));//setRotations(6); // new CellPartSphere(new CellId(0)));    // 6*.6*
        setReflective(0);
    }

    @Override
    protected ICellPart createCell(int id) {
        ICellPart cell = new CellPartSphere(new CellId(id));
        cell.setPartId(getId());
        return cell;
    }

    @Override
    public List<Integer> completeRotations(boolean notCheckDup) {
        List<Integer> toDel = new ArrayList<Integer>();
        rotationIndex = 0;
        for (int sym = 0; sym<reflective;sym++) {

            for (int i=0; i<rotationCycle(new CellPartSphere(new CellId(0)), CellPartSphere.edge.XY.ordinal()) ; i++) {
                for (int j=0; j<rotationCycle(new CellPartSphere(new CellId(0)), CellPartSphere.edge.N60.ordinal()) ; j++) {
                    for (int k=0; k<rotationCycle(new CellPartSphere(new CellId(0)), CellPartSphere.edge.X120.ordinal()) ; k++) {
                        for (int l=0; l<rotationCycle(new CellPartSphere(new CellId(0)), CellPartSphere.edge.FM150M30.ordinal()) ; l++) {
//                            for (int m=0; m<rotationCycle(new CellPartSphere(new CellId(0)), CellPartSphere.edge.S60.ordinal()) ; m++) {
    //
                            if (sym == 1)
                                reflect(preparedRotations[rotationIndex]);

                            for (int ii=0; ii<i; ii++)
                                rotate(preparedRotations[rotationIndex], CellPartSphere.edge.XY.ordinal());
                            for (int ij=0; ij<j; ij++)
                                rotate(preparedRotations[rotationIndex],  CellPartSphere.edge.N60.ordinal());
                            for (int ik=0; ik<k; ik++)
                                rotate(preparedRotations[rotationIndex], CellPartSphere.edge.X120.ordinal());
                            for (int il=0; il<l; il++)
                                rotate(preparedRotations[rotationIndex], CellPartSphere.edge.FM150M30.ordinal());
//                            for (int im=0; im<m; im++)
//                                rotate(preparedRotations[rotationIndex], CellPartSphere.edge.S60.ordinal());
    //
                            anchorIndices[rotationIndex] = computeAnchorIndex();

                            if (checkDuplicity(notCheckDup) == -1)
                                ++ rotationIndex;
                            else
                                toDel.add(rotationIndex);
//                            }
                        }
                    }
                }
            }
        }

        totalRotations = rotationIndex;
        rotationIndex = -1;
        return Collections.unmodifiableList(toDel); // TODO?
    }

}
