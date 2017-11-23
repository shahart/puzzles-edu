package edu.generalpuzzle.examples.hexPrism;

import edu.generalpuzzle.infra.IGrid;
import edu.generalpuzzle.infra.IPart;
import edu.generalpuzzle.infra.Parts;

/**
 * Created by IntelliJ IDEA.
 * Date: 16/06/2008
 */
public class Parts_HexPrism extends Parts {

    // @SuppressWarnings({"SameParameterValue"})
    public Parts_HexPrism(int unique) {
        super(unique);
    }

    @Override
    public IPart getGridPart() {
        return new PartHexPrism(IGrid.GRID_ID);
    }

    public void buildTest() {
        parts.clear();
        totalFill = 0;

        PartHexPrism part;

        part = new PartHexPrism('F');
        part.prepareRotations(2);
        part.addEdge(1, HexagonalEdge.EAST, 2);
        add(part);

        part = new PartHexPrism('E');
        part.prepareRotations(1);
        add(part);

        part = new PartHexPrism('G');
        part.prepareRotations(3);
        part.addEdge(1, HexagonalEdge.FRONT, 2);
        part.addEdge(1, HexagonalEdge.WEST_M60, 3);

        add(part);
    }

    public void buildClassic() { // my print screen, Gaya
        parts.clear();
        totalFill = 0;

        PartHexPrism part;

        part = new PartHexPrism('A');
        part.prepareRotations(4);
        part.specialCell(3);
        part.addEdge(1, HexagonalEdge.EAST, 2);
        part.addEdge(1, HexagonalEdge.WEST, 3);
        part.addEdge(1, HexagonalEdge.BACK, 4);
        add(part);

        part = new PartHexPrism('B');
        part.prepareRotations(5);
        part.specialCell(1);
        part.specialCell(4);
        part.addEdge(1, HexagonalEdge.WEST, 2);
        part.addEdge(1, HexagonalEdge.EAST_120, 3);
        part.addEdge(3, HexagonalEdge.WEST, 4);
        part.addEdge(2, HexagonalEdge.BACK, 5);
        add(part);

        part = new PartHexPrism('C');
        part.prepareRotations(4);
        part.specialCell(2);
        part.specialCell(4);
        part.specialCell(3);
        part.addEdge(1, HexagonalEdge.EAST, 2);
        part.addEdge(2, HexagonalEdge.EAST_60, 3);
        part.addEdge(2, HexagonalEdge.BACK, 4);
        // part.addEdge(4, HexagonalEdge.EAST_60, 5); // error in Gaya site
        add(part);

        part = new PartHexPrism('D');
        part.prepareRotations(4);
        part.addEdge(1, HexagonalEdge.EAST_120, 2);
        part.addEdge(2, HexagonalEdge.BACK, 3);
        part.addEdge(3, HexagonalEdge.WEST, 4);
        add(part);

        part = new PartHexPrism('E');
        part.prepareRotations(4);
        part.specialCell(3);
        part.specialCell(4);
        part.addEdge(1, HexagonalEdge.EAST_60, 2);
        part.addEdge(2, HexagonalEdge.EAST_60, 3);
        part.addEdge(3, HexagonalEdge.BACK, 4);
        add(part);

        part = new PartHexPrism('F');
        part.prepareRotations(4);
        part.specialCell(1);
        part.specialCell(2);
        part.addEdge(1, HexagonalEdge.BACK, 2);
        part.addEdge(1, HexagonalEdge.EAST, 3);
        part.addEdge(2, HexagonalEdge.EAST_60, 4);
        add(part);

        part = new PartHexPrism('G');
        part.prepareRotations(4);
        part.addEdge(1, HexagonalEdge.BACK, 2);
        part.addEdge(1, HexagonalEdge.EAST, 3);
        part.addEdge(2, HexagonalEdge.EAST_120, 4);
        part.specialCell(4);
        add(part);

        part = new PartHexPrism('H'); // 8
        part.prepareRotations(4);
        part.addEdge(1, HexagonalEdge.BACK, 2);
        part.addEdge(1, HexagonalEdge.EAST_120, 3);
        part.addEdge(3, HexagonalEdge.WEST, 4);
        part.specialCell(1);
        part.specialCell(2);
        part.specialCell(4);
        add(part);

        part = new PartHexPrism('I');
        part.prepareRotations(4);
        part.addEdge(1, HexagonalEdge.EAST_120, 2);
        part.addEdge(2, HexagonalEdge.FRONT, 3);
        part.addEdge(3, HexagonalEdge.EAST_120, 4);
        add(part);

        part = new PartHexPrism('J');
        part.prepareRotations(4);
        part.addEdge(1, HexagonalEdge.EAST, 2);
        part.addEdge(2, HexagonalEdge.EAST_120, 3);
        part.addEdge(3, HexagonalEdge.BACK, 4);
        part.specialCell(2);
        // NOT TODO more connections- NO NEED, we place the branches, not the edges
        add(part);

        part = new PartHexPrism('K');
        part.prepareRotations(4);
        part.addEdge(1, HexagonalEdge.EAST_60, 2);
        part.addEdge(1, HexagonalEdge.WEST_M60, 3);
        part.addEdge(3, HexagonalEdge.BACK, 4);
        part.specialCell(2);
        part.specialCell(3);
        part.specialCell(4);
        add(part);
    }

    public void buildSnowFlake() { 
        parts.clear();
        totalFill = 0;

        PartHexPrism part;

        part = new PartHexPrism('A');
        part.prepareRotations(4);
        part.addEdge(1, HexagonalEdge.EAST, 2);
        part.addEdge(2, HexagonalEdge.EAST, 3);
        part.addEdge(3, HexagonalEdge.EAST, 4);
        add(part);

        part = new PartHexPrism('B');
        part.prepareRotations(3);
        part.addEdge(1, HexagonalEdge.EAST, 2);
        part.addEdge(1, HexagonalEdge.WEST_M60, 3);
        part.addEdge(3, HexagonalEdge.EAST_60, 2);

        add(part);

        part = new PartHexPrism('C');
        part.prepareRotations(4);
        part.addEdge(1, HexagonalEdge.WEST_M120, 2);
        part.addEdge(1, HexagonalEdge.EAST, 3);
        part.addEdge(1, HexagonalEdge.EAST_120, 4);
        add(part);

        part = new PartHexPrism('D');
        part.prepareRotations(3);
        part.addEdge(1, HexagonalEdge.EAST, 2);
        part.addEdge(2, HexagonalEdge.EAST, 3);
        add(part);

        part = new PartHexPrism('E');
        part.prepareRotations(4);
        part.addEdge(1, HexagonalEdge.EAST, 2);
        part.addEdge(3, HexagonalEdge.EAST, 4);
        part.addEdge(1, HexagonalEdge.WEST_M120, 3);
        part.addEdge(1, HexagonalEdge.WEST_M60, 4);
        part.addEdge(2, HexagonalEdge.WEST_M120, 4);
        add(part);

        part = new PartHexPrism('F');
        part.prepareRotations(4);
        part.addEdge(1, HexagonalEdge.EAST, 2);
        part.addEdge(2, HexagonalEdge.EAST, 3);
        part.addEdge(3, HexagonalEdge.EAST_60, 4);
        add(part);
        
        part = new PartHexPrism('G');
        part.prepareRotations(4);
        part.addEdge(1, HexagonalEdge.EAST, 2);
        part.addEdge(2, HexagonalEdge.EAST, 3);
        part.addEdge(2, HexagonalEdge.EAST_60, 4);
        part.addEdge(3, HexagonalEdge.EAST_120, 4);
        add(part);

        part = new PartHexPrism('H');
        part.prepareRotations(4);
        part.addEdge(1, HexagonalEdge.WEST_M60, 2);
        part.addEdge(2, HexagonalEdge.WEST_M120, 3);
        part.addEdge(3, HexagonalEdge.WEST_M60, 4);
        add(part);

        part = new PartHexPrism('I');
        part.prepareRotations(3);
        part.addEdge(1, HexagonalEdge.EAST_60, 2);
        part.addEdge(1, HexagonalEdge.WEST_M60, 3);
        add(part);

        part = new PartHexPrism('J');
        part.prepareRotations(4);
        part.addEdge(1, HexagonalEdge.WEST_M120, 2);
        part.addEdge(2, HexagonalEdge.WEST_M60, 3);
        part.addEdge(3, HexagonalEdge.EAST, 4);
        add(part);
    }

}
