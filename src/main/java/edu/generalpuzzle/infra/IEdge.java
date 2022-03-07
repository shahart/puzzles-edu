package edu.generalpuzzle.infra;

import edu.generalpuzzle.main.PuzzleException;

/**
 * Created by IntelliJ IDEA.
 * Date: 16/06/2008
 */
public class IEdge {

    public static final int X=0;
    public static final int Y=1;
    public static final int Z=2;

    public static int OFFSET=10; // distance in pixels for the graphics rendering

	/** number of edges */
    public int getSize() {
        return 0;
    }

    /** given the connection between this and the neighboor is:
      * @param edge
     * @return the symmetric connection
     */
    public int symmetricEdge(int edge) {
//        assert(false);
         throw new PuzzleException("invalid edge " + edge);
        // --edge; // suppress warning
    }

    public final static int size = 0;

    /** returns the name of the edge: LEFT/ FRONT... */
	public String stringValue(int edge) {
//        assert(false);
        throw new PuzzleException("invalid edge " + edge);
        // return Integer.toString(edge);
    }

    /** location of the edge in the 3D space, for example: (0,0,0) with LEFT returns (-10,0,0) */
	public void edgeOffset(int edge, Double dims[]) { // GraphIt::dfsPlot
        throw new PuzzleException("invalid edge " + edge);
    }

    /** determines the order for tried puts, the smaller, the former to put */
    public int mark(int edge) {
//        assert(false);
        throw new PuzzleException("invalid edge " + edge);
        // -- edge;
        // return -Integer.MAX_VALUE;
    }

}
