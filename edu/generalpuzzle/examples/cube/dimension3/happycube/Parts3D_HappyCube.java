package edu.generalpuzzle.examples.cube.dimension3.happycube;

import edu.generalpuzzle.examples.cube.dimension3.Edge3D;
import edu.generalpuzzle.examples.cube.dimension3.Part3D;
import edu.generalpuzzle.infra.Parts;
import edu.generalpuzzle.infra.IPart;
import edu.generalpuzzle.infra.IGrid;

/**
 * Created by IntelliJ IDEA.
 * Date: 27/06/2008
 */
public class Parts3D_HappyCube extends Parts {

    public Parts3D_HappyCube(int unique) {
        super(unique);
    }

    @Override
    public IPart getGridPart() {
        return new Part3D(IGrid.GRID_ID);
    }

    private Part3D create(char id, String side[]) {
        Part3D part;

        part = new Part3D(id);

        int perimeter = 0;
        for (String s:side)
            for (int i=0; i<4; i++)
                if (s.charAt(i) == '1')
                    perimeter++;

        part.prepareRotations(perimeter + 3*3);

        int g[][] = new int[5][5]; // new indexes

        for (int row=0; row<5; ++row)
            for (int col=0; col<5; ++col)
                g[row][col] = -1;

        int cells= 0;

        for (int row=1; row<4;row++)
            for (int col=1; col<4;col++)
                g[row][col] = cells++;

        for (int j=0; j<4; j++)
            if (side[0].charAt(j) == '1')
                if (g[0][j] != -1) System.out.println("twice0"); else
                g[0][j] = cells++;

        for (int j=0; j<4; j++)
            if (side[1].charAt(j) == '1')
                if (g[j][4] != -1) System.out.println("twice1"); else
                g[j][4] = cells++;

        for (int j=0; j<4; j++)
            if (side[2].charAt(j) == '1')
                if (g[4][4-j] != -1) System.out.println("twice2"); else
                g[4][4-j] = cells++;

        for (int j=0; j<4; j++)
            if (side[3].charAt(j) == '1')
                if (g[4-j][0] != -1) System.out.println("twice3"); else
                g[4-j][0] = cells++;

        for (int row=0; row<5; ++row)
            for (int col=0; col<5; ++col) {
                if (g[row][col] != -1) {
                    if (row>0) if (g[row-1][col] != -1)
                        part.addEdgeMono(id, g[row][col], Edge3D.UP, g[row-1][col]);
                    if (col>0) if (g[row][col-1] != -1)
                        part.addEdgeMono(id, g[row][col], Edge3D.LEFT, g[row][col-1]);
                    if (col<5-1) if (g[row][col+1] != -1)
                        part.addEdgeMono(id, g[row][col], Edge3D.RIGHT, g[row][col+1]);
                    if (row<5-1) if (g[row+1][col] != -1)
                        part.addEdgeMono(id, g[row][col], Edge3D.DOWN, g[row+1][col]);
                }
            }

        return part;
    }

    public void buildGreen() {
        parts.clear();
        totalFill = 0;

        add(create('A', new String[]{"0101", "0010", "0101", "0101"}));
        add(create('B', new String[]{"0010", "0101", "0010", "0101"}));
        add(create('C', new String[]{"1101", "1010", "1101", "0101"}));
        add(create('D', new String[]{"1010", "0010", "0101", "1101"}));
        add(create('E', new String[]{"1101", "1010", "0010", "0010"}));
        add(create('F', new String[]{"1101", "0010","0010", "0010", }));

    }

    public void buildRed() {
        parts.clear();
        totalFill = 0;

        add(create('A', new String[]{"0010", "0010", "0101", "0010"}));
        add(create('B', new String[]{"0010", "0001", "0101", "0101"}));
        add(create('C', new String[]{"0101", "1001", "1101", "1011"}));
        add(create('D', new String[]{"0010", "0001","1010", "1101" }));
        add(create('E', new String[]{"0011", "0010", "1101", "1010"}));
        add(create('F', new String[]{"0101", "0010", "1101", "0011"}));
    }

    public void buildOrange() {
        parts.clear();
        totalFill = 0;

        add(create('A', new String[]{"0101", "0010", "1101", "0101"}));
        add(create('B', new String[]{"1010", "0101", "1010", "0101"}));
        add(create('C', new String[]{"0101", "0010", "0101", "0010"}));
        add(create('D', new String[]{"1101", "1010", "0010", "0101"}));
        add(create('E', new String[]{"1101", "0010", "0010", "0010"}));
        add(create('F', new String[]{"1101", "1010","0101", "0010", }));

    }
}


