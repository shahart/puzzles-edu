package edu.generalpuzzle.examples.cube.dimension2.polysticks;

import edu.generalpuzzle.infra.CellId;
import edu.generalpuzzle.infra.ICellPart;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created by IntelliJ IDEA.
 * Date: 31/08/2010
 */
public class CellPartPoly extends ICellPart {

//    public int direction = 0;

    enum edge {REF,XY}

    @Override
    public int getRots() { return edge.values().length; }
    
    public CellPartPoly() {
        super();
        this.dummyEdge = new EdgePoly();
//        this.dummyCellId = new CellId();
    }

//    public void init(CellId id) {
//        this.id = id;
//        this.cell = new ICellPart[dummyEdge.getSize()];
//    }

    public CellPartPoly(CellId id) {
        super(id);
        this.id = id;
        this.dummyEdge = new EdgePoly();
//        this.dummyCellId = new CellId();
        this.cell = new ICellPart[dummyEdge.getSize()];
        this.keys = new int[dummyEdge.getSize()];
    }

    @Override
    public void rotate(int param) {
        // reflect, aka flip
//        if (param == edge.REF.ordinal()) {
//            swap(EdgePoly.LEFT, EdgePoly.RIGHT);
//            swap(EdgePoly.LEFT_UP, EdgePoly.UP_RIGHT);
//            swap(EdgePoly.DOWN_LEFT, EdgePoly.RIGHT_DOWN);
//            special = 1 - special;
//        }
        // rotate clock wise - xy
//        else
//        if (param == edge.XY.ordinal()) {
//            swap(EdgePoly.LEFT, EdgePoly.UP, EdgePoly.RIGHT, EdgePoly.DOWN);
//            swap(EdgePoly.LEFT_UP, EdgePoly.UP_RIGHT, EdgePoly.RIGHT_DOWN, EdgePoly.DOWN_LEFT);
//            special = 1 - special;
//        }
//        direction = 1- direction;
    }

    @Override
    public Element graphCell(Document doc) {
        Element box = doc.createElement("Box"); // TODO? line
        box.setAttribute("size",special == 1 ? /*direction == 0 */ "7 2 7" : "2 7 7");
        return box;
    }


}
