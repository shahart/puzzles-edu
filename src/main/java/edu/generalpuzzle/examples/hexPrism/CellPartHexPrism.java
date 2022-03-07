package edu.generalpuzzle.examples.hexPrism;

import edu.generalpuzzle.infra.CellId;
import edu.generalpuzzle.infra.ICellPart;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created by IntelliJ IDEA.
 * Date: 16/06/2008
 */
public class CellPartHexPrism extends ICellPart {

    public enum edge {REF,XY,XZ} // XZ                // TODO

    @Override
    public int getRots() { return edge.values().length; }
    
    public CellPartHexPrism() {
        super();
        this.dummyEdge = new HexagonalEdge();
    }

    @Override
    public void init(CellId id) {
        this.id = id;
        this.cell = new ICellPart[dummyEdge.getSize()];
        this.keys = new int[dummyEdge.getSize()];
    }

    public CellPartHexPrism(CellId id) {
        super(id);
        this.id = id;
        this.dummyEdge = new HexagonalEdge();
//        this.dummyCellId = new CellId();
        this.cell = new ICellPart[dummyEdge.getSize()];
        this.keys = new int[dummyEdge.getSize()];
    }


    @Override
    public void rotate(int param) { // TODO 4 rotates, 2 steps for each. revalidate
        // reflect, aka flip
//        if (param == edge.REF.ordinal()) {
//            swap(HexagonalEdge.EAST, HexagonalEdge.WEST);
//            swap(HexagonalEdge.EAST_60, HexagonalEdge.EAST_120);
//            swap(HexagonalEdge.WEST_M60, HexagonalEdge.WEST_M120);
//        }
        // rotate clock wise - xy
//        else
        if (param == edge.XY.ordinal()) {
            swap(HexagonalEdge.EAST, HexagonalEdge.EAST_60, HexagonalEdge.EAST_120, HexagonalEdge.WEST, HexagonalEdge.WEST_M120, HexagonalEdge.WEST_M60);
        }
        else if (param == edge.XZ.ordinal()) {
            swap(HexagonalEdge.BACK, HexagonalEdge.FRONT);
            swap(HexagonalEdge.EAST_60, HexagonalEdge.WEST_M60);
            swap(HexagonalEdge.EAST_120, HexagonalEdge.WEST_M120);
        }
    }

    @Override
    public Element graphCell(Document doc) {
        Element hexa = doc.createElement("IndexedFaceSet");
        hexa.setAttribute("coordIndex",
                "                6 7 8 9 10 11 -1\n" +
                "                5 4 3 2 1 0 -1\n" +
                "                1 7 6 0 -1\n" +
                "                3 9 8 2 -1\n" +
                "                1 2 8 7 -1\n" +
                "                3 4 10 9 -1\n" +
                "                4 5 11 10 -1\n" +
                "                0 6 11 5");
        Element coords = doc.createElement("Coordinate");
        coords.setAttribute("point",
                "              5 0 -2, 2.5 4.33 -2, -2.5 4.33 -2, -5 0 -2,  -2.5 -4.33  -2, 2.5 -4.33 -2, \n" +
                "              5 0 2, 2.5 4.33 2, -2.5 4.33 2, -5 0 2,  -2.5 -4.33  2, 2.5 -4.33 2");
        hexa.appendChild(coords);
        return hexa;
    }


}
