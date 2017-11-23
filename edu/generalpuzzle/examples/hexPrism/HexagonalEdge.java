package edu.generalpuzzle.examples.hexPrism;

import edu.generalpuzzle.infra.IEdge;

/**
 * Created by IntelliJ IDEA.
 * Date: 16/10/2008
 */
public class HexagonalEdge extends IEdge {

    public final static int FRONT = IEdge.size;
    public final static int BACK = FRONT + 1;
    public final static int EAST = BACK+1;
    public final static int EAST_60 = EAST + 1;
    public final static int EAST_120 = EAST_60 + 1;
    public final static int WEST = EAST_120 + 1;
    public final static int WEST_M120 = WEST + 1;
    public final static int WEST_M60 = WEST_M120 + 1;

    public final static int size = WEST_M60 + 1;

    @Override
    public int getSize() {
        return size;
    }

    public int symmetricEdge(int edge) {
        if (edge == EAST)
            return WEST;
        else if (edge == EAST_60)
            return WEST_M120;
        else if (edge == EAST_120)
            return WEST_M60;
        else if (edge == WEST)
            return EAST;
        else if (edge == WEST_M120)
            return EAST_60;
        else if (edge == WEST_M60)
            return EAST_120;
        else if (edge == FRONT)
            return BACK;
        else if (edge == BACK)
            return FRONT;
        else
            return super.symmetricEdge(edge);
    }

    @Override
    public String stringValue(int edge) {
        if (edge == EAST)
            return "EAST";
        else if (edge == WEST)
            return "WEST";
        if (edge == WEST_M120)
            return "WEST_M120";
        else if (edge == WEST_M60)
            return "WEST_M60";
        if (edge == EAST_120)
            return "EAST_120";
        else if (edge == EAST_60)
            return "EAST_60";
        else if (edge == BACK)
            return "BACK";
        else if (edge == FRONT)
            return "FRONT";
        else
            return super.stringValue(edge);
    }


    @Override
    public void edgeOffset(int edge, Double dims[]) {
        final double y = Math.sin(Math.PI / 3);
        final double x = Math.cos(Math.PI / 3); // or 0.5

        if (edge == EAST)
            dims[IEdge.X] += IEdge.OFFSET ;
        else if (edge == WEST)
            dims[IEdge.X] -= IEdge.OFFSET ;
        else if (edge == WEST_M60) {
            dims[IEdge.Y] -= IEdge.OFFSET * y;
            dims[IEdge.X] += IEdge.OFFSET * x;
        }
        else if (edge == EAST_120) {
            dims[IEdge.Y] += IEdge.OFFSET * y;
            dims[IEdge.X] -= IEdge.OFFSET * x;
        }
        else if (edge == WEST_M120) {
            dims[IEdge.Y] -= IEdge.OFFSET * y;
            dims[IEdge.X] -= IEdge.OFFSET * x;
        }
        else if (edge == EAST_60) {
            dims[IEdge.X] += IEdge.OFFSET * x;
            dims[IEdge.Y] += IEdge.OFFSET * y;
        }

        else if (edge == FRONT) {
            dims[IEdge.Z] += IEdge.OFFSET ;
        }
        else if (edge == BACK) {
            dims[IEdge.Z] -= IEdge.OFFSET ;
        }
        else
            super.edgeOffset(edge, dims);
    }

    public int mark(int edge) {
        if (edge == EAST)
            return 1;
        else if (edge == WEST_M60)
            return -10+1;
        else if (edge == WEST)
            return -1;
        else if (edge == EAST_120)
            return +10-1;
        else if (edge == WEST_M120)
            return -10-1;
        else if (edge == EAST_60)
            return +10;

        else if (edge == FRONT)
            return +100;
        else if (edge == BACK)
            return -100;
        else {
            System.out.println("invalid edge");
            return Integer.MAX_VALUE;
        }
    }


}
