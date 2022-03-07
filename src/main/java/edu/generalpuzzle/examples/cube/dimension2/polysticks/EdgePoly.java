package edu.generalpuzzle.examples.cube.dimension2.polysticks;

import edu.generalpuzzle.examples.cube.dimension2.Edge2D;
import edu.generalpuzzle.infra.IEdge;

/**
 * Created by IntelliJ IDEA.
 * Date: 31/08/2010
 */
public class EdgePoly extends Edge2D { // y
    public final static int UP_RIGHT = Edge2D.size; // Rotate CW- starts at 45
    public final static int LEFT_UP = UP_RIGHT + 1;
    public final static int DOWN_LEFT = LEFT_UP + 1;
    public final static int RIGHT_DOWN = DOWN_LEFT + 1;

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public int symmetricEdge(int edge) {
        if (edge == UP_RIGHT)
            return DOWN_LEFT;
        else if (edge == DOWN_LEFT)
            return UP_RIGHT;
        if (edge == LEFT_UP)
            return RIGHT_DOWN;
        else if (edge == RIGHT_DOWN)
            return LEFT_UP;
        else
            return super.symmetricEdge(edge);
    }


    public final static int size = RIGHT_DOWN + 1;

    @Override
    public String stringValue(int edge) {
        if (edge == RIGHT_DOWN)
            return "RIGHT_DOWN";
        else if (edge == UP_RIGHT)
            return "UP_RIGHT";
        if (edge == LEFT_UP)
            return "LEFT_UP";
        else if (edge == DOWN_LEFT)
            return "DOWN_LEFT";
        else
            return super.stringValue(edge);
    }

    @Override
    public void edgeOffset(int edge, Double dims[]) {
        if (edge == UP_RIGHT) {
            dims[IEdge.Z] -= IEdge.OFFSET;
            dims[IEdge.Y] += IEdge.OFFSET;
        }
        else if (edge == RIGHT_DOWN) {
            dims[IEdge.Z] += IEdge.OFFSET;
            dims[IEdge.Y] += IEdge.OFFSET;
        }
        else if (edge == DOWN_LEFT) {
            dims[IEdge.Z] += IEdge.OFFSET;
            dims[IEdge.Y] -= IEdge.OFFSET;
        }
        else if (edge == LEFT_UP) {
            dims[IEdge.Z] -= IEdge.OFFSET;
            dims[IEdge.Y] -= IEdge.OFFSET;
        }
        else
            super.edgeOffset(edge, dims);
    }

    @Override
    public  int mark(int edge) {
        if (edge == UP_RIGHT)
            return -10000 + 100;//100;//-10000;
        else if (edge == DOWN_LEFT)
            return +10000 - 100;//-100;//+10000;
        if (edge == RIGHT_DOWN)
            return +10000 + 100;//100;//-10000;
        else if (edge == LEFT_UP)
            return -10000 - 100;//-100;//+10000;
        else
            return super.mark(edge);
    }

}

