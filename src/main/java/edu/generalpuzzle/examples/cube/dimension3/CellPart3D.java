package edu.generalpuzzle.examples.cube.dimension3;

import edu.generalpuzzle.infra.CellId;
import edu.generalpuzzle.infra.ICellPart;

/**
 * Created by IntelliJ IDEA.
 * Date: 16/06/2008
 */
public class CellPart3D extends ICellPart {

    enum edge {REF,XY,XZ,YZ}

    @Override
    public int getRots() { return edge.values().length; }
    

    public CellPart3D() {
        super();
        this.dummyEdge = new Edge3D();
//        this.dummyCellId = new CellId();
    }

//    public void init(CellId id) {
//        this.id = id;
//        this.cell = new ICellPart[dummyEdge.getSize()];
//    }

    public CellPart3D(CellId id) {
        super(id);
//        this.id = id;
        this.dummyEdge = new Edge3D();
//        this.dummyCellId = new CellId();
        this.cell = new ICellPart[dummyEdge.getSize()];
        this.keys = new int[dummyEdge.getSize()];
    }


    @Override
    public void rotate(int param) {
        if (param == edge.REF.ordinal()) { // reflect 2D/ 3D - putting a mirror
            swap(Edge3D.LEFT, Edge3D.RIGHT);
        }
        else if (param == edge.XY.ordinal()) { // xy: aka x to y
            swap(Edge3D.LEFT, Edge3D.UP, Edge3D.RIGHT, Edge3D.DOWN);
            // no change in BACK FRONT
        }
        else if (param == edge.XZ.ordinal()) { // xz
            swap(Edge3D.LEFT, Edge3D.BACK, Edge3D.RIGHT, Edge3D.FRONT);
        }
        else if (param == edge.YZ.ordinal()) { // yz
            swap(Edge3D.FRONT, Edge3D.UP, Edge3D.BACK, Edge3D.DOWN);
        }
//        else if (param == 4) { // reflect 3D
//            front  6
//            right 2
//            up 4
//
//            back 6
//            up 4
//            left 2
//        }
    }

//    public static Element graphCell(Document doc) {
//        Element box = doc.createElement("Box");
//        box.setAttribute("size","7 7 7");
    //         box.setAttribute("onclick", "window.close()");
//        return box;
//    }


}
