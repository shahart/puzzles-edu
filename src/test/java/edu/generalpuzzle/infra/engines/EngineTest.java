package edu.generalpuzzle.infra.engines;

import edu.generalpuzzle.examples.cube.dimension2.Edge2D;
import edu.generalpuzzle.examples.cube.dimension2.Grid2D;
import edu.generalpuzzle.examples.cube.dimension2.Part2D;
import edu.generalpuzzle.infra.Parts;
import edu.generalpuzzle.infra.engines.trivial.TrivialRecursiveEngineStrategy;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * Date: 29/05/2008
 */
public class EngineTest {

//    public static void main(String s[]) {
//        new MyTestCase(EngineTest.class).runTests();
//    }

    private int factorial(int x) {
        if (x == 0)
            return 1;
        else
            return x * factorial(x-1);
    }

    @Test
    public void testBasicBackTracking() {

//        assertTrue(false);

        final int PARTS = 4;
        int partInSolution = 0;

        Grid2D grid = new Grid2D();
        grid.build2d(1,PARTS-partInSolution);

        Parts parts = new Parts('A');

        for (int i=0; i<PARTS; i++) {
            Part2D part = new Part2D((char)('A'+i)); // no real point to implement CellId1D, Grid1D
            part.setRotations(1);
            part.setReflective(0);
            part.prepareRotations(1);

            parts.add(part);
        }

        parts.setPartsInSolution(partInSolution);

        EngineStrategy engine = new TrivialRecursiveEngineStrategy(parts, grid);
//        EngineStrategy engine = new DlxEngineStrategy(parts, grid); // change splitsAmount to 1
        EngineStrategy.set_GENERATE_BY_ALL(true);
        EngineStrategy.set_FULL_OUTPUT(true);
        // engine.setRange(2, 4);
        engine.solve();

        assertEquals(factorial(PARTS), engine.getUniqueSolutions());
    }

    @Test
    public void testAdvancedBackTracking() {
    
        final int PIECES = 2;

        Grid2D grid = new Grid2D();
        grid.build2d(2,PIECES);

        Parts parts = new Parts();//'A'); // 'Z');
        Part2D part1, part2;

        for (int i=0; i<PIECES;i++) {
            part1 = new Part2D((char)('A'+i));
//            part1.anotherOne(i);
            part1.prepareRotations(2);
            part1.addEdge(1, Edge2D.DOWN,2);
            parts.add(part1);
        }
//        parts.anotherOne(PIECES);
        
        parts.complete(parts.getParts().get(0));
        parts.complete(parts.getParts().get(1));

//         part2 = new Part2D();
//         part2.prepareRotations(3);
//         part2.addEdge(1, Edge2D.RIGHT, 2);
//         part2.addEdge(1, Edge2D.DOWN, 3);
//         part2.setReferenceCellALL();
//
//         parts.add(part2);
//         parts.add(part1);

        EngineStrategy engine = new TrivialRecursiveEngineStrategy(parts, grid);
        EngineStrategy.set_GENERATE_BY_ALL(true);
        EngineStrategy.set_FULL_OUTPUT(true);
        engine.solve();

        assertEquals(4, engine.getUniqueSolutions());
    }

    @BeforeEach
    public void setUp() {
//        BasicConfigurator.configure();
    }

}
