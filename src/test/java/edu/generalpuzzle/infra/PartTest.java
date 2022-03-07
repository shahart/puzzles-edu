package edu.generalpuzzle.infra;

import edu.generalpuzzle.examples.cube.dimension2.Edge2D;
import edu.generalpuzzle.examples.cube.dimension2.Part2D;
import edu.generalpuzzle.examples.cube.dimension2.Parts2D_Poly5;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * Date: 29/05/2008
 */

public class PartTest {

//    public static void main(String s[]) {
//        new MyTestCase(PartTest.class).runTests();
//    }

    private Part2D part;

//    @Before
    public void setUp(int reflective) {

        part = new Part2D();
        part.setReflective(reflective);
        part.setRotations(4);
        part.prepareRotations(4-reflective);

        if (reflective == 0) {
            // X X X 1 2 4
            // X     3
            part.addEdge(1, Edge2D.RIGHT, 2);
            part.addEdge(1, Edge2D.DOWN, 3);
            part.addEdge(4, Edge2D.LEFT, 2);
        }
        else {
            // X X 1 2
            // X    3
            part.addEdge(1, Edge2D.RIGHT, 2);
            part.addEdge(1, Edge2D.DOWN, 3);
        }


        part.completeRotations(false);
    }

    @Test
    public void testPartsRotate() {
        Parts2D_Poly5 parts = new Parts2D_Poly5('Z');
        parts.buildPoly5();

        for (IPart part: parts.getParts()) {
            String origPart = part.toString();
            while (! part.rotate())
                ;
            assertEquals(part.toString(), origPart);
        }
    }

    @Test
    public void testRotate() {
        for (int i=0; i<2; i++) {
            setUp(i);
            String origPart = part.toString();
            while (! part.rotate())
                ;
            assertEquals(part.toString(), origPart);
        }
    }

    @Test
    public void testValid() {
        for (int i=0; i<2; i++) {
            setUp(i);
            for (ICellPart cell : part.getCells())
                for (ICellPart a: cell.getCell())
                    if (a != null)
                        ;//assertTrue( part.getCells().contains(a));
        }
    }

}
