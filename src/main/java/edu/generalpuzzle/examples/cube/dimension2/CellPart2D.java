package edu.generalpuzzle.examples.cube.dimension2;

import edu.generalpuzzle.infra.CellId;
import edu.generalpuzzle.infra.ICellPart;

/**
 * Created by IntelliJ IDEA.
 * Date: 16/06/2008
 */
public class CellPart2D extends ICellPart {

    enum edge {REF,XY}

    @Override
    public int getRots() { return edge.values().length; }
    
    public CellPart2D() {
        super();
        this.dummyEdge = new Edge2D();
//        this.dummyCellId = new CellId();
    }

//    public void init(CellId id) {
//        this.id = id;
//        this.cell = new ICellPart[dummyEdge.getSize()];
//    }

    public CellPart2D(CellId id) {
        super(id);
        this.id = id;
        this.dummyEdge = new Edge2D();
//        this.dummyCellId = new CellId();
        this.cell = new ICellPart[dummyEdge.getSize()];
        this.keys = new int[dummyEdge.getSize()];
    }

    @Override
    public void rotate(int param) {
        // reflect, aka flip
        if (param == edge.REF.ordinal()) {
            swap(Edge2D.LEFT, Edge2D.RIGHT);
        }
        // rotate anti clock wise - xy
        else if (param == edge.XY.ordinal()) {
            swap(Edge2D.LEFT, Edge2D.UP, Edge2D.RIGHT, Edge2D.DOWN);
        }
    }

//    public static Element graphCell(Document doc) {
//        Element box = doc.createElement("Box");
//        box.setAttribute("size","7 7 7");
    //         box.setAttribute("onclick", "window.close()");
//        return box;
//    }


}
