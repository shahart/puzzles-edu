package edu.generalpuzzle.test;

import edu.generalpuzzle.examples.cube.dimension3.Grid3D;
import edu.generalpuzzle.examples.cube.dimension3.Parts3D_Poly5;
import edu.generalpuzzle.infra.GraphIt;
import edu.generalpuzzle.infra.IPart;
import edu.generalpuzzle.infra.engines.EngineStrategy;
import edu.generalpuzzle.infra.engines.ParallelEngineStrategy;
//import junit.framework.TestCase;
import edu.generalpuzzle.main.Main;

import java.io.File;


/**
 * Created by IntelliJ IDEA.
 * Date: 16/12/2008
 */
public class BasicAcceptanceTest extends MyTestCase {


//    public static void main(String s[]) {
//        new MyTestCase(BasicAcceptanceTest.class).runTests();
//    }

    public void testEngines() {
        boolean error = false;
        for (int engineType=EngineStrategy.ENGINE_TYPE_DLX; engineType<=EngineStrategy.ENGINE_TYPE_ITERATIVE; engineType++)
            for (int testAll=0; testAll<=1; testAll++) {
                Grid3D grid = new Grid3D();
                grid.build3d(10, 2,3);
                Parts3D_Poly5 parts = new Parts3D_Poly5('F');
                parts.buildPoly5();
                for (IPart part: parts.getParts())
                    parts.complete(part);
                EngineStrategy.set_GENERATE_BY_ALL(testAll==1);
                EngineStrategy.set_AUTO_GRAPH_IT(false);
                ParallelEngineStrategy engine = Main.getConcreteEngine(parts, grid, engineType);
                engine.solve();
                int solutions = engine.getUniqueSolutions();
                System.out.println(solutions);
                System.out.println("\n************************************************************ engine:testAll " + engineType + testAll);
                if (solutions != 12 + (96-12) * testAll) {                                   // internal note
                    System.out.println("note: the check in EngineStrategy.solved for duplicates");
                    error = true;
                }
//                assertEquals(12 + (96-12) * testAll, solutions);
            }
        assertTrue(! error); //, false);
    }

    public void testMainCodeCoverage_3D() {
        // enable code coverage before
        Main main = new Main();
        GraphIt.setArgs("testMain");
        main.go(new String[] {"3d", "10", "2", "3"});
        System.out.println("no asserts, used for code coverage.");
    }

}
