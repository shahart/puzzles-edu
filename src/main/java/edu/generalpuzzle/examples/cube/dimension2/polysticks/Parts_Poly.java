package edu.generalpuzzle.examples.cube.dimension2.polysticks;

import edu.generalpuzzle.examples.cube.dimension2.polysticks.PartPoly;
import edu.generalpuzzle.infra.IGrid;
import edu.generalpuzzle.infra.IPart;
import edu.generalpuzzle.infra.Parts;
import edu.generalpuzzle.infra.engines.EngineStrategy;

/**
 * Created by IntelliJ IDEA.
 * Date: 16/06/2008
 */
public class Parts_Poly extends Parts {

    // @SuppressWarnings({"SameParameterValue"})
    public Parts_Poly(int unique) {
        super(unique);
    }

    public Parts_Poly() {
        super();
    }


    @Override
    public IPart getGridPart() {
        return new PartPoly(IGrid.GRID_ID);
    }

    public void buildTest() {
        parts.clear();
        totalFill = 0;

        PartPoly part;

            part = new PartPoly('A');
            part.prepareRotations(3);
            part.addEdge(1, EdgePoly.RIGHT, 2);
        part.addEdge(1, EdgePoly.DOWN, 3);
        part.specialCell(1);
//            ((CellPartPoly)part.getCells()[0]).direction = 7;
            add(part);

        part = new PartPoly('B');
        part.prepareRotations(1);
    part.specialCell(1);
//            ((CellPartPoly)part.getCells()[0]).direction = 7;
        add(part);
        }

    public void build72() { // no H
        parts.clear();
        totalFill = 0;

        // F H I J N O P R S U V W X Y Z L

        PartPoly part;

        part = new PartPoly('A'); // pink
        part.prepareRotations(4);
        part.addEdge(1, EdgePoly.DOWN, 2);
        part.addEdge(2, EdgePoly.RIGHT_DOWN, 3);
        part.addEdge(3, EdgePoly.RIGHT, 4);
        part.specialCell(1);    // ///
        part.specialCell(4);
        add(part);

        part = new PartPoly('B'); // cyan
        part.prepareRotations(4);
        part.addEdge(1, EdgePoly.UP_RIGHT, 2);
    part.addEdge(2, EdgePoly.RIGHT, 3);
        part.addEdge(2, EdgePoly.UP_RIGHT, 4); // must?
    part.addEdge(3, EdgePoly.UP, 4);
    part.specialCell(1);
    part.specialCell(2);
        part.specialCell(4);
        add(part);

        part = new PartPoly('C'); // suqare
        part.prepareRotations(4);
        part.addEdge(1, EdgePoly.RIGHT, 2);
    part.addEdge(2, EdgePoly.DOWN, 3);
    part.addEdge(3, EdgePoly.LEFT, 4);
        part.addEdge(2, EdgePoly.DOWN_LEFT, 4);
        part.addEdge(1, EdgePoly.RIGHT_DOWN, 3);
    part.specialCell(1);
    part.specialCell(3);
        add(part);

        part = new PartPoly('D'); // blue
        part.prepareRotations(4);
        part.addEdge(1, EdgePoly.RIGHT_DOWN, 2);
    part.addEdge(1, EdgePoly.DOWN, 3);
    part.addEdge(3, EdgePoly.RIGHT, 2);
        part.addEdge(3, EdgePoly.DOWN_LEFT, 4);
    part.specialCell(3);
    part.specialCell(4);
        add(part);

//        set uniqu

        part = new PartPoly('E'); // orange
        part.prepareRotations(4);
        part.addEdge(1, EdgePoly.RIGHT, 2);
    part.addEdge(2, EdgePoly.RIGHT, 3);
    part.addEdge(3, EdgePoly.RIGHT_DOWN, 4);
    part.specialCell(2);
        add(part);

        part = new PartPoly('F'); // black
        part.prepareRotations(4);
        part.addEdge(1, EdgePoly.DOWN_LEFT, 2);
    part.addEdge(2, EdgePoly.DOWN_LEFT, 3);
    part.addEdge(3, EdgePoly.DOWN_LEFT, 4);
    part.specialCell(1);
    part.specialCell(4);
        part.specialCell(2);
        part.specialCell(3);
        add(part);

        part = new PartPoly('G'); // pink
        part.prepareRotations(4);
        part.addEdge(1, EdgePoly.DOWN, 2);
    part.addEdge(2, EdgePoly.LEFT, 3);
    part.addEdge(3, EdgePoly.DOWN, 4);
        part.addEdge(1, EdgePoly.DOWN_LEFT, 3);
        part.addEdge(2, EdgePoly.DOWN_LEFT, 4);
    part.specialCell(1);
    part.specialCell(3);
        add(part);

        part = new PartPoly('H'); // gray
        part.prepareRotations(4);
        part.addEdge(1, EdgePoly.LEFT, 2);
        part.addEdge(2, EdgePoly.LEFT_UP, 3);
        part.addEdge(3, EdgePoly.LEFT_UP, 4);
        part.specialCell(1);
        add(part);

        part = new PartPoly('I'); // red
        part.prepareRotations(4);
        part.addEdge(1, EdgePoly.DOWN, 2);
        part.addEdge(2, EdgePoly.RIGHT, 3);
        part.addEdge(3, EdgePoly.UP, 4);
        part.addEdge(1, EdgePoly.RIGHT_DOWN, 3);
        part.addEdge(2, EdgePoly.UP_RIGHT, 4);
        part.specialCell(2);
        part.specialCell(4);
        add(part);


        part = new PartPoly('J'); // cyan
        part.prepareRotations(4);
        part.addEdge(1, EdgePoly.UP_RIGHT, 2);
        part.addEdge(2, EdgePoly.RIGHT, 3);
        part.addEdge(3, EdgePoly.RIGHT_DOWN, 4);
        part.specialCell(1);
        part.specialCell(2);
        add(part);

        part = new PartPoly('K'); // purple
        part.prepareRotations(4);
        part.addEdge(1, EdgePoly.RIGHT, 2);
        part.addEdge(2, EdgePoly.RIGHT, 3);
        part.addEdge(3, EdgePoly.DOWN, 4);
        part.addEdge(2, EdgePoly.RIGHT_DOWN, 4);
        part.specialCell(1);
        part.specialCell(3);
        add(part);

        part = new PartPoly('L'); // light gray
        part.prepareRotations(4);
        part.addEdge(1, EdgePoly.RIGHT, 2);
        part.addEdge(2, EdgePoly.UP, 3);
        part.addEdge(1, EdgePoly.UP_RIGHT, 3);
        part.addEdge(3, EdgePoly.LEFT_UP, 4);
        part.specialCell(2);
        add(part);

        part = new PartPoly('M'); // yellow
        part.prepareRotations(4);
        part.addEdge(1, EdgePoly.RIGHT, 2);
        part.addEdge(2, EdgePoly.RIGHT, 3);
        part.addEdge(3, EdgePoly.RIGHT, 4);
        part.specialCell(2);
        part.specialCell(4);
        add(part);

        part = new PartPoly('N'); // green
        part.prepareRotations(4);
        part.addEdge(1, EdgePoly.RIGHT, 2);
        part.addEdge(2, EdgePoly.UP_RIGHT, 3);
        part.addEdge(3, EdgePoly.RIGHT, 4);
        part.specialCell(2);
        part.specialCell(3);
        add(part);

        part = new PartPoly('O'); // brown
        part.prepareRotations(4);
        part.addEdge(1, EdgePoly.UP, 2);
        part.addEdge(2, EdgePoly.UP, 3);
        part.addEdge(3, EdgePoly.RIGHT, 4);
        part.addEdge(2, EdgePoly.UP_RIGHT, 4);
        part.specialCell(1);
        part.specialCell(3);
        add(part);
        }


/*
    @Override
    public void complete(IPart p) {

        for (int r=0; r<p.totalRotations; r++)
            for (int c=0; c<p.getCellsAmount(); c++)
                ;//((CellPartPoly)p.preparedRotations[r][c]).direction = ((CellPartPoly)p.getCells()[c]).direction;

        super.complete(p);

//        if (! EngineStrategy.GENERATE_BY_ALL)
//            if (p.getId() != uniqueId)
//                switch (p.getId()) {
//                    case 'F': p.totalRotations = 4; break; // p.preparedRotations[0] = p.preparedRotations[3];break;
//                    default:p.totalRotations = 4;
//                }
    }
*/
}
