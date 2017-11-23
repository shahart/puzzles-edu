package edu.generalpuzzle.examples.cube.dimension3;

import edu.generalpuzzle.infra.IGrid;
import edu.generalpuzzle.infra.IPart;
import edu.generalpuzzle.infra.Parts;

/**
 * Created by IntelliJ IDEA.
 * Date: 11/09/2008
 */
public class Parts3D_Examples extends Parts {

    public Parts3D_Examples(int unique) {
        super(unique);
    }

    public Parts3D_Examples() {
        super();
    }

    @Override
    public IPart getGridPart() {
        return new Part3D(IGrid.GRID_ID);
    }

    public void buildLiveCube() {
        parts.clear();
        totalFill = 0;

        Part3D part;

        for (int i=0; i<8; i++) {
            part = new Part3D((char)('A' + i)); //i == 0 ? 'A' : 'H');
            part.prepareRotations(8);
            part.addEdge(1, Edge3D.DOWN, 2);
            part.addEdge(2, Edge3D.FRONT, 3);
            part.addEdge(3, Edge3D.DOWN, 4);
            part.addEdge(4, Edge3D.RIGHT, 5);
            part.addEdge(5, Edge3D.RIGHT, 6);
            part.addEdge(6, Edge3D.BACK, 7);
            part.addEdge(7, Edge3D.UP, 8);
//            if (i>0)
//                part.anotherOne(i-1);
            add(part);
        }
//        anotherOne(7);
    }

    public void build_Bedlam() {

        parts.clear();
        totalFill = 0;

        Part3D part;

        part = new Part3D('A');
        part.prepareRotations(5);
        part.addEdge(1, Edge3D.RIGHT, 2);
        part.addEdge(2, Edge3D.RIGHT, 3);
        part.addEdge(3, Edge3D.UP, 4);
        part.addEdge(2, Edge3D.DOWN, 5);

        add(part);

        part = new Part3D('B');
        part.prepareRotations(5);
        part.addEdge(3, Edge3D.RIGHT, 4);
        part.addEdge(3, Edge3D.LEFT, 2);
        part.addEdge(3, Edge3D.DOWN, 5);
        part.addEdge(3, Edge3D.UP, 1);

        add(part);

        part = new Part3D('C');
        part.prepareRotations(5);
        part.addEdge(1, Edge3D.DOWN, 3);
        part.addEdge(2, Edge3D.RIGHT, 3);
        part.addEdge(2, Edge3D.DOWN, 5);
        part.addEdge(4, Edge3D.RIGHT, 5);

        add(part);

        part = new Part3D('D');
        part.prepareRotations(5);
        part.addEdge(1, Edge3D.RIGHT, 2);
        part.addEdge(2, Edge3D.DOWN, 3);
        part.addEdge(1, Edge3D.UP, 4);
        part.addEdge(4, Edge3D.FRONT, 5);

        add(part);

        part = new Part3D('E');
        part.prepareRotations(5);
        part.addEdge(2, Edge3D.RIGHT, 3);
        part.addEdge(2, Edge3D.FRONT, 5);
        part.addEdge(2, Edge3D.UP,1);
        part.addEdge(2, Edge3D.DOWN, 4);

        add(part);

        part = new Part3D('F');
        part.prepareRotations(5);
        part.addEdge(1, Edge3D.DOWN, 2);
        part.addEdge(2, Edge3D.FRONT, 3);
        part.addEdge(2, Edge3D.RIGHT, 4);
        part.addEdge(4, Edge3D.DOWN, 5);

        add(part);

        part = new Part3D('G');
        part.prepareRotations(5);
        part.addEdge(1, Edge3D.DOWN, 2);
        part.addEdge(2, Edge3D.DOWN, 3);
        part.addEdge(2, Edge3D.FRONT, 4);
        part.addEdge(4, Edge3D.RIGHT, 5);

        add(part);

        part = new Part3D('H');
        part.prepareRotations(5);
        part.addEdge(1, Edge3D.DOWN, 2);
        part.addEdge(2, Edge3D.DOWN, 3);
        part.addEdge(3, Edge3D.RIGHT, 4);
        part.addEdge(3, Edge3D.FRONT, 5);

        add(part);

        part = new Part3D('I');
        part.prepareRotations(5);
        part.addEdge(1, Edge3D.DOWN, 2);
        part.addEdge(2, Edge3D.DOWN, 3);
        part.addEdge(1, Edge3D.FRONT, 4);
        part.addEdge(3, Edge3D.RIGHT, 5);

        add(part);

        part = new Part3D('J');
        part.prepareRotations(5);
        part.addEdge(1, Edge3D.DOWN, 2);
        part.addEdge(2, Edge3D.DOWN, 3);
        part.addEdge(1, Edge3D.RIGHT, 4);
        part.addEdge(4, Edge3D.FRONT, 5);

        add(part);

        part = new Part3D('K');
        part.prepareRotations(5);
        part.addEdge(1, Edge3D.DOWN, 2);
        part.addEdge(2, Edge3D.BACK, 3);
        part.addEdge(3, Edge3D.RIGHT, 4);
        part.addEdge(4, Edge3D.DOWN, 5);

        add(part);

        part = new Part3D('L');
        part.prepareRotations(5);
        part.addEdge(1, Edge3D.DOWN, 2);
        part.addEdge(2, Edge3D.DOWN, 3);
        part.addEdge(3, Edge3D.RIGHT, 4);
        part.addEdge(2, Edge3D.FRONT, 5);

        add(part);

        part = new Part3D('M');
        part.prepareRotations(4);
        part.addEdge(1, Edge3D.DOWN, 2);
//        part.addEdge(2, Edge3D.DOWN, 5);
        part.addEdge(2, Edge3D.RIGHT, 3);
        part.addEdge(3, Edge3D.FRONT, 4);

        add(part);

    }

    public void build_Graatsma() {

        parts.clear();
        totalFill = 0;

        Part3D part;

        for (int i=0; i< 3; i++) {
            part = new Part3D(('A'));
            part.anotherOne(i);
            part.prepareRotations(1);
            // TODO, invalid config, size of parts 33 vs. grid 27 as I wrote prepare(3) - identify this
            add(part);
        }
        anotherOne(3);

        // six 1x2x2
        for (int i=0; i< 6; i++) {
            part = new Part3D(('O'));
            part.anotherOne(i);
            part.prepareRotations(4);
            part.addEdge(1, Edge3D.RIGHT, 2);
            part.addEdge(1, Edge3D.DOWN, 3);
            part.addEdge(3, Edge3D.RIGHT, 4);
            // and must add
            part.addEdge(2, Edge3D.DOWN, 4);
            add(part);
        }
        anotherOne(6);

//        part = new Part3D(('L'));
//        part.prepareRotations(1);
//        add(part);

    }

    public void build_Soma() {

        parts.clear();
        totalFill = 0;

        Part3D part;

        part = new Part3D('L'); // V in cube puzzle.ppt
        part.prepareRotations(3);
        part.addEdge(1, Edge3D.RIGHT, 2);
        part.addEdge(1, Edge3D.DOWN, 3);

        add(part);

        part = new Part3D('T'); // T
        part.prepareRotations(4);
        part.addEdge(1, Edge3D.RIGHT, 2);
        part.addEdge(1, Edge3D.LEFT, 3);
        part.addEdge(1, Edge3D.DOWN, 4);

        add(part);

        part = new Part3D('M'); // L2 // L
        part.prepareRotations(4);
        part.addEdge(1, Edge3D.DOWN, 2);
        part.addEdge(1, Edge3D.RIGHT, 3);
        part.addEdge(3, Edge3D.RIGHT, 4);

        add(part);

        part = new Part3D('S'); // Z
        part.prepareRotations(4);
        part.addEdge(1, Edge3D.RIGHT, 2);
        part.addEdge(2, Edge3D.UP, 3);
        part.addEdge(3, Edge3D.RIGHT, 4);

        add(part);

        part = new Part3D('E'); // R
        part.prepareRotations(4);
        part.addEdge(1, Edge3D.DOWN, 2);
        part.addEdge(1, Edge3D.RIGHT, 3);
        part.addEdge(3, Edge3D.FRONT,4);

        add(part);

        part = new Part3D('F'); // S
        part.prepareRotations(4);
        part.addEdge(1, Edge3D.DOWN, 2);
        part.addEdge(2, Edge3D.FRONT, 3);
        part.addEdge(1, Edge3D.RIGHT, 4);

        add(part);

        part = new Part3D('G'); // Y
        part.prepareRotations(4);
        part.addEdge(1, Edge3D.DOWN, 2);
        part.addEdge(1, Edge3D.RIGHT, 3);
        part.addEdge(1, Edge3D.FRONT, 4);

        add(part);
    }

    public void buildGaya() {
        parts.clear();
        totalFill = 0;

        Part3D part;

        // L
        // X
        // X
        // X X

//        char Lname = 'L';
//        for (int i=0; i<6; i++) {
//            part = new Part3D(Lname);
//            ++ Lname;
//            part.setRotationsXY(4);
//            part.setRotationsXZ(4);
//            part.setRotationsYZ(4); // was 2
//            part.prepareRotations(2);
//            part.addEdge(1, Edge3D.DOWN, 2);
//
//            add(part);
//        }

        char Lname = 'L';
        int i;
        for (i=0; i<4; i++) { // should be 4
            part = new Part3D(Lname);
            part.anotherOne(i);
            part.prepareRotations(4);
            part.addEdge(1, Edge3D.DOWN, 2);
            part.addEdge(2, Edge3D.DOWN, 3);
            part.addEdge(3, Edge3D.RIGHT, 4);

            add(part);
        }
        anotherOne(i);

//        part = new Part3D('W'); // check for anotherOne test
//        part.prepareRotations(4);
//        part.addEdge(1, Edge3D.DOWN, 2);
//        part.addEdge(2, Edge3D.DOWN, 3);
//        part.addEdge(3, Edge3D.RIGHT, 4);
//        add(part);

        // X X
        // X
        part = new Part3D('A');
        part.prepareRotations(3);
        part.addEdge(1, Edge3D.RIGHT, 2);
        part.addEdge(1, Edge3D.DOWN, 3);

        add(part);

        // our NEW unique
        // X
        //       X
        // _ X X
        //   X
        part = new Part3D('X');
        // part.setReflective(0);
        // part.setRotations(1);
        part.prepareRotations(4);
        part.addEdge(3, Edge3D.RIGHT, 4);
        part.addEdge(3, Edge3D.DOWN, 2);
        part.addEdge(4, Edge3D.UP, 1);

        add(part);

        // our OLD unique creator
        // P, like plus
        //    X
        // X X
        //    X
        part = new Part3D('P');
        part.prepareRotations(4);
        part.addEdge(1, Edge3D.LEFT, 3);
        part.addEdge(1, Edge3D.DOWN, 4);
        part.addEdge(1, Edge3D.UP, 2);

        add(part);

    }

    public void buildSnake() {
        parts.clear();
        totalFill = 0;

        Part3D part;

        // A
        part = new Part3D('A');
        part.prepareRotations(12);
        part.addEdge(1, Edge3D.RIGHT, 2);
        part.addEdge(2, Edge3D.RIGHT, 3);
        part.addEdge(3, Edge3D.RIGHT, 4);
        part.addEdge(4, Edge3D.DOWN, 5);
        part.addEdge(5, Edge3D.DOWN, 6);
        part.addEdge(6, Edge3D.FRONT, 7);
        part.addEdge(7, Edge3D.FRONT, 8);
        part.addEdge(8, Edge3D.UP, 9);
        part.addEdge(9, Edge3D.LEFT ,10);
        part.addEdge(10, Edge3D.LEFT ,11);
        part.addEdge(11, Edge3D.LEFT, 12);

        add(part);

        // B
        part = new Part3D('B');
        part.prepareRotations(14);
        part.addEdge(1, Edge3D.RIGHT, 2);
        part.addEdge(2, Edge3D.RIGHT, 3);
        part.addEdge(3, Edge3D.RIGHT, 4);
        part.addEdge(4, Edge3D.DOWN, 5);
        part.addEdge(5, Edge3D.DOWN, 6);
        part.addEdge(1, Edge3D.DOWN, 7);
        part.addEdge(7, Edge3D.DOWN, 8);
        part.addEdge(8, Edge3D.RIGHT, 9);
        part.addEdge(9, Edge3D.FRONT ,10);
        part.addEdge(10, Edge3D.FRONT ,11);
        part.addEdge(11, Edge3D.LEFT, 12);
        part.addEdge(12, Edge3D.FRONT, 13);
        part.addEdge(13, Edge3D.UP, 14);

        add(part);

        // C
        part = new Part3D('C');
        part.prepareRotations(14);
        part.addEdge(1, Edge3D.RIGHT, 2);
        part.addEdge(2, Edge3D.RIGHT, 3);
        part.addEdge(3, Edge3D.RIGHT, 4);
        part.addEdge(4, Edge3D.FRONT, 5);
        part.addEdge(1, Edge3D.FRONT, 6);
        part.addEdge(6, Edge3D.DOWN, 7);
        part.addEdge(7, Edge3D.DOWN, 8);
        part.addEdge(5, Edge3D.DOWN, 9);
        part.addEdge(9, Edge3D.RIGHT ,10);
        part.addEdge(10, Edge3D.DOWN ,11);
        part.addEdge(11, Edge3D.DOWN, 12);
        part.addEdge(12, Edge3D.LEFT ,13);
        part.addEdge(13, Edge3D.LEFT, 14);

        add(part);

        // D
        part = new Part3D('D');
        part.prepareRotations(14);
        part.addEdge(1, Edge3D.UP, 2);
        part.addEdge(2, Edge3D.LEFT, 3);
        part.addEdge(3, Edge3D.LEFT, 4);
        part.addEdge(4, Edge3D.LEFT, 5);
        part.addEdge(5, Edge3D.DOWN, 6);
        part.addEdge(6, Edge3D.DOWN, 7);
        part.addEdge(7, Edge3D.DOWN, 8);
        part.addEdge(8, Edge3D.LEFT, 9);
        part.addEdge(9, Edge3D.FRONT ,10);
        part.addEdge(10, Edge3D.FRONT ,11);
        part.addEdge(11, Edge3D.FRONT, 12); // TODO missing cell,
        part.addEdge(12, Edge3D.FRONT ,13);
        part.addEdge(13, Edge3D.RIGHT ,14);

        add(part);

        // E
        part = new Part3D('E');
        part.prepareRotations(15);
        part.addEdge(1, Edge3D.RIGHT, 2);
        part.addEdge(2, Edge3D.RIGHT, 3);
        part.addEdge(3, Edge3D.FRONT, 4);
        part.addEdge(4, Edge3D.FRONT, 5);
        part.addEdge(5, Edge3D.FRONT, 6);
        part.addEdge(6, Edge3D.RIGHT, 7);
        part.addEdge(7, Edge3D.RIGHT, 8);
        part.addEdge(8, Edge3D.BACK, 9);
        part.addEdge(9, Edge3D.BACK ,10);
        part.addEdge(10, Edge3D.BACK ,11);
        part.addEdge(11, Edge3D.DOWN, 12);
        part.addEdge(12, Edge3D.DOWN, 13);
        part.addEdge(13, Edge3D.DOWN, 14);
        part.addEdge(14, Edge3D.LEFT, 15);

        add(part);

        // F
        part = new Part3D('F');
        part.prepareRotations(13);
        part.addEdge(1, Edge3D.DOWN, 2);
        part.addEdge(2, Edge3D.RIGHT, 3);
        part.addEdge(1, Edge3D.FRONT, 4);
        part.addEdge(4, Edge3D.UP, 5);
        part.addEdge(5, Edge3D.LEFT, 6);
        part.addEdge(6, Edge3D.UP, 7);
        part.addEdge(7, Edge3D.LEFT, 8);
        part.addEdge(8, Edge3D.LEFT, 9);
        part.addEdge(9, Edge3D.DOWN ,10);
        part.addEdge(10, Edge3D.DOWN ,11);
        part.addEdge(11, Edge3D.FRONT, 12);
        part.addEdge(12, Edge3D.FRONT, 13);

        add(part);

        // G
        part = new Part3D('G');
        part.prepareRotations(16);
        part.addEdge(1, Edge3D.RIGHT, 2);
        part.addEdge(2, Edge3D.RIGHT, 3);
        part.addEdge(1, Edge3D.DOWN, 4);
        part.addEdge(4, Edge3D.DOWN, 5);
        part.addEdge(5, Edge3D.LEFT, 6);
        part.addEdge(6, Edge3D.DOWN, 7);
        part.addEdge(7, Edge3D.FRONT, 8);
        part.addEdge(8, Edge3D.FRONT, 9);
        part.addEdge(9, Edge3D.UP ,10);
        part.addEdge(10, Edge3D.LEFT ,11);
        part.addEdge(3, Edge3D.FRONT, 12);
        part.addEdge(12, Edge3D.FRONT, 13);
        part.addEdge(13, Edge3D.DOWN ,14);
        part.addEdge(14, Edge3D.DOWN ,15);
        part.addEdge(15, Edge3D.DOWN ,16);

        add(part);

        // H
        part = new Part3D('H');
        part.prepareRotations(13);
        part.addEdge(1, Edge3D.RIGHT, 2);
        part.addEdge(2, Edge3D.RIGHT, 3);
        part.addEdge(1, Edge3D.DOWN, 4);
        part.addEdge(4, Edge3D.DOWN, 5);
        part.addEdge(5, Edge3D.DOWN, 6);
        
        part.addEdge(6, Edge3D.FRONT, 7);
        part.addEdge(3, Edge3D.FRONT, 8);
        part.addEdge(7, Edge3D.FRONT, 10);
        part.addEdge(8, Edge3D.FRONT, 9);
        part.addEdge(9, Edge3D.RIGHT, 13);
        
        part.addEdge(10, Edge3D.UP ,11);
        part.addEdge(11, Edge3D.UP, 12);

        add(part);

        // I
        part = new Part3D('I');
        part.prepareRotations(14);
        part.addEdge(1, Edge3D.RIGHT, 2);
        part.addEdge(2, Edge3D.RIGHT, 3);
        part.addEdge(3, Edge3D.RIGHT, 4);
        part.addEdge(4, Edge3D.DOWN, 5);
        part.addEdge(5, Edge3D.RIGHT, 6);
        part.addEdge(1, Edge3D.FRONT, 7);
        part.addEdge(6, Edge3D.FRONT, 8);
        part.addEdge(8, Edge3D.FRONT, 9);
        part.addEdge(9, Edge3D.LEFT ,10);
        part.addEdge(10, Edge3D.DOWN,11);
        part.addEdge(11, Edge3D.DOWN, 12);
        part.addEdge(12, Edge3D.BACK ,13);
        part.addEdge(13, Edge3D.BACK ,14);

        add(part);

    }

    public void build_test() {

        parts.clear();
        totalFill = 0;

        Part3D part;

//        part = new Part3D((char)('D'));
//        part.prepareRotations(2);
//        part.addEdge(1, Edge3D.FRONT, 2); // TODO how it works? because rotations!
//        add(part);

        for (int i=0; i< 3; i++) {
            part = new Part3D((char)('A')); // A has 24 rotations!!! TODO and it's the unique!!
            part.anotherOne(i);
            part.prepareRotations(1);
            add(part);
        }
        anotherOne(3);

    }

    public void build_Conway() {

        parts.clear();
        totalFill = 0;

        Part3D part;

        for (int i=0; i< 13; i++) {
            part = new Part3D(('A'));
            part.anotherOne(i);
            part.prepareRotations(8);
            part.addEdge(1, Edge3D.DOWN, 2);
            part.addEdge(2, Edge3D.DOWN, 3);
            part.addEdge(3, Edge3D.DOWN, 4);
            part.addEdge(1, Edge3D.RIGHT, 5);
            part.addEdge(2, Edge3D.RIGHT, 6);
            part.addEdge(3, Edge3D.RIGHT, 7);
            part.addEdge(4, Edge3D.RIGHT, 8);
            add(part);
        }
        anotherOne(3);

        for (int i=0; i< 3; i++) {
            part = new Part3D(('N'));
            part.anotherOne(i);
            part.prepareRotations(3);
            part.addEdge(1, Edge3D.DOWN, 2);
            part.addEdge(2, Edge3D.DOWN, 3);
            add(part);
        }
        anotherOne(3);

        part = new Part3D('Q');
        part.prepareRotations(4);
        part.addEdge(1, Edge3D.DOWN, 3);
        part.addEdge(1, Edge3D.RIGHT, 2);
        part.addEdge(3, Edge3D.RIGHT, 4);
        add(part);

        part = new Part3D('R');
        part.prepareRotations(8);
        part.addEdge(1, Edge3D.DOWN, 3);
        part.addEdge(1, Edge3D.RIGHT, 2);
        part.addEdge(3, Edge3D.RIGHT, 4);
        part.addEdge(1, Edge3D.FRONT, 5);
        part.addEdge(5, Edge3D.RIGHT, 6);
        part.addEdge(5, Edge3D.DOWN, 7);
        part.addEdge(7, Edge3D.RIGHT, 8);
        add(part);

    }

    public void build_pairs() { // patio block- 4x4x5 http://www.johnrausch.com/PuzzlingWorld/chap03f.htm should be 25 solutions

        parts.clear();
        totalFill = 0;

        Part3D part;

        part = new Part3D('A'); // bottom right
        part.prepareRotations(8);
        part.addEdge(1, Edge3D.DOWN, 2);
        part.addEdge(2, Edge3D.DOWN, 3);
        part.addEdge(3, Edge3D.DOWN, 4);
        part.addEdge(1, Edge3D.RIGHT, 5);
        part.addEdge(2, Edge3D.RIGHT, 6);
        part.addEdge(3, Edge3D.RIGHT, 7);
        part.addEdge(4, Edge3D.RIGHT, 8);

        part.addEdge(5, Edge3D.DOWN, 6); // unnecessary connection, unless you want to make it a part with 1 rotation
        part.addEdge(6, Edge3D.DOWN, 7); // "
        part.addEdge(7, Edge3D.DOWN, 8); // "

        add(part);

        part = new Part3D('B'); // top right
        part.prepareRotations(8);
        part.addEdge(1, Edge3D.DOWN, 3);
        part.addEdge(1, Edge3D.RIGHT, 2);
        part.addEdge(3, Edge3D.RIGHT, 4);
        part.addEdge(2, Edge3D.DOWN, 4);

        part.addEdge(1, Edge3D.BACK, 5);

        part.addEdge(5, Edge3D.RIGHT, 6);
        part.addEdge(5, Edge3D.DOWN, 7);
        part.addEdge(7, Edge3D.RIGHT, 8);
        part.addEdge(6, Edge3D.DOWN, 8);

        part.addEdge(2, Edge3D.BACK, 6);
        part.addEdge(3, Edge3D.BACK, 7);
        part.addEdge(4, Edge3D.BACK, 8);

        add(part);

        part = new Part3D('C'); // top left
        part.prepareRotations(8);
        part.addEdge(1, Edge3D.DOWN, 2);
        part.addEdge(2, Edge3D.DOWN, 3);
        part.addEdge(1, Edge3D.BACK, 4);
        part.addEdge(2, Edge3D.BACK, 5);
        part.addEdge(3, Edge3D.RIGHT, 6);
        part.addEdge(3, Edge3D.FRONT, 7);
        part.addEdge(7, Edge3D.RIGHT, 8);
        add(part);

        part = new Part3D('D');
        part.prepareRotations(8);
        part.addEdge(1, Edge3D.DOWN, 3);
        part.addEdge(1, Edge3D.RIGHT, 2);
        part.addEdge(3, Edge3D.RIGHT, 4);
        part.addEdge(4, Edge3D.DOWN, 5);
        part.addEdge(5, Edge3D.RIGHT, 6);
        part.addEdge(5, Edge3D.FRONT, 7);
        part.addEdge(7, Edge3D.RIGHT, 8);
        add(part);

        part = new Part3D('E');
        part.prepareRotations(8);
        part.addEdge(1, Edge3D.BACK, 2);
        part.addEdge(1, Edge3D.DOWN, 3);
        part.addEdge(3, Edge3D.BACK, 4);
        part.addEdge(4, Edge3D.DOWN, 5);
        part.addEdge(5, Edge3D.DOWN, 6);
        part.addEdge(5, Edge3D.RIGHT, 7);
        part.addEdge(6, Edge3D.RIGHT, 8);
        add(part);

        part = new Part3D('F');
        part.prepareRotations(8);
        part.addEdge(1, Edge3D.DOWN, 3);
        part.addEdge(1, Edge3D.RIGHT, 2);
        part.addEdge(3, Edge3D.RIGHT, 4);
        part.addEdge(3, Edge3D.DOWN, 5);
        part.addEdge(5, Edge3D.DOWN, 7);
        part.addEdge(5, Edge3D.FRONT, 6);
        part.addEdge(7, Edge3D.FRONT, 8);
        add(part);

        part = new Part3D('G'); // bottom left
        part.prepareRotations(8);
        part.addEdge(1, Edge3D.DOWN, 3);
        part.addEdge(1, Edge3D.BACK, 2);
        part.addEdge(2, Edge3D.DOWN, 4);
        part.addEdge(3, Edge3D.BACK, 4);
        part.addEdge(3, Edge3D.DOWN, 5);
        part.addEdge(4, Edge3D.DOWN, 6);
        part.addEdge(5, Edge3D.RIGHT, 7);
        part.addEdge(6, Edge3D.RIGHT, 8);
        part.addEdge(7, Edge3D.BACK, 8);
        add(part);

        part = new Part3D('H');
        part.prepareRotations(8);
        part.addEdge(1, Edge3D.DOWN, 3);
        part.addEdge(1, Edge3D.RIGHT, 2);
        part.addEdge(3, Edge3D.RIGHT, 4);
        part.addEdge(4, Edge3D.DOWN, 5);
        part.addEdge(5, Edge3D.RIGHT, 6);
        part.addEdge(5, Edge3D.DOWN, 7);
        part.addEdge(7, Edge3D.RIGHT, 8);
        add(part);

        part = new Part3D('I');
        part.prepareRotations(8);
        part.addEdge(1, Edge3D.DOWN, 3);
        part.addEdge(1, Edge3D.RIGHT, 2);
        part.addEdge(3, Edge3D.RIGHT, 4);
        part.addEdge(5, Edge3D.RIGHT, 6);
        part.addEdge(5, Edge3D.DOWN, 7);
        part.addEdge(7, Edge3D.RIGHT, 8);
        part.addEdge(3, Edge3D.BACK, 6);
        add(part);

        part = new Part3D('J');
        part.prepareRotations(8);
        part.addEdge(1, Edge3D.DOWN, 3);
        part.addEdge(1, Edge3D.RIGHT, 2); 
        part.addEdge(3, Edge3D.RIGHT, 4);
        part.addEdge(5, Edge3D.RIGHT, 6);
        part.addEdge(5, Edge3D.DOWN, 7);
        part.addEdge(7, Edge3D.RIGHT, 8);
        part.addEdge(3, Edge3D.BACK, 5);
        add(part);


    }
}
