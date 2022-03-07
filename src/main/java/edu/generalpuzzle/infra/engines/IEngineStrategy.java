package edu.generalpuzzle.infra.engines;

import edu.generalpuzzle.infra.ICellPart;
import edu.generalpuzzle.infra.IGrid;
import edu.generalpuzzle.infra.Parts;
import edu.generalpuzzle.infra.IPart;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.logging.Logger;

//import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * Date: 23/08/2008
 */
public abstract class IEngineStrategy { // Strategy pattern

    public static final Logger LOG = Logger.getLogger(IEngineStrategy.class.getName());

    public int gridSize;
    public int totalReducedOrientations;
    public int totalOrientations;
    public static double timeTo1st = -1;
    public int totalTime;
    public static String first_sol;
    public static String first_sol_short;

//    protected long starttime;

    protected Parts parts;
    protected IGrid grid;

    public static char lastId = 0;
    protected int id;

    // comm with outer world

    public static boolean FULL_OUTPUT = false; // shows more than the 1st solution

    public static void set_FULL_OUTPUT(boolean b) { FULL_OUTPUT = b; }

    protected long triedParts = 0;

//    protected long triedParts = 0;

    // end of comm

    public static void graphIt() { graphIt = true; }

    public static final boolean INITIAL_SHOW_ALL_COPIES = false; // 223

    public static int inPause = 0;
    public static boolean graphIt = true;

    protected String lastSolution = "";
    public String lastSolutionDL = "";
    protected ICellPart cell1st;
    protected static boolean showAll = INITIAL_SHOW_ALL_COPIES;

    protected int uniqueSolutions;
    protected static int static_uniqueSolutions;

    public static String solutionPresentation;

     // counters

    protected String threadName = "";
    protected long startTime = 0;
     protected long endTime = 0;
    protected long elapsedTime = 0;
     protected int partsAmount;

     protected long updates[];
     protected long branches[];

    protected long totalNodes;


    public IEngineStrategy() {
    }

    protected float knownValue = 1;
    protected float currentKnownValue = -1;

    protected /*static*/ int impliedSize;

    public void setForRatio(float knownValue) {
        this.knownValue = knownValue / impliedSize;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public float getRatio() { // use myPzl.properties in order to know number of solutions
        currentKnownValue = uniqueSolutions;
        if (knownValue <= 1)
            return -1;
        else
            return currentKnownValue / knownValue; //  * lastId;
    }

    public int getEngineId() {
        if (threadName.length() == 0)
            return 0;
        else
            return threadName.charAt(threadName.length()-1) - '0';
    }

    public int getImpliedSize() {
        return impliedSize;
    }

    public static void toggleShowAll() {
        showAll = ! showAll;
    }

    public static int setPause() {

        if (inPause <= lastId) {
            inPause++;
            if (lastId == 1)
                inPause++;
        }
        else
            inPause = 0;

        return inPause;
    }


    public static int getLastSolutionNumber() {
        return static_uniqueSolutions;
    }

    public int getUniqueSolutions() {
        return uniqueSolutions;
    }

    public long getTriedParts() {
        return triedParts;
    }

    public abstract String getStatistics();

    public void pause() {
        synchronized (Thread.currentThread())  {
            while (inPause == id+1 || inPause == lastId+1) {
                try {
                    final long SLEEP_TIME = 500; // max time to wait before the pause click will take effect
                    Thread.currentThread().wait(SLEEP_TIME);
                    startTime += SLEEP_TIME; // stop counting seconds
                }
                catch (Exception e) {
                }
            }
        }
    }

    public abstract void solve();

    protected abstract void solved();

    public IEngineStrategy(Parts parts, IGrid grid) {
        this.parts = parts;
        this.grid = grid;
        this.id = lastId;
        ++ lastId;

        partsAmount = parts.getParts().size();

//        starttime = System.currentTimeMillis();

        int partsAmountForNodes = partsAmount;
        if (EngineStrategy.get_ENGINE_TYPE() == EngineStrategy.ENGINE_TYPE_DLX) {
            ++ partsAmountForNodes;
            if (grid.getPlaceHolders() > 0)
                ++ partsAmountForNodes;
        }

        branches = new long[partsAmountForNodes];
        totalNodes = 0;
        updates = new long[branches.length];

        cell1st = grid.getCells().get( grid.getCurrCellIndex() );
        static_uniqueSolutions = 0;

        gridSize = grid.getLeftCells();
    }

    protected void showSummary() {

        System.out.println("\n" + getStatistics());
        NumberFormat nf = NumberFormat.getInstance();

        System.out.println();
        long totalUpdates = 0;
         totalNodes = 0;
        for (int i=0; i<branches.length; i++) { //parts.getParts().size(); i++) {
            totalUpdates += updates[i];
            totalNodes += branches[i];
        }
        for (int i=0; i<branches.length; i++) { //parts.getParts().size(); i++) {
            System.out.print("\tlevel " + i + " - branches " + nf.format(branches[i]) + " \t% " + (int)((float)branches[i] / totalNodes * 100) ); // +
                    //" updates " + nf.format(updates[i]) + " \t% " + (int)((float)updates[i] / totalUpdates * 100));
            if (branches[i] != 0) System.out.println(" \tper branch " + updates[i] / branches[i]); else System.out.println();
        }
        triedParts = totalNodes;
//        System.out.println("\n\ttotal updates - " + nf.format(totalUpdates));
        System.out.println("\ttotal branches - " + nf.format(totalNodes));
        double totalTime = ((System.currentTimeMillis()- startTime) / 1000.0);   // starttime
        LOG.info("time: " + nf.format(totalTime));
        LOG.info("branches: " + nf.format(totalNodes));
        System.out.println();
    }

    public Parts getParts() {
        return parts;
    }

}
