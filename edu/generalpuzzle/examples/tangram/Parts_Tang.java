package edu.generalpuzzle.examples.tangram;

import edu.generalpuzzle.infra.IGrid;
import edu.generalpuzzle.infra.IPart;
import edu.generalpuzzle.infra.Parts;
import edu.generalpuzzle.infra.engines.EngineStrategy;

/**
 * Created by IntelliJ IDEA.
 * Date: 16/06/2008
 */
public class Parts_Tang extends Parts {

    // @SuppressWarnings({"SameParameterValue"})
    public Parts_Tang(int unique) {
        super(unique);
    }

    public Parts_Tang() {
        super();
    }


    @Override
    public IPart getGridPart() {
        return new PartTang(IGrid.GRID_ID);
    }
/*
    public void build() {  // build is not the real tangram (Pythagoras, from www.puzzleworld.org)
        parts.clear();
        totalFill = 0;

        PartTang part;

        part = new PartTang('A');
        part.prepareRotations(4);
        part.addEdge(1, EdgeTang.RIGHT_DOWN, 2);
        part.addEdge(2, EdgeTang.DOWN_LEFT, 3);
        part.addEdge(3, EdgeTang.LEFT_UP, 4);
        part.addEdge(4, EdgeTang.UP_RIGHT, 1);
        ((CellPartTang)part.getCells()[1]).direction = 1;
        ((CellPartTang)part.getCells()[2]).direction = 2;
        ((CellPartTang)part.getCells()[3]).direction = 3;
        add(part);

        for (int i=0; i<2; i++) {
            part = new PartTang('B');
            part.anotherOne(i);
            part.prepareRotations(1);
            add(part);
        }
        anotherOne(2);

        for (int i=0; i<2; i++) {
            part = new PartTang('D');
            part.anotherOne(i);
            part.prepareRotations(2);
            part.addEdge(1, EdgeTang.RIGHT_DOWN, 2);
            ((CellPartTang)part.getCells()[1]).direction = 1;
            add(part);
        }
        anotherOne(2);

        part = new PartTang('F');
        part.prepareRotations(2);
        part.addEdge(1, EdgeTang.DOWN, 2);
        ((CellPartTang)part.getCells()[0]).direction = 2;
        ((CellPartTang)part.getCells()[1]).direction = 0;
        add(part);

        part = new PartTang('G');
        part.prepareRotations(4);
        part.addEdge(1, EdgeTang.RIGHT_DOWN, 2);
        part.addEdge(2, EdgeTang.RIGHT, 3);
        part.addEdge(3, EdgeTang.RIGHT_DOWN, 4);
        ((CellPartTang)part.getCells()[1]).direction = 1;
        ((CellPartTang)part.getCells()[2]).direction = 3;
        ((CellPartTang)part.getCells()[3]).direction = 2;
        add(part);
    }
*/
    public void buildRealTangram() {
        parts.clear();
        totalFill = 0;

        PartTang part;

        for (int i=0; i<2; i++) { // green & blue
            part = new PartTang('A');
            part.anotherOne(i);
            part.prepareRotations(8);
            part.addEdge(1, EdgeTang.RIGHT, 2);
            part.addEdge(2, EdgeTang.RIGHT_DOWN, 3);
            part.addEdge(3, EdgeTang.DOWN, 4);
            part.addEdge(3, EdgeTang.RIGHT, 6);
            part.addEdge(4, EdgeTang.RIGHT, 5);
            part.addEdge(5, EdgeTang.UP, 6);
            part.addEdge(6, EdgeTang.UP_RIGHT, 7);
            part.addEdge(7, EdgeTang.RIGHT, 8);
            ((CellPartTang)part.getCells()[0]).setSpecial(7);
            ((CellPartTang)part.getCells()[1]).setSpecial(0);
            ((CellPartTang)part.getCells()[2]).setSpecial(1);
            ((CellPartTang)part.getCells()[3]).setSpecial (2);
            ((CellPartTang)part.getCells()[4]).setSpecial ( 5);
            ((CellPartTang)part.getCells()[5]).setSpecial ( 6);
            ((CellPartTang)part.getCells()[6]).setSpecial ( 7);
            ((CellPartTang)part.getCells()[7]).setSpecial ( 0);
            add(part);
        }
        anotherOne(2);

        for (int i=0; i<2; i++) { // pink & yellow
            part = new PartTang('C');
            part.anotherOne(i);
            part.prepareRotations(2);
            part.addEdge(1, EdgeTang.RIGHT, 2);
            ((CellPartTang)part.getCells()[0]).setSpecial(7);
            ((CellPartTang)part.getCells()[1]).setSpecial ( 0);
            add(part);
        }
        anotherOne(2);

        part = new PartTang('E'); // red
        part.prepareRotations(4);
        part.addEdge(1, EdgeTang.LEFT, 2);
        part.addEdge(2, EdgeTang.DOWN_LEFT, 3);
        part.addEdge(3, EdgeTang.DOWN, 4);
        ((CellPartTang)part.getCells()[1]).setSpecial(  7);
        ((CellPartTang)part.getCells()[2]).setSpecial(  6);
        ((CellPartTang)part.getCells()[3]).setSpecial(  5);
        add(part);

        part = new PartTang('F'); // purple
        part.prepareRotations(4);
        part.addEdge(1, EdgeTang.RIGHT, 4);
        part.addEdge(1, EdgeTang.DOWN, 2);
        part.addEdge(2, EdgeTang.RIGHT, 3);
        part.addEdge(3, EdgeTang.UP, 4);
        ((CellPartTang)part.getCells()[0]).setSpecial(  1);
        ((CellPartTang)part.getCells()[1]).setSpecial(  2);
        ((CellPartTang)part.getCells()[2]).setSpecial(  5);
        ((CellPartTang)part.getCells()[3]).setSpecial(  6);
        add(part);

        part = new PartTang('G'); // orange
        part.prepareRotations(4);
        part.addEdge(1, EdgeTang.DOWN, 2);
        part.addEdge(2, EdgeTang.DOWN_LEFT, 3);
        part.addEdge(3, EdgeTang.DOWN, 4);
        ((CellPartTang)part.getCells()[0]).setSpecial(  1);
        ((CellPartTang)part.getCells()[1]).setSpecial(  2);
        ((CellPartTang)part.getCells()[2]).setSpecial(  3);
        ((CellPartTang)part.getCells()[3]).setSpecial(  4);
        add(part);
    }


    @Override
    public void complete(IPart p) {

        for (int r=0; r<p.totalRotations; r++)
            for (int c=0; c<p.getCellsAmount(); c++)
                p.preparedRotations[r][c].setSpecial(p.getCells()[c].getSpecial());

        super.complete(p);

//        if (! EngineStrategy.GENERATE_BY_ALL)
//            if (p.getId() != uniqueId)
//                switch (p.getId()) {
//                    case 'F': p.totalRotations = 4; break; // p.preparedRotations[0] = p.preparedRotations[3];break;
//                    default:p.totalRotations = 4;
//                }
    }

}
