package edu.generalpuzzle.examples.cube.dimension3;

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
public class Part3D extends IPart {

    @Override
    public List<Integer> completeRotations(boolean notCheckDup) {
        List<Integer> toDel = new ArrayList<Integer>();
        rotationIndex = 0;
        for (int sym = 0; sym <reflective; sym++) {

            // the silly way

            if (sym == 1)
                    reflect(preparedRotations[rotationIndex]);


            for (int i=0; i<rotationCycle(new CellPart3D(new CellId(0)), CellPart3D.edge.XY.ordinal()) ; i++)
                for (int j=0; j<rotationCycle(new CellPart3D(new CellId(0)), CellPart3D.edge.XZ.ordinal()) ; j++)
                    for (int k=0; k<rotationCycle(new CellPart3D(new CellId(0)), CellPart3D.edge.YZ.ordinal()) ; k++) {

                        for (int ii=0; ii<i; ii++)
                            rotate(preparedRotations[rotationIndex], CellPart3D.edge.XY.ordinal());
                        for (int ij=0; ij<j; ij++)
                            rotate(preparedRotations[rotationIndex],  CellPart3D.edge.XZ.ordinal());
                        for (int ik=0; ik<k; ik++)
                            rotate(preparedRotations[rotationIndex], CellPart3D.edge.YZ.ordinal());

            // the smart way

//            for (int i=0; i<3; i++) {
////                if (sym == 1)
////                        reflect(preparedRotations[rotationIndex]);
//
//                //// 2D
//
//                // unique?
//                int sym2D_max = rotations == 3 ? 1 : 2;
//                //int sym2D_max = 2; //getId() == 5 ? 1 : 2;
//                int rot_max = rotations == 3 ? 1 : 4;
//                //int rot_max = getId() == 5 ? 1 : 4;
//
//                for (int sym2D = 0; sym2D<sym2D_max;sym2D++)
//                    for (int rot=0; rot< rot_max; rot++) {
//                        if (sym2D == 1)
//                            reflect(preparedRotations[rotationIndex]);
//                        for (int r=0; r<rot; r++)
//                            rotate(preparedRotations[rotationIndex], CellPart3D.edge.XY.ordinal());;
//                //// end 2D
//
//                        // put it on one of the 3 dimensions
//
//                        if (i==0)
//                            ;
//                        else if (i==1)
//                            rotate(preparedRotations[rotationIndex], CellPart3D.edge.XZ.ordinal());
//                        else if (i==2)
//                            rotate(preparedRotations[rotationIndex], CellPart3D.edge.YZ.ordinal());

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

    public Part3D() {
        super(new CellPart3D());
        setReflective(0);
        setRotations(new CellPart3D(new CellId(0))); // 4*4*4); // 3*(2*2*2)); // 3 dim * 2^3 dim
    }

    public Part3D(char id) {
        super(id, new CellPart3D());
        int reflective = 1; // id == IGrid.GRID_ID ? 2 : 1;
        if (! EngineStrategy.NO_ORIENTATIONS) {
            setReflective(reflective-1);
            setRotations(new CellPart3D(new CellId(0))); // 4*4*4); // 3*(2*2*2)); // 3 dim * 2^3 dim
//            setRotations(totalRotations * 2);
        }
        else {
            setReflective(0);
            setRotations(1);
        }
    }

    @Override
    protected ICellPart createCell(int id) {
        ICellPart cell = new CellPart3D(new CellId(id));
        cell.setPartId(getId());
        return cell;
    }



}
