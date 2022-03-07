package edu.generalpuzzle.examples.pie;

import edu.generalpuzzle.infra.IGrid;
import edu.generalpuzzle.infra.IPart;
import edu.generalpuzzle.infra.Parts;

/**
 * Created by IntelliJ IDEA.
 * Date: 16/06/2008
 */
public class Parts_Pie extends Parts {

//     @SuppressWarnings({"SameParameterValue"})
    public Parts_Pie(int unique) {
        super(unique);
    }

    public Parts_Pie() {
        super();
    }

    @Override
    public IPart getGridPart() {
        return new PartPie(IGrid.GRID_ID);
    }

    private PartPie getPart(char id, int ... locks) { // locks = holes
        PartPie part;
        part = new PartPie(id);
        part.prepareRotations(4);
        part.addEdge(1, PieEdge.CW_0, 2);
        part.addEdge(2, PieEdge.CW_1, 3);
        part.addEdge(3, PieEdge.CW_2, 4);
        part.addEdge(4, PieEdge.CW_3, 1);
        for (int i=0; i<locks.length; i++) {
            part.putKey(locks[i], PieEdge.FRONT, -1);
            part.putKey(locks[i], PieEdge.BACK, -1);
        }
        return part;
    }

    public void buildTest() {
        parts.clear();
        totalFill = 0;

        PartPie part;

        part = getPart('A',1,2);
//        part.anotherOne(0);
        part.putKey(3, PieEdge.FRONT, 1);
        add(part);

//        part = getPart('B',1,2);
//        part.anotherOne(1);
//        part.putKey(3, PieEdge.FRONT, 1);
//        add(part);

//        anotherOne(2);

        part = getPart('C',1,2);
        add(part);

        part = getPart('D',1);
        part.putKey(3, PieEdge.FRONT, 1);
        part.putKey(4, PieEdge.BACK, 1);
        add(part);
    }

    public void buildTest2() {
        parts.clear();
        totalFill = 0;

        PartPie part;

        part = getPart('A');
//        part.anotherOne(0);
        part.putKey(3, PieEdge.FRONT, 2);
        add(part);

//        part = getPart('B',1,2);
//        part.anotherOne(1);
//        part.putKey(3, PieEdge.FRONT, 1);
//        add(part);

//        anotherOne(2);

        part = getPart('C', 3, 4);
        add(part);

        part = getPart('D',3);
        part.putKey(4, PieEdge.BACK, 1);
        add(part);
    }

    public void build() {
        parts.clear();
        totalFill = 0;

        PartPie part;
        
        // 1st half

        part = getPart('A',1,2); //
        part.putKey(3, PieEdge.FRONT, 1);
//        part.anotherOne(0);
        add(part);

        part = getPart('C',1,3); //
        part.putKey(2, PieEdge.FRONT, 1);
        part.putKey(4, PieEdge.BACK, 1);
        add(part);

        part = getPart('E',1,2);//
        part.putKey(4, PieEdge.FRONT, 2);
        part.putKey(4, PieEdge.BACK ,1);
        add(part);

        part = getPart('B',1,2); //
//        part.anotherOne(1);
        part.putKey(3, PieEdge.BACK, 1);
        add(part);

//        anotherOne(2);

        part = getPart('D',3); //
        part.putKey(1, PieEdge.BACK, 1);
        part.putKey(4, PieEdge.FRONT, 2); // length 2
        add(part);

        part = getPart('G',1,2); //
        part.putKey(3, PieEdge.FRONT, 1);
        add(part);
        
        // 2nd half when THREADS=2

        part = getPart('I',1,3); //
        part.putKey(2, PieEdge.FRONT, 1);
        part.putKey(2, PieEdge.BACK, 1);
        add(part);

        part = getPart('K',1,2, 3); //
        // part.putKey(4, PieEdge.FRONT, 1);
        add(part);

        part = getPart('H',3); //
        part.putKey(4, PieEdge.FRONT, 2);
        add(part);

        part = getPart('J',1,2); //
        part.putKey(4, PieEdge.FRONT, 2);
        part.putKey(4, PieEdge.BACK, 1);
        add(part);

        part = getPart('L',3, 4); //
        part.putKey(1, PieEdge.FRONT, 1);
        part.putKey(1, PieEdge.BACK, 1);
        add(part);

        part = getPart('F',2); //
        part.putKey(1, PieEdge.FRONT, 2);
        add(part);

    }

}
