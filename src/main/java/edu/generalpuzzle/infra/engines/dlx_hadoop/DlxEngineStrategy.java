  /**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.generalpuzzle.infra.engines.dlx_hadoop;

  import edu.generalpuzzle.infra.ICellPart;
  import edu.generalpuzzle.infra.IGrid;
  import edu.generalpuzzle.infra.IPart;
  import edu.generalpuzzle.infra.Parts;
  import edu.generalpuzzle.infra.engines.EngineStrategy;
  import edu.generalpuzzle.infra.engines.ParallelEngineStrategy;
  import org.apache.log4j.Logger;

  import java.util.ArrayList;
  import java.util.List;
  import java.text.NumberFormat;

  public final class DlxEngineStrategy extends ParallelEngineStrategy {

    public static final Logger log = Logger.getLogger(DlxEngineStrategy.class.getName());

    /**
    * This interface just is a marker for what types I expect to get back
    * as column names.
    */
    protected static interface ColumnName {
        // NOTHING
    }

    /**
    * Maintain information about a puzzle part.
    */
    protected static final class Part implements ColumnName {

        private final String name;

        public Part(String name) { this.name = name; }
        public String getName() { return name; }
        public int getId() { return name.charAt(0); }

    }

    static final class GridPoint implements ColumnName {
        private final int id;
        GridPoint(int id) { this.id = id; }
        int getId() { return  id;}
    }

    private List<List<ColumnName>> current;
    private int rowIds[];
    private final List<Integer> rowExtraInfo_r = new ArrayList<Integer>();
    private final List<List<Integer>> rowExtraInfo_pos = new ArrayList<List<Integer>>();
    private final List<Integer> rowExtraInfo_anchor = new ArrayList<Integer>();
      private int i, fromPartInSplits, toPartInSplits;

    @Override
    protected boolean preSolved() {
        triedParts = dancer.branchesTotal;
        stringifySolution(current);

        String s = grid.presentation(implied[0]).toString();
        String newLastSolution = "";
        for (int i=0; i<s.length(); i+=2) {
            char part = s.charAt(i+1);
            if (newLastSolution.indexOf(part) < 0 && part >= 'A' && part <= 'Z') {
                newLastSolution += part;
                char rot = lastSolution.charAt(lastSolution.indexOf(part)+ 1);
                newLastSolution += rot + " ";
            }
        }

        lastSolution = newLastSolution;

        return grid.verify();
    }


    /**
    * Convert a solution to the puzzle returned by the model into a string
    * that represents the placement of the parts onto the board.
    * @param solution the list of column names that were selected in the model
    */
    protected void stringifySolution(List<List<ColumnName>> solution) {

        lastSolution = "";

        // for each part placement...
        int idx = 0;
        for(List<ColumnName> row: solution) {
            // go through to find which part was placed
            Part part;
            char partId = 0;
            List<Integer> pos = null;
            for(ColumnName item: row) {
                if (item instanceof Part) {
                    part = (Part) item;
                    partId = part.getName().charAt(0);
                    lastSolution += " " + partId;

                    if (partId >= 'A' && partId <= 'Z') {
                        IPart found = getParts().get(partId);
                        found.rotate(rowExtraInfo_r.get(rowIds[idx]));
                        lastSolution += rotationChar(found.getRotationIndex());
                        pos = rowExtraInfo_pos.get(rowIds[idx]);
                    }
                        

                    break;
                }
            }

            if (! EngineStrategy.GENERATE_BY_ALL && unique != null && partId == unique.getId()) {
                unique.rotate(rowExtraInfo_r.get(rowIds[idx]));
                unique.getCells()[unique.getAnchorIndex()].setGridCell( grid.getCells().get(rowExtraInfo_anchor.get(rowIds[idx]) ));
            }
            idx++;

            // for each point where the part was placed, mark it with the part name
            for(ColumnName item: row) {
                if (item instanceof GridPoint) {
                    GridPoint p = (GridPoint) item;
                    ICellPart cell = grid.getCells().get(p.id);
                    cell.setPartId(partId);
                    // putKeys();
                    if (pos != null) {
                        if (cell.getcPartId() >= 'A' && cell.getcPartId() <= 'Z') { // cell.isEmptyPartId() is NOT good ?
                            IPart ipart= super.parts.get((char)cell.getPartId());
//                if (part == null)
//                    part.getId();
                            int cellIdx = -1;
                            for (int i=0; i<pos.size(); i++)
                                if (pos.get(i) == p.getId())
                                    cellIdx = i;
                            ICellPart pcell = ipart.getCells()[cellIdx];
                            cell.keysExists = pcell.keysExists;
                            for (int i=0; i<pcell.keys.length; i++)
                                cell.keys[i] = pcell.keys[i];
                        }
                        else {
                            ; //cell.keysExists = false; // no need as in IGrid.verify matches againt non occupied grid cells is not being checked
                        }
                    }
                }
            }
        }

    }



    /**
    * A solution printer that just writes the solution to stdout.
    */
    private class SolutionPrinter implements DancingLinksGeneric.SolutionAcceptor<ColumnName> {

//        public void track(int id) {
//            System.out.println("track");
//        }

//        public void rollback() {
//            System.out.println("rollback");
//        }

        public SolutionPrinter() {
        }

//    @Override - JVM 5 problem with the annotation
        public void solution(List<List<ColumnName>> names, int rowIds2[]) {
            current = names;
            rowIds = rowIds2;
            solved();
        }

        public void track2(List<ColumnName> part, int row) {
//            lastSolutionDL = "";
            int min = Integer.MAX_VALUE;
            boolean found = false;
            for(ColumnName item: part) {
                if (item instanceof DlxEngineStrategy.Part) {
                    DlxEngineStrategy.Part p = (DlxEngineStrategy.Part) item;
                    char partId = p.getName().charAt(0);
                    if (partId >= 'A' && partId <= 'Z') {
                        found = true;
                        lastSolutionDL += Character.toString(partId) + rotationChar(rowExtraInfo_r.get(row));
                    }
                }
                else if (item instanceof DlxEngineStrategy.GridPoint) {
                    int loc = ((DlxEngineStrategy.GridPoint)item).getId();
                    if (loc<min)
                        min = loc;
                }
            }
            if (! found)
                lastSolution += "_";
            lastSolutionDL += "," + min + " ";
//            lastSolutionDL += "," + Math.min(rowExtraInfo_pos.get(row)); // .get(rowExtraInfo_anchor.get(row));

        }

        public void track(char part, int depth2) {
            depth = depth2-1;
            if (part != ' ')
                currBranch[depth] = part;
        }

        public boolean middlePut(List<List<ColumnName>> value, int rowIds2[]) {
            if (grid.considerKeys) { // if false
                rowIds = rowIds2;
                stringifySolution(value);
                boolean result =  grid.verifyMiddleDLX();
                for(List<ColumnName> row: value) 
                    for(ColumnName item: row) {
                        if (item instanceof GridPoint) {
                            GridPoint p = (GridPoint) item;
                            ICellPart cell = grid.getCells().get(p.id);
                            cell.clearPartId();
                        }
                    }

                return result;
            }
            else
                return true;
        }

    }

    protected final List<Part> parts = new ArrayList<Part>();
    private int rows;
    public final DancingLinksGeneric<ColumnName> dancer = new DancingLinksGeneric<ColumnName>(this);
    private final DancingLinksGeneric.SolutionAcceptor<ColumnName> printer;

    public DlxEngineStrategy(Parts regularParts, IGrid grid) {

        super(regularParts, grid);
        dancer.branches = branches;
        dancer.updates = updates;
        dancer.S_heuristic = S_HEURISTIC;
        dancer.solutionInDepth = regularParts.getParts().size() + 1 - regularParts.getPartsInSolution();

        // the original initializeParts();
        for (IPart part: regularParts.getParts()) {
            StringBuffer name = new StringBuffer("x");
            name.setCharAt(0, (char)part.getId());
            parts.add(new Part(name.toString()));
        }

        if (grid.getPlaceHolders() > 0) // talsHole
            parts.add(new Part("_"));

        // the original initialize
        for(int id =0; id < grid.getCells().size(); ++id)
//         if (! (id == 27 || id == 28 || id == 35 || id == 36)) // talsHole
            dancer.addColumn(new GridPoint(id),-1);

        int partBase = dancer.getNumberColumns();
        int index=0;
        for(Part p: parts)
            dancer.addColumn(p, index++);

        dancer.init(parts.size());
        boolean[] row = new boolean[dancer.getNumberColumns()];
        for(int idx = 0; idx < parts.size(); ++idx) {
            row[idx + partBase] = true;
            if (parts.get(idx).getName().equals("_")) {
                for(int i=0; i < partBase; ++i) //grid.getCells().size() - grid.getPlaceHolders(); ++i)
                    row[i] = grid.getCells().get(i).isPlaceHolder();
                dancer.addRow(row,  rows);
                rowExtraInfo_r.add(9999);
                rowExtraInfo_pos.add(new ArrayList<Integer>());
                rowExtraInfo_anchor.add(9999); // as a hole is not a real part
                rows++;
            }
            else if (parts.get(idx).getName().charAt(0) >= 'A') {
                IPart part = regularParts.getParts().get(idx); // parts.get(idx);
                int start = 0;
//                char partId = parts.get(idx).getName().charAt(0);
//                if (partId < 'O')
//                    start = 0 + partId - 'A';
//                else
//                    start = 0 + partId - 'O';
                regularGenerateRows(dancer, part, row,  grid, regularParts, start);
            }
            row[idx + partBase] = false;
        }

        log.info("EngineStrategy" + id + " - sparse matrix rows " + rows);
         printer = new SolutionPrinter();
    }

     private  void regularGenerateRows(DancingLinksGeneric dancer, IPart part,  boolean[] row, IGrid grid, Parts regularParts, int start) {

        // for each rotation
        for (int r=part.getTotalRotations()-1;r>=0; r--) {
            part.rotate(r);
            for (int i=start; i<grid.getCells().size(); i++) {

                if (! EngineStrategy.GENERATE_BY_ALL && part == regularParts.getUniquePart()) { //.getId() == parts.getUnique()) {
                    int newLocation = EngineStrategy.implied[EngineStrategy.uniqueInImplied[r]][ i ];
                    if (newLocation < i)
                        // if (uniqsFound[oldCellIndex][r] != -1)
                        continue;
                    }

                    grid.setCurrCellIndex(i);
                    boolean canPut = grid.canPut(part);
                    if (canPut) {
                        boolean stranded = false;
                        for (ICellPart cell: grid.getCells()) {
                            if (grid.islandSize(cell) < smallestPart && ! partWithPlaceHolderExist) {
                                stranded = true; // var8
                                break;
                            }
                        }
                        if (! stranded) {
                            // clear the columns related to the points on the board
                            for(int idx=0; idx < grid.getCells().size() ; ++idx)  // - grid.getPlaceHolders(); ++idx) { // talsHole
                                row[idx] = false;
                            // mark the shape
                            int id = -1;
                            List<Integer> positions = new ArrayList<Integer>();
                            for (ICellPart cell: part.getCells()) {
                                id = grid.getCellIndex(cell.getGridCellId());
//                              if (id >= 29 && id < 35) // talsHole
//                                 id -= 2;
//                             else if (id >= 37)
//                                 id -= 4;
                                if (id < 0)
                                    break;
                                positions.add(id);
                                row[id] = true;
                            }

                            if (id < 0)
                                break;
                            dancer.addRow(row, rows);
                            rowExtraInfo_r.add(r);
                            rowExtraInfo_pos.add(positions);
                            rowExtraInfo_anchor.add( grid.getCellIndex(part.getCells()[part.getAnchorIndex()].getGridCellId()));
                            rows++;
                        }

                        grid.removeLast(part);
                    }
                }
        }

    }

    /**
    * Generate a list of prefixes to a given depth
    * @param depth the length of each prefix
    * @return a list of arrays of ints, which are potential prefixes
    */
    public List<int[]> getSplits(int depth) {
        return dancer.split(depth);
    }


    /**
    * Find all of the solutions that start with the given prefix. The printer
    * is given each solution as it is found.
    * @param split a list of row indexes that should be choosen for each row
    *        in order
    * @return the number of solutions found
    */
    public int solve(int[] split) {
        return dancer.solve(split, printer);
    }

    @Override
    public void solve() {

        preSolve();

        DancingLinksGeneric.considerKeys = grid.considerKeys;

        int splitsAmount;

        if (parts.size() - 2 >= 3)
            splitsAmount = 3;
        else
            splitsAmount = parts.size() - 2;

        if (DL_SPLITS != 0)
            splitsAmount = DL_SPLITS;

        List<int[]> splits = getSplits(splitsAmount); // tals, 12, 334, 4137 splits for depth 1,2,3

        log.info("splits.size: " + splits.size());

        float slice = (float)splits.size() / partsAmount; //(dancer.getNumberColumns() - grid.getCells().size()); //(toPart - fromPart + 1));

        fromPartInSplits = (int)(slice * fromPart);
        toPartInSplits = (int)(slice * toPart);

        if (toPart == partsAmount)
            toPartInSplits = splits.size();

//        for(i = fromPartInSplits; i < toPartInSplits; i++) {
////        for(i = toPartInSplits-1;i >= fromPartInSplits; i--) {
//            System.out.print("split " + (i+1) + ": ");
//            dancer.show(splits.get(i), printer);
//        }

        knownValue = toPartInSplits - fromPartInSplits;

        for(i = fromPartInSplits; i < toPartInSplits; i++) {
//        for(i = toPartInSplits-1;i >= fromPartInSplits; i--) {
            lastSolutionDL = "split " +  NumberFormat.getInstance().format(i+1) + "/" + NumberFormat.getInstance().format(toPartInSplits) + " - ";
            int[] choices = splits.get(i);
            currentKnownValue = i - fromPartInSplits;

//            dancer.show(choices, printer);
            if (inPause >= 1) pause();

            StringBuffer sb = new StringBuffer();
            for(int choice: choices)
                sb.append(" ").append(choice);
            int result = solve(choices);
//            if (result > 0)
//            log.debug("EngineStrategy" + id + " - " + lastSolutionDL + /* split" + i + "(" + sb.toString() + ")*/ " - possible(!) solutions " + result);
        }

        currentKnownValue = toPartInSplits - fromPartInSplits;
        triedParts = dancer.branchesTotal;
        postSolve();

    }

    @Override
    public float getRatio() {
        if (isAlive())
            triedParts = dancer.branchesTotal;
        totalNodes = triedParts;
//        return super.getRatio();
        return ((float)i - fromPartInSplits) / (toPartInSplits - fromPartInSplits);
    }

      public String getLastSolution() {
          return super.getLastSolution();
//          String s = "";
//          for (int i=0; i<lastSolution.length(); i+= 3) {
//              char part = lastSolution.charAt(i);
//                if (s.indexOf(part) < 0) //  && part >= 'A' && part <= 'Z')
//                    s += part;
//          }
//          return s;
      }      

}

/* for 6x10. with S, 10 seconds
genAll 23
    no S, 140 secs ?

    mine: 12 secs, no genAll
    genAll 30

~~~~

    ~~ 5 8 8 1- 10054 in 25 sec for all F, 11 for two F (2320)  - bug, F not symmetric
    mine: 97, two F(5027)

    ~~ 5 8 8 7- 17360 in 49 sec for all F, for two(4340)
    one F, 2170 in 10 sec. 1.3 M branches
    mine: 28 sec. 12 M branches

0 5 4 3, no S: 5800 sec.  1,969,089,103 branches
genAll 296 sec/ 46 M branches
else- 68 secs

*/
