package edu.generalpuzzle.examples.cube.dimension2;

import edu.generalpuzzle.examples.cube.Parts_Poly5;
import edu.generalpuzzle.infra.IGrid;
import edu.generalpuzzle.infra.IPart;
import edu.generalpuzzle.infra.Parts;

/** build set of 2D poliominoes 
 *
 * Created by IntelliJ IDEA.
 * Date: 16/06/2008
 */
public class Parts2D_Poly5 extends Parts {

    public Parts2D_Poly5(int unique) {
        super(unique);
    }
    
    @Override
    public IPart getGridPart() {
        return new Part2D(IGrid.GRID_ID);
    }

    public void buildPoly5() {
        parts.clear();
        totalFill = 0;

        // add(Parts_Poly5.poly(new Part2D('A')));
        // add(Parts_Poly5.poly(new Part2D('a'))); // for edu.generalpuzzle.test

        add(Parts_Poly5.poly(new Part2D('U')));
        add(Parts_Poly5.poly(new Part2D('L')));
        add(Parts_Poly5.poly(new Part2D('F')));
        add(Parts_Poly5.poly(new Part2D('X')));
        add(Parts_Poly5.poly(new Part2D('Y')));
        add(Parts_Poly5.poly(new Part2D('N')));
        add(Parts_Poly5.poly(new Part2D('W')));
        add(Parts_Poly5.poly(new Part2D('P')));
        add(Parts_Poly5.poly(new Part2D('Z')));
        add(Parts_Poly5.poly(new Part2D('V')));
        add(Parts_Poly5.poly(new Part2D('T')));
        add(Parts_Poly5.poly(new Part2D('I')));
    }

}
