package edu.generalpuzzle.examples.cube;

import edu.generalpuzzle.examples.cube.dimension3.Edge3D;
import edu.generalpuzzle.infra.IPart;
import edu.generalpuzzle.infra.Parts;

/**
 * Created by IntelliJ IDEA.
 * Date: 16/06/2008
 */
public class Parts_Poly5 extends Parts {

    // in Knuth's DLX paper XIVTUSWPLRYZ
    // us XIVTU?WPLFYZ TODO S=?
    
    /** returns the poliomino named part (from XIVTUSWPLRYZ) */
	public static IPart poly(IPart part) {

        part.prepareRotations(5);

        switch (part.getId()) {
        case 'L':
            // L
            // X
            // X
            // X
            // X X
            part.addEdge(1, Edge3D.DOWN, 2);
            part.addEdge(2, Edge3D.DOWN, 3);
            part.addEdge(3, Edge3D.DOWN, 4);
            part.addEdge(4, Edge3D.RIGHT, 5);
            break;
        case 'U':
            // U
            // X X X
            // X    X
            part.addEdge(1, Edge3D.RIGHT, 2);
            part.addEdge(1, Edge3D.DOWN, 3);
            part.addEdge(2, Edge3D.RIGHT, 4);
            part.addEdge(4, Edge3D.DOWN, 5);
            break;
        case 'F':
            // our unique creator
            // F
            //    X X
            // X X
            //    X
            part.addEdge(1, Edge3D.RIGHT, 2);
            part.addEdge(1, Edge3D.DOWN, 4);
            part.addEdge(3, Edge3D.RIGHT, 4);
            part.addEdge(4, Edge3D.DOWN, 5);
//            part.putKey(5, Edge3D.DOWN, 1);
            break;
        case 'X':
            // X
            //     X
            // X X X
            //   X
            part.addEdge(3, Edge3D.RIGHT, 4);
            part.addEdge(3, Edge3D.LEFT, 2);
//            part.putKey(1, Edge3D.UP,  -1);
            part.addEdge(3, Edge3D.DOWN, 5);
            part.addEdge(3, Edge3D.UP, 1);
            break;
        case 'Y':
            // Y
            // X X X X
            //       X
            part.addEdge(1, Edge3D.RIGHT, 2);
            part.addEdge(2, Edge3D.RIGHT, 3);
            part.addEdge(3, Edge3D.RIGHT, 4);
            part.addEdge(5, Edge3D.UP, 3);
            break;
        case 'N':
            // N
            //    X
            // X X
            // X
            // X
            part.addEdge(1, Edge3D.DOWN, 3);
            part.addEdge(2, Edge3D.RIGHT, 3);
            part.addEdge(2, Edge3D.DOWN, 4);
            part.addEdge(4, Edge3D.DOWN, 5);
            break;
        case 'W':
            // W
            // _ _ X
            // _ X X
            // X X
            part.addEdge(1, Edge3D.DOWN, 3);
            part.addEdge(2, Edge3D.RIGHT, 3);
            part.addEdge(2, Edge3D.DOWN, 5);
            part.addEdge(4, Edge3D.RIGHT, 5);
            break;
        case 'P':
            // P
            // X
            // X X
            // X X
           part.addEdge(2, Edge3D.RIGHT, 3);
           part.addEdge(4, Edge3D.RIGHT, 5);
//           part.addEdge(3, Edge3D.DOWN, 5);
           part.addEdge(2, Edge3D.DOWN, 4);
           part.addEdge(1, Edge3D.DOWN, 2);
            break;
        case 'Z':
            // Z
            // _ _  X
            // X X X
            // X
            part.addEdge(1, Edge3D.DOWN, 4);
            part.addEdge(2, Edge3D.RIGHT, 3);
            part.addEdge(3, Edge3D.RIGHT, 4);
            part.addEdge(2, Edge3D.DOWN, 5);
            break;
        case 'V':
            // V
            // _ _  X
            // _ _  X
            // X X X
//        part.setRotations(4);
//        part.setSymmetric(1);
            part.addEdge(1, Edge3D.DOWN, 2);
            part.addEdge(2, Edge3D.DOWN, 3);
            part.addEdge(3, Edge3D.LEFT, 4);
            part.addEdge(4, Edge3D.LEFT, 5);
            break;
        case 'T':
            // T
            // _ _  X
            // X X X
            // _ _  X
            part.addEdge(1, Edge3D.RIGHT, 2);
            part.addEdge(2, Edge3D.RIGHT, 3);
            part.addEdge(3, Edge3D.UP, 4);
            part.addEdge(3, Edge3D.DOWN, 5);
            break;
        case 'I':
            // I
            // X X X X X
            part.addEdge(1, Edge3D.RIGHT, 2);
            part.addEdge(2, Edge3D.RIGHT, 3);
            part.addEdge(3, Edge3D.RIGHT, 4);
            part.addEdge(4, Edge3D.RIGHT, 5);
            break;
        case 'A':
            // X X
            // X X
            part.prepareRotations(4);
            part.addEdge(1, Edge3D.RIGHT, 2);
            part.addEdge(1, Edge3D.DOWN, 3);
            part.addEdge(4, Edge3D.LEFT, 3);
            part.addEdge(4, Edge3D.UP, 2);
            break;
        case 'a':
            // X X
            // X X
            part.prepareRotations(3);
            part.addEdge(1, Edge3D.RIGHT, 2);
            part.addEdge(1, Edge3D.DOWN, 3);
            break;
        default:
            System.out.println("no valid poly5 part");
            return null;
        }

        return part;
    }

}
