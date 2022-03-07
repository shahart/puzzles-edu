package edu.generalpuzzle.examples.cube.dimension2;

import edu.generalpuzzle.infra.IPart;
import edu.generalpuzzle.infra.IGrid;
import edu.generalpuzzle.infra.Parts;
import edu.generalpuzzle.examples.cube.Parts_Poly5;

/**
 * Created by IntelliJ IDEA.
 * Date: 16/04/2009
 */
public class Parts2D_Examples extends Parts {

    public Parts2D_Examples(int unique) {
        super(unique);
    }

    public Parts2D_Examples() {
        super();
    }

    @Override
    public IPart getGridPart() {
        return new Part2D(IGrid.GRID_ID);
    }

    public void build_Star() { // grid 4x4

        parts.clear();
        totalFill = 0;

        Part2D part;

        part = new Part2D('A');
        part.prepareRotations(1);
        part.putKey(1, Edge2D.RIGHT, 1);
        part.putKey(1, Edge2D.DOWN, 1);
        part.putKey(1, Edge2D.LEFT, 1); // outer boundary
        part.putKey(1, Edge2D.UP, 1); //
        add(part);

        part = new Part2D('B');
        part.prepareRotations(1);
        part.putKey(1, Edge2D.DOWN, 1);
        part.putKey(1, Edge2D.LEFT, -1);
        part.putKey(1, Edge2D.UP, 1); //
        add(part);

        part = new Part2D('C');
        part.prepareRotations(1);
        part.putKey(1, Edge2D.DOWN, -1);
        part.putKey(1, Edge2D.UP, 1); //
        add(part);

        part = new Part2D('D');
        part.prepareRotations(1);
        part.putKey(1, Edge2D.RIGHT, 1); //
        part.putKey(1, Edge2D.UP, 1); //
        add(part);

        part = new Part2D('E');
        part.prepareRotations(1);
        part.putKey(1, Edge2D.RIGHT, -1);
        part.putKey(1, Edge2D.DOWN, -1);
        part.putKey(1, Edge2D.LEFT, 1); //
        part.putKey(1, Edge2D.UP, -1);
        add(part);

        part = new Part2D('F');
        part.prepareRotations(1);
        part.putKey(1, Edge2D.RIGHT, 1);
        part.putKey(1, Edge2D.DOWN, -1);
        part.putKey(1, Edge2D.LEFT, 1);
        part.putKey(1, Edge2D.UP, -1);
        add(part);

        part = new Part2D('G');
        part.prepareRotations(1);
        part.putKey(1, Edge2D.DOWN, -1);
        part.putKey(1, Edge2D.LEFT, -1);
        part.putKey(1, Edge2D.UP, 1);
        add(part);

        part = new Part2D('H');
        part.prepareRotations(1);
        part.putKey(1, Edge2D.RIGHT, 1); //
        add(part);

        part = new Part2D('I');
        part.prepareRotations(1);
        part.putKey(1, Edge2D.DOWN, -1);
        part.putKey(1, Edge2D.LEFT, 1); //
        part.putKey(1, Edge2D.UP, 1);
        add(part);

        part = new Part2D('J');
        part.prepareRotations(1);
        part.putKey(1, Edge2D.RIGHT, -1);
        part.putKey(1, Edge2D.UP, 1);
        add(part);

        part = new Part2D('K');
        part.prepareRotations(1);
        part.putKey(1, Edge2D.RIGHT, -1);
        part.putKey(1, Edge2D.DOWN, -1);
        part.putKey(1, Edge2D.LEFT, 1);
        part.putKey(1, Edge2D.UP, 1);
        add(part);

        part = new Part2D('L');
        part.prepareRotations(1);
        part.putKey(1, Edge2D.RIGHT, 1); //
        part.putKey(1, Edge2D.LEFT, 1);
        add(part);

        part = new Part2D('M');
        part.prepareRotations(1);
        part.putKey(1, Edge2D.RIGHT, 1);
        part.putKey(1, Edge2D.DOWN, 1); //
        part.putKey(1, Edge2D.LEFT, 1); //
        part.putKey(1, Edge2D.UP, 1);
        add(part);

        part = new Part2D('N');
        part.prepareRotations(1);
        part.putKey(1, Edge2D.RIGHT, -1);
        part.putKey(1, Edge2D.DOWN, 1);
        part.putKey(1, Edge2D.LEFT, -1);
        add(part);

        part = new Part2D('O');
        part.prepareRotations(1);
        part.putKey(1, Edge2D.DOWN, 1); //
        part.putKey(1, Edge2D.RIGHT, -1);
        part.putKey(1, Edge2D.LEFT, 1); 
        part.putKey(1, Edge2D.UP, 1); //
        add(part);

        part = new Part2D('P');
        part.prepareRotations(1);
        part.putKey(1, Edge2D.DOWN, 1);
        part.putKey(1, Edge2D.RIGHT, 1); //
        part.putKey(1, Edge2D.LEFT, 1);
        add(part);

        /// the frame

        part = new Part2D('Q');
        part.prepareRotations(20);

        part.addEdge(1, Edge2D.RIGHT, 2);
        part.addEdge(2, Edge2D.RIGHT, 3);
        part.addEdge(3, Edge2D.RIGHT, 4);
        part.addEdge(4, Edge2D.RIGHT, 5);
        part.addEdge(5, Edge2D.RIGHT, 6);

        part.addEdge(6, Edge2D.DOWN, 7);
        part.addEdge(7, Edge2D.DOWN, 8);
        part.addEdge(8, Edge2D.DOWN, 9);
        part.addEdge(9, Edge2D.DOWN, 10);
        part.addEdge(10, Edge2D.DOWN, 11);
        
        part.addEdge(11, Edge2D.LEFT, 12);
        part.addEdge(12, Edge2D.LEFT, 13);
        part.addEdge(13, Edge2D.LEFT, 14);
        part.addEdge(14, Edge2D.LEFT, 15);
        part.addEdge(15, Edge2D.LEFT, 16);

        part.addEdge(16, Edge2D.UP, 17);
        part.addEdge(17, Edge2D.UP, 18);
        part.addEdge(18, Edge2D.UP, 19);
        part.addEdge(19, Edge2D.UP, 20);
        part.addEdge(20, Edge2D.UP, 1);

        part.putKey(2, Edge2D.DOWN, -1);
        part.putKey(3, Edge2D.DOWN, -1);
        part.putKey(4, Edge2D.DOWN, -1);
        part.putKey(5, Edge2D.DOWN, -1);

        part.putKey(7, Edge2D.LEFT, -1);
        part.putKey(8, Edge2D.LEFT, -1);
        part.putKey(9, Edge2D.LEFT, -1);
        part.putKey(10, Edge2D.LEFT, -1);

        part.putKey(12, Edge2D.UP, -1);
        part.putKey(13, Edge2D.UP, -1);
        part.putKey(14, Edge2D.UP, -1);
        part.putKey(15, Edge2D.UP, -1);

        part.putKey(17, Edge2D.RIGHT, -1);
        part.putKey(18, Edge2D.RIGHT, -1);
        part.putKey(19, Edge2D.RIGHT, -1);
        part.putKey(20, Edge2D.RIGHT, -1);

        add(part);
    }

    public void build_Checkers() {

        parts.clear();
        totalFill = 0;

        Part2D part = new Part2D('A');
        part.prepareRotations(4);
        part.addEdge(1, Edge2D.RIGHT, 2);
        part.addEdge(3, Edge2D.RIGHT, 4);
        part.addEdge(1, Edge2D.DOWN, 3);
        part.addEdge(2, Edge2D.DOWN, 4);
        part.specialCell(1);
        part.specialCell(4);
        add(part);

        part = new Part2D('B');
        part.prepareRotations(4);
        part.addEdge(1, Edge2D.RIGHT, 2);
        part.addEdge(2, Edge2D.UP, 3);
        part.addEdge(2, Edge2D.DOWN, 4);
        part.specialCell(1);
        part.specialCell(4);
        part.specialCell(3);
        add(part);

        part = new Part2D('C');
        part.prepareRotations(3);
        part.addEdge(1, Edge2D.RIGHT, 2);
        part.addEdge(1, Edge2D.DOWN, 3);
        part.specialCell(1);
        add(part);

        part = (Part2D)Parts_Poly5.poly(new Part2D('N'));
        part.specialCell(1);
        part.specialCell(2);
        part.specialCell(5);
        add(part);

        part = (Part2D)Parts_Poly5.poly(new Part2D('T'));
        part.specialCell(1);
        part.specialCell(3);
        add(part);
        
        part = new Part2D('L');
        part.prepareRotations(4);
        part.addEdge(1, Edge2D.RIGHT, 2);
        part.addEdge(2, Edge2D.RIGHT, 3);
        part.addEdge(1, Edge2D.DOWN, 4);
        part.specialCell(1);
        part.specialCell(3);
        add(part);

    }
}
