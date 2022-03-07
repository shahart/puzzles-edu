package edu.generalpuzzle.examples.tangram;

import edu.generalpuzzle.infra.IEdge;

/**
 * Created by IntelliJ IDEA.
 * Date: 16/10/2008
 */
public class EdgeTang extends IEdge {

    public final static int UP = IEdge.size;
    public final static int UP_RIGHT = UP + 1;
    public final static int RIGHT = UP_RIGHT+1;
    public final static int RIGHT_DOWN = RIGHT + 1;
    public final static int DOWN = RIGHT_DOWN + 1;
    public final static int DOWN_LEFT = DOWN + 1;
    public final static int LEFT = DOWN_LEFT + 1;
    public final static int LEFT_UP = LEFT + 1;

    public final static int size = LEFT_UP + 1;

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
        else if (edge == LEFT)
            return RIGHT;
        else if (edge == RIGHT)
            return LEFT;
        else if (edge == UP_RIGHT)
            return DOWN_LEFT;
        else if (edge == DOWN_LEFT)
            return UP_RIGHT;
        else if (edge == RIGHT_DOWN)
            return LEFT_UP;
        else if (edge == LEFT_UP)
            return RIGHT_DOWN;
        else
            return super.symmetricEdge(edge);
    }

    @Override
    public String stringValue(int edge) {
        if (edge == UP)
            return "UP";
        else if (edge == DOWN)
            return "DOWN";
        if (edge == LEFT)
            return "LEFT";
        else if (edge == RIGHT)
            return "RIGHT";
        if (edge == RIGHT_DOWN)
            return "RIGHT_DOWN";
        else if (edge == LEFT_UP)
            return "LEFT_UP";
        else if (edge == DOWN_LEFT)
            return "DOWN_LEFT";
        else if (edge == UP_RIGHT)
            return "UP_RIGHT";
        else
            return super.stringValue(edge);
    }


    @Override
    public void edgeOffset(int edge, Double dims[]) {
        final double s2 = 1.4142; // Math.sqrt(2.0);


        if (edge == RIGHT)
            dims[IEdge.X] += IEdge.OFFSET ;
        else if (edge == LEFT)
            dims[IEdge.X] -= IEdge.OFFSET ;
        else if (edge == DOWN)
            dims[IEdge.Y] -= IEdge.OFFSET;
        else if (edge == UP)
            dims[IEdge.Y] += IEdge.OFFSET;
        else if (edge == DOWN_LEFT) {
            dims[IEdge.Y] -= IEdge.OFFSET *s2;
            dims[IEdge.X] -= IEdge.OFFSET *s2;
        }
        else if (edge == UP_RIGHT) {
            dims[IEdge.X] += IEdge.OFFSET *s2;
            dims[IEdge.Y] += IEdge.OFFSET *s2;
        }
        else if (edge == LEFT_UP) {
            dims[IEdge.X] -= IEdge.OFFSET *s2;
            dims[IEdge.Y] += IEdge.OFFSET *s2;
        }
        else if (edge == RIGHT_DOWN) {
            dims[IEdge.X] += IEdge.OFFSET *s2;
            dims[IEdge.Y] -= IEdge.OFFSET *s2;
        }
        else
            super.edgeOffset(edge, dims);
    }

    @Override
    public int mark(int edge) {       // TODO impl as in GridTang
        if (edge == UP)
            return -100;
        else if (edge == RIGHT)       // TODO problem, as 1 to 6 (=right) is the same as 7 to 0
            return 10;
        else if (edge == DOWN)
            return 100;
        else if (edge == LEFT)
            return -10;
        else
            return 1;
//        else if (edge == RIGHT_DOWN)
//            return 1;
//        else if (edge == DOWN_LEFT)
//            return -1;
//        else if (edge == LEFT_UP)
//            return 1;
//        else if (edge == UP_RIGHT)
//            return -1;
//        else {
//            System.out.println("invalid edge");
//            return Integer.MAX_VALUE;
//        }
    }


}
