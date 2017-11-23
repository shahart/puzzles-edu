package edu.generalpuzzle.examples.tangram;

import edu.generalpuzzle.infra.CellId;
import edu.generalpuzzle.infra.ICellPart;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created by IntelliJ IDEA.
 * Date: 16/06/2008
 */
public class CellPartTang extends ICellPart {

//    public int direction = 0;
    public static int currentDir = 0;

    public enum edge {REF,XY}

    public int getRots() { return edge.values().length; }
    
    public CellPartTang() {
        super();
        this.dummyEdge = new EdgeTang();
    }

    @Override
    public void init(CellId id) {
        this.id = id;
        this.cell = new ICellPart[dummyEdge.getSize()];
        this.keys = new int[dummyEdge.getSize()];
    }

    public CellPartTang(CellId id) {
        super(id);
        this.id = id;
        this.dummyEdge = new EdgeTang();
//        this.dummyCellId = new CellId();
        this.cell = new ICellPart[dummyEdge.getSize()];
        this.keys = new int[dummyEdge.getSize()];
    }


    @Override
    public void rotate(int param) { 
        // reflect, aka flip
        if (param == edge.REF.ordinal()) {
            swap(EdgeTang.LEFT, EdgeTang.RIGHT);
            swap(EdgeTang.LEFT_UP, EdgeTang.UP_RIGHT);
            swap(EdgeTang.RIGHT_DOWN, EdgeTang.DOWN_LEFT);
            final int newDir[] = new int[] {7,6,5,4,3,2,1,0};
            special = newDir[special%10];
        }
        // rotate clock wise - xy
        else
        if (param == edge.XY.ordinal()) {
            swap(EdgeTang.UP, EdgeTang.RIGHT,  EdgeTang.DOWN,  EdgeTang.LEFT);
            swap(EdgeTang.UP_RIGHT, EdgeTang.RIGHT_DOWN,  EdgeTang.DOWN_LEFT,  EdgeTang.LEFT_UP);
            int reminder = special % 10;
            reminder++;
            if (reminder == 8)
                reminder = 0;
            special = (special /10 * 10 + reminder);
        }
    }

    public Element graphCell(Document doc) {
        Element triangle = doc.createElement("IndexedFaceSet");
        triangle.setAttribute("coordIndex",
                "                5 4 3 -1\n" +
                "                0 1 2 -1\n" +
                "                1 0 3 4 -1\n" +
                "                2 1 4  5 -1\n" +
                "                0 2 5 3");

        Element coords = doc.createElement("Coordinate");
        if (currentDir == 0) {
            coords.setAttribute("point",
                    "              1 1 2, 5 1 2, 5 5 2, \n" +
                    "              1 1 -2, 5 1 -2, 5 5 -2");
        }
        if (currentDir == 7) {
            coords.setAttribute("point",
                    "              1 1 2, 5 5 2, 1 5 2, \n" +
                    "              1 1 -2, 5 5 -2, 1 5 -2");
        }
        if (currentDir == 6) {
            coords.setAttribute("point",
                    "              -1 1 2, -1  5 2, -5 5 2, \n" +
                    "              -1 1 -2, -1  5 -2, -5 5 -2");
        }
        if (currentDir == 5) {
            coords.setAttribute("point",
                    "              -1 1 2, -5 5 2, -5 1 2, \n" +
                    "              -1 1 -2, -5 5 -2, -5 1 -2");
        }
        if (currentDir == 4) {
            coords.setAttribute("point",
                    "              -1 -1 2, -5 -1 2, -5 -5 2, \n" +
                    "              -1 -1 -2, -5 -1 -2, -5 -5 -2");
        }
        if (currentDir == 3) {
            coords.setAttribute("point",
                    "              -1 -1 2, -5 -5 2, -1 -5 2, \n" +
                    "              -1 -1 -2, -5 -5 -2, -1 -5 -2");
        }
        if (currentDir == 2) {
            coords.setAttribute("point",
                    "              1 -1 2, 1 -5 2, 5 -5 2, \n" +
                    "              1 -1 -2, 1 -5 -2, 5 -5 -2");
        }
        if (currentDir == 1) {
            coords.setAttribute("point",
                    "              1 -1 2, 5 -5 2, 5 -1 2, \n" +
                    "              1 -1 -2, 5 -5 -2, 5 -1 -2");
        }

        ++ currentDir;
        if (currentDir == 8) currentDir = 0;
        
        triangle.appendChild(coords);
        return triangle;
    }


}
