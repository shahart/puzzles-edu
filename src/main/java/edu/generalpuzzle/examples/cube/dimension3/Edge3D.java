package edu.generalpuzzle.examples.cube.dimension3;

import edu.generalpuzzle.examples.cube.dimension2.Edge2D;
import edu.generalpuzzle.infra.IEdge;

/**
 * Created by IntelliJ IDEA.
 * Date: 29/05/2008
 */
public class Edge3D extends Edge2D { // z
    public final static int FRONT = Edge2D.size;
    public final static int BACK = FRONT + 1;

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
        else
            return super.symmetricEdge(edge);
    }

    public final static int size = BACK + 1;

    @Override
    public String stringValue(int edge) {
        if (edge == BACK)
            return "BACK";
        else if (edge == FRONT)
            return "FRONT";
        else
            return super.stringValue(edge);
    }


    @Override
    public void edgeOffset(int edge, Double dims[]) {
        if (edge == BACK)
            dims[IEdge.X] -= IEdge.OFFSET;
        else if (edge == FRONT)
            dims[IEdge.X] += IEdge.OFFSET;
        else
            super.edgeOffset(edge, dims);
    }

    @Override
    public int mark(int edge) {
        if (edge == BACK)
            return 1;//-10000;//+1;
        else if (edge == FRONT)
            return -1;//10000;//-1;
        else
            return super.mark(edge);
    }

}

