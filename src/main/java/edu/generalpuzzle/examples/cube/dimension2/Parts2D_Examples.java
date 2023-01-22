package edu.generalpuzzle.examples.cube.dimension2;

import edu.generalpuzzle.examples.cube.dimension3.Edge3D;
import edu.generalpuzzle.infra.*;
import edu.generalpuzzle.examples.cube.Parts_Poly5;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 16/04/2009
 */
public class Parts2D_Examples extends Parts {

    public Parts2D_Examples(int unique) {
        super(unique);
    }

    public Parts2D_Examples() {
        super();
    }

    @Override
    public IPart getGridPart() {
        return new Part2D(IGrid.GRID_ID);
    }

    public void build_Star() { // grid 4x4

        parts.clear();
        totalFill = 0;

        Part2D part;

        part = new Part2D('A');
        part.prepareRotations(1);
        part.putKey(1, Edge2D.RIGHT, 1);
        part.putKey(1, Edge2D.DOWN, 1);
        part.putKey(1, Edge2D.LEFT, 1); // outer boundary
        part.putKey(1, Edge2D.UP, 1); //
        add(part);

        part = new Part2D('B');
        part.prepareRotations(1);
        part.putKey(1, Edge2D.DOWN, 1);
        part.putKey(1, Edge2D.LEFT, -1);
        part.putKey(1, Edge2D.UP, 1); //
        add(part);

        part = new Part2D('C');
        part.prepareRotations(1);
        part.putKey(1, Edge2D.DOWN, -1);
        part.putKey(1, Edge2D.UP, 1); //
        add(part);

        part = new Part2D('D');
        part.prepareRotations(1);
        part.putKey(1, Edge2D.RIGHT, 1); //
        part.putKey(1, Edge2D.UP, 1); //
        add(part);

        part = new Part2D('E');
        part.prepareRotations(1);
        part.putKey(1, Edge2D.RIGHT, -1);
        part.putKey(1, Edge2D.DOWN, -1);
        part.putKey(1, Edge2D.LEFT, 1); //
        part.putKey(1, Edge2D.UP, -1);
        add(part);

        part = new Part2D('F');
        part.prepareRotations(1);
        part.putKey(1, Edge2D.RIGHT, 1);
        part.putKey(1, Edge2D.DOWN, -1);
        part.putKey(1, Edge2D.LEFT, 1);
        part.putKey(1, Edge2D.UP, -1);
        add(part);

        part = new Part2D('G');
        part.prepareRotations(1);
        part.putKey(1, Edge2D.DOWN, -1);
        part.putKey(1, Edge2D.LEFT, -1);
        part.putKey(1, Edge2D.UP, 1);
        add(part);

        part = new Part2D('H');
        part.prepareRotations(1);
        part.putKey(1, Edge2D.RIGHT, 1); //
        add(part);

        part = new Part2D('I');
        part.prepareRotations(1);
        part.putKey(1, Edge2D.DOWN, -1);
        part.putKey(1, Edge2D.LEFT, 1); //
        part.putKey(1, Edge2D.UP, 1);
        add(part);

        part = new Part2D('J');
        part.prepareRotations(1);
        part.putKey(1, Edge2D.RIGHT, -1);
        part.putKey(1, Edge2D.UP, 1);
        add(part);

        part = new Part2D('K');
        part.prepareRotations(1);
        part.putKey(1, Edge2D.RIGHT, -1);
        part.putKey(1, Edge2D.DOWN, -1);
        part.putKey(1, Edge2D.LEFT, 1);
        part.putKey(1, Edge2D.UP, 1);
        add(part);

        part = new Part2D('L');
        part.prepareRotations(1);
        part.putKey(1, Edge2D.RIGHT, 1); //
        part.putKey(1, Edge2D.LEFT, 1);
        add(part);

        part = new Part2D('M');
        part.prepareRotations(1);
        part.putKey(1, Edge2D.RIGHT, 1);
        part.putKey(1, Edge2D.DOWN, 1); //
        part.putKey(1, Edge2D.LEFT, 1); //
        part.putKey(1, Edge2D.UP, 1);
        add(part);

        part = new Part2D('N');
        part.prepareRotations(1);
        part.putKey(1, Edge2D.RIGHT, -1);
        part.putKey(1, Edge2D.DOWN, 1);
        part.putKey(1, Edge2D.LEFT, -1);
        add(part);

        part = new Part2D('O');
        part.prepareRotations(1);
        part.putKey(1, Edge2D.DOWN, 1); //
        part.putKey(1, Edge2D.RIGHT, -1);
        part.putKey(1, Edge2D.LEFT, 1); 
        part.putKey(1, Edge2D.UP, 1); //
        add(part);

        part = new Part2D('P');
        part.prepareRotations(1);
        part.putKey(1, Edge2D.DOWN, 1);
        part.putKey(1, Edge2D.RIGHT, 1); //
        part.putKey(1, Edge2D.LEFT, 1);
        add(part);

        /// the frame

        part = new Part2D('Q');
        part.prepareRotations(20);

        part.addEdge(1, Edge2D.RIGHT, 2);
        part.addEdge(2, Edge2D.RIGHT, 3);
        part.addEdge(3, Edge2D.RIGHT, 4);
        part.addEdge(4, Edge2D.RIGHT, 5);
        part.addEdge(5, Edge2D.RIGHT, 6);

        part.addEdge(6, Edge2D.DOWN, 7);
        part.addEdge(7, Edge2D.DOWN, 8);
        part.addEdge(8, Edge2D.DOWN, 9);
        part.addEdge(9, Edge2D.DOWN, 10);
        part.addEdge(10, Edge2D.DOWN, 11);
        
        part.addEdge(11, Edge2D.LEFT, 12);
        part.addEdge(12, Edge2D.LEFT, 13);
        part.addEdge(13, Edge2D.LEFT, 14);
        part.addEdge(14, Edge2D.LEFT, 15);
        part.addEdge(15, Edge2D.LEFT, 16);

        part.addEdge(16, Edge2D.UP, 17);
        part.addEdge(17, Edge2D.UP, 18);
        part.addEdge(18, Edge2D.UP, 19);
        part.addEdge(19, Edge2D.UP, 20);
        part.addEdge(20, Edge2D.UP, 1);

        part.putKey(2, Edge2D.DOWN, -1);
        part.putKey(3, Edge2D.DOWN, -1);
        part.putKey(4, Edge2D.DOWN, -1);
        part.putKey(5, Edge2D.DOWN, -1);

        part.putKey(7, Edge2D.LEFT, -1);
        part.putKey(8, Edge2D.LEFT, -1);
        part.putKey(9, Edge2D.LEFT, -1);
        part.putKey(10, Edge2D.LEFT, -1);

        part.putKey(12, Edge2D.UP, -1);
        part.putKey(13, Edge2D.UP, -1);
        part.putKey(14, Edge2D.UP, -1);
        part.putKey(15, Edge2D.UP, -1);

        part.putKey(17, Edge2D.RIGHT, -1);
        part.putKey(18, Edge2D.RIGHT, -1);
        part.putKey(19, Edge2D.RIGHT, -1);
        part.putKey(20, Edge2D.RIGHT, -1);

        add(part);
    }

    public void build_Checkers() {

        parts.clear();
        totalFill = 0;

        Part2D part = new Part2D('A');
        part.prepareRotations(4);
        part.addEdge(1, Edge2D.RIGHT, 2);
        part.addEdge(3, Edge2D.RIGHT, 4);
        part.addEdge(1, Edge2D.DOWN, 3);
        part.addEdge(2, Edge2D.DOWN, 4);
        part.specialCell(1);
        part.specialCell(4);
        add(part);

        part = new Part2D('B');
        part.prepareRotations(4);
        part.addEdge(1, Edge2D.RIGHT, 2);
        part.addEdge(2, Edge2D.UP, 3);
        part.addEdge(2, Edge2D.DOWN, 4);
        part.specialCell(1);
        part.specialCell(4);
        part.specialCell(3);
        add(part);

        part = new Part2D('C');
        part.prepareRotations(3);
        part.addEdge(1, Edge2D.RIGHT, 2);
        part.addEdge(1, Edge2D.DOWN, 3);
        part.specialCell(1);
        add(part);

        part = (Part2D)Parts_Poly5.poly(new Part2D('N'));
        part.specialCell(1);
        part.specialCell(2);
        part.specialCell(5);
        add(part);

        part = (Part2D)Parts_Poly5.poly(new Part2D('T'));
        part.specialCell(1);
        part.specialCell(3);
        add(part);
        
        part = new Part2D('L');
        part.prepareRotations(4);
        part.addEdge(1, Edge2D.RIGHT, 2);
        part.addEdge(2, Edge2D.RIGHT, 3);
        part.addEdge(1, Edge2D.DOWN, 4);
        part.specialCell(1);
        part.specialCell(3);
        add(part);

    }

    public void buildFromFile() {
        parts.clear();
        totalFill = 0;

        try {
            List<String> lines = Files.readAllLines(Path.of("myPzl.txt"));
            Part2D part = null;
            int [][] partGrid = new int[9][9]; // for now we support max of 9 cells per Piece - charAt // todo
            int foundCells = 0;
            int i;
            // skip the grid
            for (i=0; i<lines.size(); ++i) {
                String line = lines.get(i);
                if (line.toLowerCase().startsWith("#piece")) {
                    break;
                }
            }
            int row = 0;
            for (; i<lines.size(); ++i) {
                String line = lines.get(i);
                if (line.toLowerCase().startsWith("#piece")) {
                    if (part != null) {
                        row = 0;
                        if (part.getCellsAmount() != foundCells) {
                            throw new IllegalStateException("error: invalid config, size of part " + foundCells + " vs. defined in file " + part.getCellsAmount());
                        }
                        foundCells = 0;
                        for (int r=0; r<8; r++) {
                            for (int c=0; c<8; c++) {
                                if (partGrid[r][c] > 0) {
                                    if (partGrid[r+1][c] > 0) {
                                        part.addEdge(partGrid[r][c], Edge3D.DOWN, partGrid[r+1][c]);
                                    }
                                    if (partGrid[r][c+1] > 0) {
                                        part.addEdge(partGrid[r][c], Edge3D.RIGHT, partGrid[r][c+1]);
                                    }
                                }
                            }
                        }
                        // todo alert if part is not fully connected
                        add(part);
                    }
                    if (line.toLowerCase().startsWith("#piece-end")) {
                        break;
                    }
                    partGrid = new int[9][9];
                    part = new Part2D(line.charAt("#Piece".length()));
                    part.prepareRotations(Integer.parseInt(line.substring("#Piece".length()+1)));
                    System.out.println("Parsing piece " + (char)part.getId());
                    if (part.getCellsAmount() == 0) {
                        throw new IllegalStateException("error: invalid config, size of part can't be zero");
                    }
                }
                else if (line.length() > 0) {
                    int col;
                    for (col = 0; col < line.length(); ++col) {
                        if (line.charAt(col) == 'X' || line.charAt(col) == 'x') {
                            partGrid[row][col] = ++ foundCells;
                        } else if (line.charAt(col) != ' ') {
                            System.err.println("Invalid char "+ line.charAt(col));
                        }
                    }
                    ++row;
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Found " + parts.size() + " parts");
        System.out.println("TotalFill " + totalFill);

        // we won't do a lot of validations, but at least the basic
        // this exists also later in the code
//        if (totalFill != getGridPart().getCellsAmount()) {
//            throw new IllegalStateException("error: invalid config, size of parts " + totalFill + " vs. grid " + getGridPart().getCellsAmount());
//        }

    }
}
