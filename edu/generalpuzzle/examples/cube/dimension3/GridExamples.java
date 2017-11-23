package edu.generalpuzzle.examples.cube.dimension3;

import edu.generalpuzzle.infra.CellId;

/**
 * Created by IntelliJ IDEA.
 * Date: 27/06/2008
 */
public class GridExamples extends Grid3D { // 10/7 mail

    public void buildCase3() { // whole stairs
        build3d(5,4,5);
        for (int plane=0; plane<height; ++plane)
            for (int y=0; y<plane; ++y)
                for (int x=0; x<columns; ++x) {
                    remove(new CellId(calcId(y,x,plane)));
                }
    }

    public void buildCase4() { // buildHole (strip)
        build3d(9,3,3);
        for (int row=1; row<rows-1; ++row)
            for (int col=1; col<columns-1; ++col)
                for (int z=0; z<height; ++z) {
                    remove(new CellId(calcId(row,col,z)));
                }
    }

    public void buildCase5() { // big T 
        build3d(6,6,3);
        for (int row=0; row<rows; ++row)
            if (row != 2 && row != 3)
                for (int col=2; col<columns; ++col)
                    for (int z=0; z<height; ++z) {
                        remove(new CellId(calcId(row,col,z)));
                    }
    }

    public void buildCase6() { // big "KAF"
        build3d(4,6,3);
        for (int plane=0; plane<height; ++plane)
            for (int y=2; y<=3; ++y)
                for (int x=2; x<=3; ++x) {
                    remove(new CellId(calcId(y,x,plane)));
                }
    }

    public void buildCase7() { // big Y
        build3d(4,8,3);
        for (int row=2; row<rows; ++row)
            for (int col=0; col<columns; ++col)
                if (col != 4 && col != 5)
                    for (int z=0; z<height; ++z) {
                        remove(new CellId(calcId(row,col,z)));
                    }
    }

    public void buildCase8() { // stairs - only by BT
        build3d(5,5,5);
        for (int z=0; z<height; ++z) {
            // remove top lines
            for (int row=0; row<z; row++)
                for (int col=0; col<columns; ++col) {
                    remove(new CellId(calcId(row,col,z)));
                }
            // remove bottom lines
            for (int row=z; row<rows; ++row)
                for (int col=row+1; col<columns; ++col) {
                    remove(new CellId(calcId(row,col,z)));
                }
        }
    }
}
