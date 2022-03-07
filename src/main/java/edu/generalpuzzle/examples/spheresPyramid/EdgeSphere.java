package edu.generalpuzzle.examples.spheresPyramid;

import edu.generalpuzzle.infra.IEdge;

/**
 * Created by IntelliJ IDEA.
 * Date: 29/05/2008
 */
public class EdgeSphere extends IEdge {

    // M for MINUS, all degress are Counter-Clock-Wise from the EAST

    // ground floor
    public final static int EAST = IEdge.size;
    public final static int EAST_60 = EAST + 1;
    public final static int EAST_120 = EAST_60 + 1;
    public final static int WEST = EAST_120 + 1;
    public final static int WEST_M120 = WEST + 1;
    public final static int WEST_M60 = WEST_M120 + 1;

    // other floors - original

    public final static int FRONT_NORTH = WEST_M60 +1;
    public final static int FRONT_M150 = FRONT_NORTH + 1;
    public final static int FRONT_M30 = FRONT_M150 + 1;

    public final static int BACK_EAST_30 = FRONT_M30 +1;
    public final static int BACK_EAST_150 = BACK_EAST_30 + 1;
    public final static int BACK_SOUTH = BACK_EAST_150 + 1;

    // other floors - middles.
    // only original OR middles can be in a real part

    public final static int FRONT_EAST_30 = BACK_SOUTH+1;
    public final static int FRONT_EAST_150 = FRONT_EAST_30 + 1;
    public final static int FRONT_SOUTH = FRONT_EAST_150 + 1;

    public final static int BACK_NORTH = FRONT_SOUTH +1;
    public final static int BACK_M150 = BACK_NORTH + 1;
    public final static int BACK_M30 = BACK_M150 + 1;

    public final static int size = BACK_M30 + 1;

    @Override
    public int getSize() {
        return size;
    }

    @Override
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

        // other flows - original

        else if (edge == FRONT_NORTH)
            return BACK_SOUTH;
        else if (edge == FRONT_M150)
            return BACK_EAST_30;
        else if (edge == FRONT_M30)
            return BACK_EAST_150;

        else if (edge == BACK_M150)
            return FRONT_EAST_30;
        else if (edge == BACK_M30)
            return FRONT_EAST_150;
        else if (edge == BACK_NORTH)
            return FRONT_SOUTH;

        // other flows - part II

        else if (edge == BACK_SOUTH)
            return FRONT_NORTH;
        else if (edge == BACK_EAST_30)
            return FRONT_M150;
        else if (edge == BACK_EAST_150)
            return FRONT_M30;

        else if (edge == FRONT_EAST_30)
            return BACK_M150;
        else if (edge == FRONT_EAST_150)
            return BACK_M30;
        else if (edge == FRONT_SOUTH)
            return BACK_NORTH;

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

        else if (edge == FRONT_NORTH)
            return "FRONT_NORTH";
        else if (edge == FRONT_M30)
            return "FRONT_M30";
        if (edge == FRONT_M150)
            return "FRONT_M150";

        else if (edge == BACK_NORTH)
            return "BACK_NORTH";
        if (edge == BACK_M30)
            return "BACK_M30";
        else if (edge == BACK_M150)
            return "BACK_M150";

        // part II

        else if (edge == BACK_EAST_30)
            return "BACK_EAST_30";
        else if (edge == BACK_EAST_150)
            return "BACK_EAST_150";
        if (edge == BACK_SOUTH)
            return "BACK_SOUTH";

        else if (edge == FRONT_EAST_30)
            return "FRONT_EAST_30";
        if (edge == FRONT_EAST_150)
            return "FRONT_EAST_150";
        else if (edge == FRONT_SOUTH)
            return "FRONT_SOUTH";

        else
            return super.stringValue(edge);
    }

    /** <a href="sphereMath.gif">
    * math</a>.*/
    @Override
    public void edgeOffset(int edge, Double dims[]) {
        final double h = 1.683; // Math.sqrt(2.0/3);
        final double y = 1.732; // Math.sin(Math.PI / 3);
        final double x = 1; // Math.cos(Math.PI / 3); // or 0.5
        final double r = IEdge.OFFSET / 2;

        if (edge == EAST)
            dims[IEdge.X] += 2*r;
        else if (edge == WEST)
            dims[IEdge.X] -= 2*r;
        else if (edge == WEST_M60) {
            dims[IEdge.X] += r;
            dims[IEdge.Y] -= y*r;
        }
        else if (edge == EAST_120) {
            dims[IEdge.X] -= r;
            dims[IEdge.Y] += y*r;
        }
        else if (edge == WEST_M120) {
            dims[IEdge.X] -= r;
            dims[IEdge.Y] -= y*r;
        }
        else if (edge == EAST_60) {
            dims[IEdge.X] += r;
            dims[IEdge.Y] += y*r;
        }

        else if (edge == FRONT_NORTH) {
            dims[IEdge.Y] += (y-1/y)*r;
            dims[IEdge.Z] += h*r;
        }
        else if (edge == BACK_NORTH) {
            dims[IEdge.Y] += (y-1/y)*r;
            dims[IEdge.Z] -= h*r;
        }
        else if (edge == FRONT_M30) {
            dims[IEdge.X] += r;
            dims[IEdge.Y] -= 1/y*r;
            dims[IEdge.Z] += h*r;
        }
        else if (edge == FRONT_M150) {
            dims[IEdge.X] -= r;
            dims[IEdge.Y] -= 1/y*r;
            dims[IEdge.Z] += h*r;
        }
        else if (edge == FRONT_EAST_30) {
            dims[IEdge.X] += r;
            dims[IEdge.Y] += 1/y*r;
            dims[IEdge.Z] += h*r;
        }
        else if (edge == FRONT_EAST_150) {
            dims[IEdge.X] -= r;
            dims[IEdge.Y] += 1/y*r;
            dims[IEdge.Z] += h*r;
        }

        else if (edge == BACK_EAST_30) {
            dims[IEdge.X] += r;
            dims[IEdge.Y] += 1/y*r;
            dims[IEdge.Z] -= h*r;
        }
        else if (edge == BACK_EAST_150) {
            dims[IEdge.X] -= r;
            dims[IEdge.Y] += 1/y*r;
            dims[IEdge.Z] -= h*r;
        }
        else if (edge == BACK_M30) {
            dims[IEdge.X] += r;
            dims[IEdge.Y] -= 1/y*r;
            dims[IEdge.Z] -= h*r;
        }
        else if (edge == BACK_M150) {
            dims[IEdge.X] -= r;
            dims[IEdge.Y] -= 1/y*r;
            dims[IEdge.Z] -= h*r;
        }
        else if (edge == FRONT_SOUTH) {
            dims[IEdge.Y] -= (y-1/y)*r;
            dims[IEdge.Z] += h*r;
        }
        else if (edge == BACK_SOUTH) {
            dims[IEdge.Y] -= (y-1/y)*r;
            dims[IEdge.Z] -= h*r;
        }
        else
            super.edgeOffset(edge, dims);

    }

    @Override
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

        else if (edge == FRONT_NORTH)
            return (+1000+10);
        else if (edge == BACK_NORTH)
            return (-1000+10);
        else if (edge == FRONT_M30)
            return (-10+1+1000);
        else if (edge == BACK_M150)
            return (-10-1000);
        else if (edge == BACK_M30)
            return (-10+1-1000);
        else if (edge == FRONT_M150)
            return (-10+1000);

        else if (edge == FRONT_SOUTH)
            return +100-10;
        else if (edge == BACK_SOUTH)
            return -100-10;
        else if (edge == FRONT_EAST_30)
            return 1+100;
        else if (edge == BACK_EAST_150)
            return -1-100;
        else if (edge == BACK_EAST_30)
            return +1-100;
        else if (edge == FRONT_EAST_150)
            return -1+100;

        else {
            System.out.println("invalid edge");
            return Integer.MAX_VALUE;
        }
    }

}

