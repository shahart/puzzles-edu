package edu.generalpuzzle.examples.pie;

import edu.generalpuzzle.infra.IEdge;

/**
 * Created by IntelliJ IDEA.
 * Date: 16/10/2008
 */
public class PieEdge extends IEdge {

    public final static int CW_0 = IEdge.size;  // 0 = 12 o'clock
    public final static int CCW_0 = CW_0 + 1;
    public final static int CW_1 = CCW_0 + 1; // 3
    public final static int CCW_1 = CW_1 + 1;
    public final static int CW_2 = CCW_1 + 1; // 6
    public final static int CCW_2 = CW_2 + 1;
    public final static int CW_3 = CCW_2 + 1; // 9
    public final static int CCW_3 = CW_3 + 1;

    public final static int FRONT = CCW_3 + 1;
    public final static int BACK = FRONT + 1;


    public final static int size = BACK + 1;

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public int symmetricEdge(int edge) {
        if (edge == FRONT)
            return BACK;
        else if (edge == BACK)
            return FRONT;
        else if (edge == CW_0)
            return CCW_0;
        else if (edge == CCW_0)
            return CW_0;
        else if (edge == CW_1)
            return CCW_1;
        else if (edge == CCW_1)
            return CW_1;
        else if (edge == CW_2)
            return CCW_2;
        else if (edge == CCW_2)
            return CW_2;
        else if (edge == CW_3)
            return CCW_3;
        else if (edge == CCW_3)
            return CW_3;

        else
            return super.symmetricEdge(edge);
    }

    @Override
    public String stringValue(int edge) {
        if (edge == CW_0)
            return "CW_0";
        else if (edge == CW_1)
            return "CW_1";
        else if (edge == CW_2)
            return "CW_2";
        else if (edge == CW_3)
            return "CW_3";
        else if (edge == CCW_0)
            return "CCW_0";
        else if (edge == CCW_1)
            return "CCW_1";
        else if (edge == CCW_2)
            return "CCW_2";
        else if (edge == CCW_3)
            return "CCW_3";
        else if (edge == BACK)
            return "BACK";
        else if (edge == FRONT)
            return "FRONT";
        else
        return Integer.toString(edge);
//            return super.stringValue(edge);
    }


    @Override
    public void edgeOffset(int edge, Double dims[]) {

        if (edge == FRONT)
            dims[IEdge.Y] += IEdge.OFFSET ;
        else if (edge == BACK)
            dims[IEdge.Y] -= IEdge.OFFSET ;
        else if (edge == CW_0 || edge == CCW_2)
            dims[IEdge.X] -= IEdge.OFFSET ;
        else if (edge == CW_1 || edge == CCW_3)
            dims[IEdge.Z] += IEdge.OFFSET ;
        else if (edge == CW_2 || edge == CCW_0)
            dims[IEdge.X] += IEdge.OFFSET ;
        else if (edge == CW_3 || edge == CCW_1)
            dims[IEdge.Z] -= IEdge.OFFSET ;
        else
            super.edgeOffset(edge, dims);
    }

    @Override
    public int mark(int edge) {
        if (edge == FRONT)
            return +100;
        else if (edge == BACK)
            return -100;
        else if( edge%2 ==0)
            return 1;
        else if( edge %2==1)
            return -1;
        else {
            System.out.println("invalid edge");
            return Integer.MAX_VALUE;
        }
    }


}
