package edu.generalpuzzle.infra.engines.trivial;

import edu.generalpuzzle.infra.ICellPart;
import edu.generalpuzzle.infra.IGrid;
import edu.generalpuzzle.infra.IPart;
import edu.generalpuzzle.infra.Parts;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import edu.generalpuzzle.infra.engines.EngineStrategy;
import edu.generalpuzzle.main.Main;

/**
 * Created by IntelliJ IDEA.
 * Date: 04/09/2008
 */
public final class TrivialIterativeEngineStrategy extends TrivialEngineStrategy {

    public TrivialIterativeEngineStrategy(Parts parts, IGrid grid) {
        super(parts,  grid);
    }

    @Override
    public void solve() {
        preSolve();
        putIterative(partsAmount);
        postSolve();
    }

    // load
    private void setState(DataInputStream fis, MyStack s) {
        try {
            int n = fis.readInt();
            while (n-- > 0)
                s.push(fis.readInt());

            partsIndices = new ArrayList<Integer>();
            n = fis.read();
            while (n-- > 0)
                partsIndices.add(fis.read());

            for (n=0; n<solution.length; n++)
                solution[n] = fis.read();

            triedParts = fis.readInt(); // Long
            // triedParts = 0; // since byte was wrote and not int - TODO write long

            totalNodes = 0;
            for (n=0; n<partsAmount; n++) {
                branches[n] = fis.readInt();
                totalNodes += branches[n];
                updates[n] = fis.readInt();
            }

            uniqueSolutions = fis.readInt();
            startTime -= fis.readInt()*1000;

            n = fis.readInt();
            int gridState[] = new int[n];
            for (int j=0; j<n; j++)
                gridState[j] = fis.readInt();
            IGrid.Memento memento = new IGrid.Memento();
            memento.state = gridState;
//            grid.setState(gridState);
            grid.setMemento(memento);

            partsInSolution = fis.read();

            for (IPart part: parts.getParts())
                part.rotate(fis.read());

            for (IPart part: parts.getParts())
                for (ICellPart cell :part.getCells()) {
                    cell.setGridCellId(fis.readInt());
                    cell.setGridCell(grid.get(cell.getGridCellId()));
                }

            if (fis.read() != 255)
                System.out.println("error: invalid load");
        }
        catch (IOException e) {
            System.out.println("error: load error- " +e.getMessage());
        }
    }

    // save
    private void getState(int address, int oldCellIndex, int partIndex, int r, int i, int leftParts, MyStack s) {

        // TODO disable when there are engines (>1)
        if (getEngineId() > 1)
            return;

        try {
            new File("saves").mkdir();
            File file = new java.io.File(Main.FILE_NAME);
            file.delete();
            file.createNewFile();
            FileOutputStream os = new FileOutputStream(file);
            DataOutputStream fos = new DataOutputStream(os);

            // local of putIterative

            fos.write(address);
            fos.writeInt(oldCellIndex);
            fos.write(partIndex);
            fos.write(r);
            fos.write(i);
            fos.write(leftParts);

            int head = s.getHead();
            fos.writeInt(head);
            int data[] = s.getData();
            for (int j=0; j<head;j++)
                fos.writeInt(data[j]);

            // more, global variables

            fos.write(partsIndices.size());
            for (int j: partsIndices)
                fos.write(j);

            for (int j: solution)
                fos.write(j);

            fos.writeInt((int)triedParts); // TODO Long

            for (int n=0;n< partsAmount; n++) {
                fos.writeInt((int)branches[n]);
                fos.writeInt((int)updates[n]);
            }

            fos.writeInt(uniqueSolutions);

            Date end = Calendar.getInstance().getTime();
            int elapsedTime = (int)((end.getTime() - startTime) / 1000);
            fos.writeInt(elapsedTime);

            // pairs of cellId, partId in that cellId
            IGrid.Memento memento = grid.createMemento();
            int gridState[] = memento.state;
            fos.writeInt(gridState.length);
            for (int aGridState : gridState)
                fos.writeInt(aGridState);

            fos.write(partsInSolution);

            for (IPart part: parts.getParts())
                fos.write(part.getRotationIndex());

            for (IPart part: parts.getParts())
                for (ICellPart cell: part.getCells())
                    fos.writeInt(cell.getGridCellId());

            fos.write(255); // very basic validation for loading

            fos.flush();
            fos.close();

        }
        catch (IOException e) {
            System.out.println("error: save error- " +e.getMessage());
        }
    }

    // Iterative - same speed as recursive, but better!
    public void putIterative(int leftParts) {

        final int ADDR_FORWARD = 10;
        final int ADDR_BACKWARD = 20;
        final int ADDR_STOP = 30;

//        for (int i=0; i< partsAmount*2; i++) solutionSoFarBuffer.append(' ');

        int address; // Entry point for each "call"
        // entry start
        int oldCellIndex = 999;
        int partIndex = 999;
        int r = 0;
        int i = fromPart; // loop, start with 0
        // int leftParts;
        // entry stop

        final int ENTRY_SIZE = 6; // 5 ints + address = state

//        Stack<Integer> s = new Stack<Integer>(); // very slow, 15 times slower than "my" stack
        MyStack s = new MyStack((leftParts+1) * ENTRY_SIZE);

        // 1st call into
        s.push(address = ADDR_STOP);
        address = ADDR_FORWARD;

        try {
            File file = new java.io.File("saves/state_puz.bin");
            FileInputStream is = new FileInputStream(file);
            DataInputStream fis = new DataInputStream(is);

            s.pop();

            address = fis.read();
            oldCellIndex = fis.readInt();
            partIndex = fis.read();
            r = fis.read();
            i = fis.read();
            leftParts = fis.read();
            setState(fis, s);

            fis.close();
            file.delete();

            if (id == 0)
                System.out.println("\nLOADED");
        }
        catch (IOException e) {
            if (id == 0)
                System.out.println("\nerror: Load-file not found- restarting");
        }

        while (true) {

            if (toSave) {
                getState(address, /**/ oldCellIndex, partIndex, r,i, leftParts, s);
                toSave = false;
            }

            if (inPause >= 1) pause();

            switch(address) {

                case ADDR_FORWARD:
                    if (leftParts == partsInSolution) { // normally ==0
                        // The base case
                        address = s.pop();
                        solved();
                        break;
                    }


                    oldCellIndex = grid.getCurrCellIndex();

                    boolean canPut = false;

                    int newR=0, newI=0;

                    for (; i<leftParts && ! canPut; i++, r=0) { // note the r=0 !

                        partIndex = partsIndices.get(i);
                        currPart = parts.getParts().get(partIndex);

                        if (leftParts == partsAmount) {
                            if (i == toPart)
                                return;
//                            if (lastSolution.length() <= 2)
                            lastSolution = "" + (char)currPart.getId() + rotationChar(r);
                        }

// 5.8 M, 21 sec, genAll false - REGULAR, no X limit

// for  5 10 6- genAll true (because tha'ts why X in the quarter) 10.9M, 45 sec
// for 5 6 10 - much more, as in 3x20 vs. 20x3
//if (! (currPart.getId() == 'X' && grid.getClass().getName().endsWith("Grid2D") && (oldCellIndex % ((Grid2D)grid).getColumns() >= ((Grid2D)grid).getColumns()/2 || oldCellIndex / ((Grid2D)grid).getColumns() >= (((Grid2D)grid).getRows()-2)/2)))
// knuth 28M branches, 4.1 G updates, with S- 902K, 309M     , little bigger as we can't do the X quarter jo

//                        solutionSoFarBuffer.setCharAt((partsAmount - leftParts)*2+1, (char)(currPart.getId()));

                        for (; r< currPart.getTotalRotations() ; r++) {

                            if (! GENERATE_BY_ALL && currPart == unique) { //.getId() == parts.getUnique()) {
                                int newLocation = implied[uniqueInImplied[r]][ (oldCellIndex) ];

                                if (newLocation < oldCellIndex)
                                    // if (uniqsFound[oldCellIndex][r] != -1)
                                    continue;
                            }


//                            ++ updates[partsAmount - leftParts]; // TODO is that correct?

                            if (possibleRotations[partIndex][r][grid.getCurrCellIndex()] != null) {// process, r, gridCell
                                currPart.rotate(r);
                                ++ updates[partsAmount - leftParts]; // TODO is that correct?


                                if (canPut = grid.newCanPut(currPart)) { //canPut(currPart)) {

//                                    if (grid.isStranded() && leftParts != partsInSolution+1) {    // no parts size 1 and no partWithPlaceHolderExist // TODO not only isStranded,but hole< 5. when seedgehing, stop if size>=5
                                    if (rollback(leftParts)) {

                                        grid.removeLast(currPart);
                                        grid.setCurrCellIndex(oldCellIndex);
                                        canPut = false;

                                    }
                                    else
                                    {

                                        if (trace)
                                            grid.show(implied[0]);

                                        track(leftParts, i, partIndex);

                                        s.push(oldCellIndex);
                                        s.push(partIndex);
                                        s.push(r);
                                        s.push(i);
                                        s.push(leftParts);

                                        // putRecursive(leftParts-1); // the recurse

                                        newR=0;
                                        newI=0;
                                        --leftParts;

                                        s.push(ADDR_BACKWARD); // where should I return?

                                        address = ADDR_FORWARD; // Make another "call"

                                        break;
                                    }
                                }
                            }
                        }
                    }

                    if (! canPut)
                        address = s.pop();
                    else {
                        r = newR;
                        i = newI;
                    }

                    break;

                case ADDR_BACKWARD:
                    // "Compute" and return
                    leftParts = s.pop();
                    i = s.pop();
                    r = s.pop();
                    partIndex = s.pop();
                    oldCellIndex = s.pop();

                    address = s.pop();

                    backTrack(leftParts, oldCellIndex, i, partIndex);

                    // next?

                    //if (r+1 < currPart.getTotalRotations()
                    //|| i+1<leftParts) {

                    if (r+1 < currPart.getTotalRotations())
                        r++;
                    else if (i+1<leftParts) {
                        r=0;
                        i++;
                    }
                    else
                        break;

                    s.push(address);

                    address = ADDR_FORWARD;
                    //}

                    break;

                case ADDR_STOP:
                    // The final return value - in our case, no value
                    return;

            }
        }
    }


    private final class MyStack {
        private final int[] data;
        private int head = 0;

        public MyStack(int size) {
            data = new int[size];
        }

        public boolean isEmpty() {
            return head == 0;
        }

        public void push(int x) {
            data[head++] = x;
        }

        public int pop() {
            return data[--head];
        }

        public int[] getData() {
            return data;
        }

        public int getHead() {
            return head;
        }
    }
}
