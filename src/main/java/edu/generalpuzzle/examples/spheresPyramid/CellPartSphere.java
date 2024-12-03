package edu.generalpuzzle.examples.spheresPyramid;

import edu.generalpuzzle.infra.CellId;
import edu.generalpuzzle.infra.ICellPart;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created by IntelliJ IDEA.
 * Date: 16/06/2008
 */
public class CellPartSphere extends ICellPart {

    enum edge {REF,XY, N60, X120, FM150M30, S60};//,X60,X120}                 // TODO

    @Override
    public int getRots() { return edge.values().length; }
    
    public CellPartSphere() {
        super();
        this.dummyEdge = new EdgeSphere();
    }

    @Override
    public void init(CellId id) {
        this.id = id;
        this.cell = new ICellPart[dummyEdge.getSize()];
        this.keys = new int[dummyEdge.getSize()];
        
    }

    public CellPartSphere(CellId id) {
        super(id);
        this.id = id;
        this.dummyEdge = new EdgeSphere();
//        this.dummyCellId = new CellId();
        this.cell = new ICellPart[dummyEdge.getSize()];
        this.keys = new int[dummyEdge.getSize()];

    }


    @Override
    public void rotate(int param) { // TODO 4 rotates, 2 steps for each. revalidate
//        // reflect, aka flip
        if (param == edge.REF.ordinal()) {
            // floor 0
            swap(EdgeSphere.EAST, EdgeSphere.WEST);
            swap(EdgeSphere.EAST_60, EdgeSphere.EAST_120);
            swap(EdgeSphere.WEST_M60, EdgeSphere.WEST_M120);
            // floor front
            swap(EdgeSphere.FRONT_M30,  EdgeSphere.FRONT_M150);
            swap(EdgeSphere.FRONT_EAST_30,  EdgeSphere.FRONT_EAST_150);
            // floor back
            swap(EdgeSphere.BACK_M30,  EdgeSphere.BACK_M150);
            swap(EdgeSphere.BACK_EAST_30,  EdgeSphere.BACK_EAST_150);
        }
        // rotate clock wise - xy
        else
        if (param == edge.XY.ordinal()) {
            // floor 0
            swap(EdgeSphere.EAST, EdgeSphere.EAST_60, EdgeSphere.EAST_120, EdgeSphere.WEST, EdgeSphere.WEST_M120, EdgeSphere.WEST_M60);
            // floor front
            swap(EdgeSphere.FRONT_NORTH, EdgeSphere.FRONT_EAST_150, EdgeSphere.FRONT_M150, EdgeSphere.FRONT_SOUTH, EdgeSphere.FRONT_M30, EdgeSphere.FRONT_EAST_30);
            // floor back
            swap(EdgeSphere.BACK_NORTH, EdgeSphere.BACK_EAST_150, EdgeSphere.BACK_M150, EdgeSphere.BACK_SOUTH, EdgeSphere.BACK_M30, EdgeSphere.BACK_EAST_30);
        }
        else if (param == edge.N60.ordinal()) {

            swap(EdgeSphere.BACK_EAST_30, EdgeSphere.FRONT_NORTH, EdgeSphere.WEST_M120);
            swap(EdgeSphere.BACK_SOUTH, EdgeSphere.EAST_60, EdgeSphere.FRONT_M150);
            swap(EdgeSphere.WEST, EdgeSphere.BACK_EAST_150, EdgeSphere.EAST_120);
            swap(EdgeSphere.WEST_M60, EdgeSphere.EAST, EdgeSphere.FRONT_M30);
        }
        else if (param == edge.X120.ordinal()) {

            swap(EdgeSphere.BACK_EAST_30, EdgeSphere.EAST_60, EdgeSphere.EAST);
            swap(EdgeSphere.BACK_SOUTH, EdgeSphere.EAST_120, EdgeSphere.FRONT_M30);
            swap(EdgeSphere.FRONT_NORTH, EdgeSphere.WEST_M60, EdgeSphere.BACK_EAST_150);
            swap(EdgeSphere.WEST_M120, EdgeSphere.WEST, EdgeSphere.FRONT_M150);
        }
        else if (param == edge.FM150M30.ordinal()) {

            swap(EdgeSphere.FRONT_NORTH, EdgeSphere.EAST_120, EdgeSphere.EAST_60);
            swap(EdgeSphere.WEST, EdgeSphere.BACK_EAST_30, EdgeSphere.FRONT_M30);
            swap(EdgeSphere.EAST, EdgeSphere.FRONT_M150, EdgeSphere.BACK_EAST_150);
            swap(EdgeSphere.BACK_SOUTH, EdgeSphere.WEST_M60, EdgeSphere.WEST_M120);
        }

        else if (param == edge.S60.ordinal()) {

            swap(EdgeSphere.FRONT_SOUTH, EdgeSphere.EAST_120, EdgeSphere.BACK_EAST_30);
            swap(EdgeSphere.FRONT_EAST_150, EdgeSphere.BACK_NORTH, EdgeSphere.WEST_M60);
            swap(EdgeSphere.EAST, EdgeSphere.FRONT_EAST_30, EdgeSphere.EAST_60);
            swap(EdgeSphere.WEST, EdgeSphere.BACK_M150, EdgeSphere.WEST_M120);
        }

//        else if (param == edge.X120.ordinal()) { // X120 TODO
//        }
//        else if (param == edge.XY.ordinal()) { // X60 TODO
//        }
//        else
//            System.out.println(param);
    }

    @Override
    public Element graphCell(Document doc) {
        Element e = doc.createElement("Sphere");
        e.setAttribute("radius","4.7");
        e.setAttribute("onclick", "window.close()");
        return e;
    }

}
