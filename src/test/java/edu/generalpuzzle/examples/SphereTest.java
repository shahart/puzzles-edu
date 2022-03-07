package edu.generalpuzzle.examples;

import edu.generalpuzzle.examples.spheresPyramid.EdgeSphere;
import edu.generalpuzzle.examples.spheresPyramid.PartSphere;
import edu.generalpuzzle.examples.cube.dimension3.happycube.Parts3D_HappyCube;
import edu.generalpuzzle.examples.cube.dimension3.happycube.GridHappyCube;
import edu.generalpuzzle.examples.hexPrism.GridHexaPyramid;
import edu.generalpuzzle.examples.hexPrism.PartHexPrism;
import edu.generalpuzzle.examples.hexPrism.HexagonalEdge;
import edu.generalpuzzle.infra.GraphIt;
import edu.generalpuzzle.infra.ICellPart;
import edu.generalpuzzle.infra.Parts;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * Date: 18/12/2008
 */
public class SphereTest {

//    public static void main(String s[]) {
//        new MyTestCase(SphereTest.class).runTests();
//    }

    @Test
    public void testConcreteGraphIt_rotates() {

        PartSphere part = new PartSphere('E');
        part.prepareRotations(3);
        part.addEdge(1, EdgeSphere.EAST, 2);
        part.addEdge(2, EdgeSphere.EAST_120, 3);

        Parts parts = new Parts(0);
        parts.add(part);
    }

    @Test
    public void testConcreteGraphIt_HappyCubeWithOnePart() {
        Parts3D_HappyCube parts = new Parts3D_HappyCube('A');
        parts.buildGreen();

        GridHappyCube grid = new GridHappyCube();
        grid.build();

        // patch in order to pass buildXml and validity of the x3d
        for (ICellPart a: grid.getCells())
            a.setPartId('E');

        GraphIt.setArgs("testConcreteGraphIt2");
        GraphIt graphIt = GraphIt.getInstance();

//        graphIt.graphIt(grid.getCells().get(0));
        graphIt.graphIt(parts.getParts().get(0).getCells()[0]);
        
        graphIt.buildXml("E"); // solution is the single part
        System.out.println("no asserts, used for visual edu.generalpuzzle.test with SwirlViewer.");

    }


    @Test
    public void testConcreteGraphIt_SphereWithOnePart() {

        PartHexPrism part = new PartHexPrism('E');
        part.prepareRotations(4);
        part.addEdge(1, HexagonalEdge.EAST_60, 2);
        part.addEdge(1, HexagonalEdge.WEST_M60, 3);
        part.addEdge(3, HexagonalEdge.BACK, 4);
        part.specialCell(1);

//        PartSphere part = new PartSphere('E');
//        part.prepareRotations(16);
//        part.addEdge(1, EdgeSphere.EAST, 2);
//        part.addEdge(2, EdgeSphere.EAST_120, 3);
//        part.addEdge(1, EdgeSphere.EAST_120, 4);
//        part.addEdge(1, EdgeSphere.WEST , 5);
//        part.addEdge(1, EdgeSphere.WEST_M120, 6);
//        part.addEdge(1, EdgeSphere.WEST_M60, 7);
//
//        part.addEdge(1, EdgeSphere.BACK_NORTH, 8);
//        part.addEdge(1, EdgeSphere.BACK_M150, 9);
//        part.addEdge(1, EdgeSphere.BACK_M30, 10);
//
//        part.addEdge(1, EdgeSphere.FRONT_SOUTH, 11);
//        part.addEdge(1, EdgeSphere.FRONT_EAST_30, 12);
//        part.addEdge(1, EdgeSphere.FRONT_EAST_150, 13);
//
//        part.addEdge(11, EdgeSphere.FRONT_NORTH, 14);
//        part.addEdge(3, EdgeSphere.EAST_120, 15);
//        part.addEdge(12, EdgeSphere.EAST_120, 16);

        Parts parts = new Parts(0);
        parts.add(part);

//        part.rotate(part.getCells(), 2);
//        part.rotate(part.getCells(), 1);
//        part.rotate(part.getCells(), 1);
//        part.rotate(part.getCells(), 1);

        GraphIt.setArgs("testConcreteGraphIt2");
        GraphIt graphIt = GraphIt.getInstance();
        graphIt.graphIt(part.getCells()[0]);
        graphIt.buildXml(Character.toString((char)part.getId())); // solution is the single part
        System.out.println("no asserts, used for visual edu.generalpuzzle.test with SwirlViewer.");
    }

    @Test
    public void testSphereGrid() {
        // Grid2DExamples grid = new Grid2DExamples(); // GridSpheresPyramid();
        // grid.buildCase8();

        GridHexaPyramid grid = new GridHexaPyramid();
        grid.buildPyramid(5,3);

        // patch in order to pass buildXml and validity of the x3d
        for (ICellPart a: grid.getCells())
            if (!a.isPlaceHolder())
                a.setPartId('A');

//         grid.show();

        GraphIt.setArgs("testConcreteGraphIt");
        GraphIt graphIt = GraphIt.getInstance();
        graphIt.graphIt(grid.getCells().get(0));
        graphIt.buildXml("A");
        System.out.println("no asserts, used for visual edu.generalpuzzle.test with SwirlViewer.");
    }


}
