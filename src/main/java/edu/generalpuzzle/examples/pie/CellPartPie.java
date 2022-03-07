package edu.generalpuzzle.examples.pie;

import edu.generalpuzzle.infra.CellId;
import edu.generalpuzzle.infra.ICellPart;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created by IntelliJ IDEA.
 * Date: 16/06/2008
 */
public class CellPartPie extends ICellPart {

    public enum edge {REF, CW}

    public CellPartPie occupied;
    public boolean matchLock;

    @Override
    public int getRots() { return edge.values().length; }
    
    public CellPartPie() {
        super();
        this.dummyEdge = new PieEdge();
    }

    @Override
    public void init(CellId id) {
        this.id = id;
        this.cell = new ICellPart[dummyEdge.getSize()];
        this.keys = new int[dummyEdge.getSize()];
    }

    public CellPartPie(CellId id) {
        super(id);
        this.id = id;
        this.dummyEdge = new PieEdge();
//        this.dummyCellId = new CellId();
        this.cell = new ICellPart[dummyEdge.getSize()];
        this.keys = new int[dummyEdge.getSize()];
    }

    /** same part, only keys moved */
    @Override
    protected void swap(int ... rotate) {
//        ICellPart firstCell = cell[rotate [0]];
        int firstKey = keys[rotate [0]];
        for (int i = 0; i < rotate.length-1; i++) {
//            cell[rotate [i]] = cell[rotate [i+1]];
            keys[rotate [i]] = keys[rotate [i+1]];
        }
//        cell[rotate [rotate.length-1]] = firstCell;
        keys[rotate [rotate.length-1]] = firstKey;
    }

    @Override
    public void rotate(int param) {

        // reflect, aka flip
        if (param == edge.REF.ordinal()) {
//            swap(PieEdge.CCW_0, PieEdge.CW_0);
//            swap(PieEdge.CCW_2, PieEdge.CW_2);
//            swap(PieEdge.CW_1, PieEdge.CCW_3);
//            swap(PieEdge.CCW_1,  PieEdge.CW_3);
            swap(PieEdge.FRONT,  PieEdge.BACK);
        }
//        // rotate clock wise - xy
//        else
//        if (param == edge.CW.ordinal()) {
//            swap(PieEdge.CW_0, PieEdge.CW_1, PieEdge.CW_2, PieEdge.CW_3);
//            swap(PieEdge.CCW_0, PieEdge.CCW_1, PieEdge.CCW_2, PieEdge.CCW_3);
//        }
    }

    @Override
    public Element graphCell(Document doc) {
        Element cyl = doc.createElement("Cylinder");
        cyl.setAttribute("radius","4.5");
        cyl.setAttribute("height","2");
        return cyl;
    }

}
