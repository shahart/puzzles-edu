package edu.generalpuzzle.examples.cube.dimension2;

import edu.generalpuzzle.infra.ICellPart;
import edu.generalpuzzle.infra.CellId;

/**
 * Created by IntelliJ IDEA.
 * Date: 18/04/2009
 */
public class Grid2DExamples extends Grid2D {

    public void buildCheckers() {
        grid.clear();
        currCell = null;
        leftCells = -1;

        this.rows = 5;
        this.columns = 5;

        for (int row=0; row<rows; ++row)
            for (int col=0; col<columns; ++col) {
                ICellPart cell = new CellPart2D(new CellId(calcId(row,col)));
                add(new CellId(calcId(row,col)), cell);
            }

        currCell = cells.get(0);

        for (int row=0; row<rows; ++row)
            for (int col=0; col<columns; ++col) {
                ICellPart cell = grid.get(new CellId(calcId(row,col)));
                addAndValidate(cell, Edge2D.UP, calcId(row-1, col));
                addAndValidate(cell, Edge2D.LEFT, calcId(row, col-1));
                addAndValidate(cell, Edge2D.RIGHT, calcId(row, col+1));
                addAndValidate(cell, Edge2D.DOWN, calcId(row+1, col));
            }

        for (int row=0; row<rows; ++row)
            for (int col=0; col<columns; ++col)
                if ((row+col) % 2 == 0)
                    grid.get(new CellId(calcId(row,col))).setSpecial(1);

    }


}
