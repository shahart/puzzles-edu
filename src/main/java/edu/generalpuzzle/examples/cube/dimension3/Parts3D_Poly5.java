package edu.generalpuzzle.examples.cube.dimension3;

import edu.generalpuzzle.examples.cube.Parts_Poly5;
import edu.generalpuzzle.infra.IGrid;
import edu.generalpuzzle.infra.IPart;
import edu.generalpuzzle.infra.Parts;

/**
 * Created by IntelliJ IDEA.
 * Date: 16/06/2008
 */
public class Parts3D_Poly5 extends Parts {

    public Parts3D_Poly5(int unique) {
        super(unique);
    }

    @Override
    public IPart getGridPart() {
        return new Part3D(IGrid.GRID_ID);
    }

    public void buildPoly5() {
        parts.clear();
        totalFill = 0;

//        add(Parts_Poly5.poly(new Part3D('A')));

        add(Parts_Poly5.poly(new Part3D('L')));
        add(Parts_Poly5.poly(new Part3D('U')));
        add(Parts_Poly5.poly(new Part3D('F')));
        add(Parts_Poly5.poly(new Part3D('X')));
        add(Parts_Poly5.poly(new Part3D('Y')));
        add(Parts_Poly5.poly(new Part3D('N')));
        add(Parts_Poly5.poly(new Part3D('W')));
        add(Parts_Poly5.poly(new Part3D('P')));
        add(Parts_Poly5.poly(new Part3D('Z')));
        add(Parts_Poly5.poly(new Part3D('V')));
        add(Parts_Poly5.poly(new Part3D('T')));
        add(Parts_Poly5.poly(new Part3D('I')));

    }

}
