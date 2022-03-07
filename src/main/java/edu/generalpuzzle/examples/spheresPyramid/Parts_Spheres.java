package edu.generalpuzzle.examples.spheresPyramid;

import edu.generalpuzzle.infra.IGrid;
import edu.generalpuzzle.infra.IPart;
import edu.generalpuzzle.infra.Parts;

/**
 * Created by IntelliJ IDEA.
 * Date: 16/06/2008
 */
public class Parts_Spheres extends Parts {

    // @SuppressWarnings({"SameParameterValue"})
    public Parts_Spheres(int unique) {
        super(unique);
    }

    @Override
    public IPart getGridPart() {
        return new PartSphere(IGrid.GRID_ID);
    }

    public void buildClassic() { // my print screen
        parts.clear();
        totalFill = 0;

        PartSphere part;

        part = new PartSphere('F');
        part.prepareRotations(3);
        part.addEdge(1, EdgeSphere.EAST, 2);
        part.addEdge(1, EdgeSphere.FRONT_EAST_30, 3); // TODO: catch something like 1, EAST_60, 2!! - two diff connections
        add(part);

        part = new PartSphere('E');
        part.prepareRotations(1);
        add(part);
    }

    public void buildAnotherClassic() { // http://www.3dpuzzles.nl/puzzles/ballspyramid/engdescription.htm
        parts.clear();
        totalFill = 0;

        PartSphere part;

//        part = new PartSphere('C'); // TODO- 18 sol with BT engine
//        part.prepareRotations(4);
//        part.addEdge(1, EdgeSphere.EAST_60, 2);
//        part.addEdge(1, EdgeSphere.EAST, 3);
//        part.addEdge(3, EdgeSphere.FRONT_EAST_150, 4);
//        add(part);
        

        part = new PartSphere('A');
        part.prepareRotations(4);
        part.addEdge(1, EdgeSphere.EAST_60, 2);
        part.addEdge(1, EdgeSphere.EAST, 3);
        part.addEdge(3, EdgeSphere.WEST_M60, 4);
        add(part);

        part = new PartSphere('B');
        part.prepareRotations(4);
        part.addEdge(1, EdgeSphere.FRONT_NORTH, 2);
        part.addEdge(1, EdgeSphere.EAST, 3);
        part.addEdge(2, EdgeSphere.WEST, 4);
        add(part);

        part = new PartSphere('C');
        part.prepareRotations(4);
        part.addEdge(1, EdgeSphere.FRONT_NORTH, 2);
        part.addEdge(2, EdgeSphere.EAST_60, 3);
        part.addEdge(3, EdgeSphere.BACK_EAST_30, 4);
        add(part);

        part = new PartSphere('D');
        part.prepareRotations(4);
        part.addEdge(1, EdgeSphere.FRONT_NORTH, 2);
        part.addEdge(2, EdgeSphere.FRONT_NORTH, 3);
        part.addEdge(3, EdgeSphere.EAST, 4);
//        part.addEdge(3, EdgeSphere.EAST, 3);
        add(part);

        part = new PartSphere('E');
        part.prepareRotations(3);
        part.addEdge(1, EdgeSphere.FRONT_NORTH, 2);
        part.addEdge(2, EdgeSphere.FRONT_NORTH, 3);
        add(part);

//        part = new PartSphere('F');
//        part.prepareRotations(2);
//        part.addEdge(1, EdgeSphere.EAST, 2);
//        add(part);
//
//        part = new PartSphere('D');
//        part.prepareRotations(2);
//        part.addEdge(1, EdgeSphere.FRONT_NORTH, 2);
//        add(part);

    }

    public void build805() { // pyramid puzzle # 805
        parts.clear();
        totalFill = 0;

        PartSphere part;

        for (int i=0; i<2; i++) {
            part = new PartSphere('I');
            part.anotherOne(i);
            part.prepareRotations(4);
            part.addEdge(1, EdgeSphere.BACK_EAST_30, 2);
            part.addEdge(2, EdgeSphere.BACK_EAST_30, 3);
            part.addEdge(3, EdgeSphere.BACK_EAST_30, 4);
            add(part);
        }
        anotherOne(2);

        for (int i=0; i<4; i++) {
            part = new PartSphere('J');
            part.anotherOne(i);
            part.prepareRotations(3);
            part.addEdge(1, EdgeSphere.EAST, 2);
            part.addEdge(2, EdgeSphere.EAST, 3);
            add(part);
        }
        anotherOne(4);
    }

//    public void buildGaya1() { // WADS example
//        parts.clear();
//        totalFill = 0;
//
//        PartSphere part;
//
//        part = new PartSphere('A');
//        part.prepareRotations(4);
//        part.addEdge(1, EdgeSphere.FRONT_NORTH, 2);
//        part.addEdge(2, EdgeSphere.FRONT_NORTH, 3);
//        part.addEdge(3, EdgeSphere.FRONT_NORTH, 4);
//        add(part);
//
//        part = new PartSphere('B');
//        part.prepareRotations(4);
//        part.addEdge(1, EdgeSphere.BACK_NORTH, 2);
//        part.addEdge(2, EdgeSphere.BACK_NORTH, 3);
//        part.addEdge(3, EdgeSphere.BACK_NORTH, 4);
//        add(part);
//
//        for (int i=0; i<2; i++) {
//            part = new PartSphere('C');
//            part.anotherOne(i);
//            part.prepareRotations(3);
//            part.addEdge(1, EdgeSphere.EAST, 2);
//            part.addEdge(2, EdgeSphere.EAST, 3);
//            add(part);
//        }
//        anotherOne(2);
//
//        for (int i=0; i<2; i++) {
//            part = new PartSphere('E');
//            part.anotherOne(i);
//            part.prepareRotations(3);
//            part.addEdge(1, EdgeSphere.BACK_NORTH, 2);
//            part.addEdge(2, EdgeSphere.BACK_NORTH, 3);
//            add(part);
//        }
//        anotherOne(2);
//
//    }

//    public void build795() { // pyramid puzzle # 795
//        parts.clear();
//        totalFill = 0;
//
//        PartSphere part;
//
//        part = new PartSphere('I');
//        part.prepareRotations(3);
//        part.addEdge(1, EdgeSphere.DOWN, 2);
//        part.addEdge(2, EdgeSphere.DOWN, 3);
//        add(part);
//
//        part = new PartSphere('V');
//        part.prepareRotations(3);
//
//        add(part);
//
//        // TODO
//    }


//    public void buildGolf() { // TODO http://local.wasp.uwa.edu.au/~pbourke/fun/golfpuzzle/
//        parts.clear();
//        totalFill = 0;
//
//        PartSphere part;
//
//        for (int i=0; i<3; i++) {
//            part = new PartSphere('I');
//            part.prepareRotations(4);
////            part.addEdge(1, EdgeSphere.WEST_M60, 2);
////            part.addEdge(2, EdgeSphere.WEST_M60, 3);
////            part.addEdge(3, EdgeSphere.EAST_60, 4);
//            add(part);
//        }
//
//        for (int i=0; i<2; i++) {
//            part = new PartSphere('J');
//            part.prepareRotations(3);
//            part.addEdge(1, EdgeSphere.DOWN, 2);
//            part.addEdge(2, EdgeSphere.DOWN, 3);
//            add(part);
//        }
////    }
//
}
