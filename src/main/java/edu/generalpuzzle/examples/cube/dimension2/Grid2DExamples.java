package edu.generalpuzzle.examples.cube.dimension2;

import edu.generalpuzzle.infra.ICellPart;
import edu.generalpuzzle.infra.CellId;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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

    public void buildFromFile() { // TODO align to the JS version, auto-detects, etc.
        grid.clear();
        currCell = null;
        leftCells = -1;

        try {
            // jdk8 -> Paths.get
            List<String> lines = Files.readAllLines(Path.of("myPzl.txt")); // todo another argument
            int foundCells = 0;
            String header = lines.get(0);
            if (!header.startsWith("#")) {
                System.err.println("Found no grid header");
            }
            int declardColumns = Integer.parseInt(header.substring(1).split(",")[0]);
            int declardRows= Integer.parseInt(header.substring(1).split(",")[1]);
            for (int row = 0; row < lines.size(); ++row) {
                boolean cellFound = false;
                String line = lines.get(row+1);
                int col;
                for (col = 0; col < line.length(); ++col) {
                    if (line.charAt(col) == 'X' || line.charAt(col) == 'x') {
                        ICellPart cell = new CellPart2D(new CellId(calcId(row, col)));
                        add(new CellId(calcId(row, col)), cell);
                        cellFound = true;
                        ++foundCells;
                    } else if (line.charAt(col) != ' ') {
                        System.err.println("Invalid char");
                    }
                }
                columns = Math.max(columns, col);
                rows = row + 1;
                if (!cellFound) {
                    break;
                }
            }
            rows -= 1;
            currCell = cells.get(0);
            if (rows != declardRows || columns != declardColumns) {
                System.err.println("Declared rows/columns misconfig");
            }
            System.out.println("Found " + (rows) + " rows, " + columns + " cols, with total of cells " + foundCells);
            int edges = 0;

            for (int row = 0; row < rows; ++row) {
                for (int col = 0; col < columns; ++col) {
                    ICellPart cell = grid.get(new CellId(calcId(row, col)));
                    if (cell != null) {
                        if (grid.get(new CellId(calcId(row - 1, col))) != null) {
                            addAndValidate(cell, Edge2D.UP, calcId(row - 1, col));
                            ++edges;
                        }
                        if (grid.get(new CellId(calcId(row, col - 1))) != null) {
                            addAndValidate(cell, Edge2D.LEFT, calcId(row, col - 1));
                            ++edges;
                        }
                        if (grid.get(new CellId(calcId(row, col + 1))) != null) {
                            addAndValidate(cell, Edge2D.RIGHT, calcId(row, col + 1));
                            ++edges;
                        }
                        if (grid.get(new CellId(calcId(row + 1, col))) != null) {
                            addAndValidate(cell, Edge2D.DOWN, calcId(row + 1, col));
                            ++edges;
                        }
                    }
                }
            }
            System.out.println("Found " + edges/2 + " edges");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
