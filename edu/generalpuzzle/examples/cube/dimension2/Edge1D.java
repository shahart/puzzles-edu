package edu.generalpuzzle.examples.cube.dimension2;

import edu.generalpuzzle.infra.IEdge;

/**
 * Created by IntelliJ IDEA.
 * Date: 16/06/2008
 */
public class Edge1D extends IEdge {

    // not using enum { LEFT,RIGHT} and ordinal and values()[index] as we can't inherit from it.example: Edge2D is 1D + new dimension

    public final static int LEFT = IEdge.size;
    public final static int RIGHT = LEFT+1;

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void edgeOffset(int edge, Double dims[]) {
        if (edge == LEFT)
            dims[IEdge.Y] -= IEdge.OFFSET;
        else if (edge == RIGHT)
            dims[IEdge.Y] += IEdge.OFFSET;
        else
            super.edgeOffset(edge, dims);
    }

    public int symmetricEdge(int edge) {
        if (edge == LEFT)
            return RIGHT;
        else if (edge == RIGHT)
            return LEFT;
        else
            return super.symmetricEdge(edge);
    }

    public final static int size = RIGHT + 1;

    @Override
    public String stringValue(int edge) {
        if (edge == LEFT)
            return "LEFT";
        else if (edge == RIGHT)
            return "RIGHT";
        else
            return super.stringValue(edge);
    }

    public int mark(int edge) {
        if (edge == LEFT)
            return -100;//1;//-100;
        else if (edge == RIGHT)
            return +100;//-1;//+100;
        else {
            System.out.println("invalid edge");
            return Integer.MAX_VALUE;
        }
    }
}
