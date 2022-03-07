package edu.generalpuzzle.examples.cube.dimension2;

import edu.generalpuzzle.infra.IEdge;

/**
 * Created by IntelliJ IDEA.
 * Date: 29/05/2008
 */
public class Edge2D extends Edge1D { // y
    public final static int UP = Edge1D.size;
    public final static int DOWN = UP + 1;

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public int symmetricEdge(int edge) {
        if (edge == UP)
            return DOWN;
        else if (edge == DOWN)
            return UP;
        else
            return super.symmetricEdge(edge);
    }


    public final static int size = DOWN + 1;

    @Override
    public String stringValue(int edge) {
        if (edge == UP)
            return "UP";
        else if (edge == DOWN)
            return "DOWN";
        else
            return super.stringValue(edge);
    }

    @Override
    public void edgeOffset(int edge, Double dims[]) {
        if (edge == UP)
            dims[IEdge.Z] -= IEdge.OFFSET;
        else if (edge == DOWN)
            dims[IEdge.Z] += IEdge.OFFSET;
        else
            super.edgeOffset(edge, dims);
    }

    @Override
    public  int mark(int edge) {
        if (edge == UP)
            return -10000;//100;//-10000;
        else if (edge == DOWN)
            return +10000;//-100;//+10000;
        else
            return super.mark(edge);
    }

}

