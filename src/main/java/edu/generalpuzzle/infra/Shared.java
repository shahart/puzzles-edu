package edu.generalpuzzle.infra;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/** place for shared data between parallel threads <p>
 * Created by IntelliJ IDEA.
 * Date: 18/08/2008
 */
public class Shared {

    private static Shared sharedInstance = null;

    /** set of solution which can be found twice, appears only on TODO condition */
	public Map<String, Integer> uniqsFoundSolutions[][]; // abstractSet- from Java 1.6, can be 1.5 without the JMain console check
    
	/** set of solutions, relevant only if "anotherOne" parts are exists */
	public Map<String, Integer> sames = new ConcurrentHashMap<String,Integer>(); // ConcurrentSkipListSet

    public static Shared getInstance() {
        
        if (sharedInstance == null)
            sharedInstance = new Shared();
        return sharedInstance;
    }

    private Shared() {
    }

    public void init(int iMax, int jMax) {
        if (uniqsFoundSolutions == null) {
            uniqsFoundSolutions = new ConcurrentHashMap[iMax][jMax]; //new Set[iMax][jMax]; // (Set<Integer>[][])new Object[iMax][jMax];
            for (int i=0; i<iMax; i++)
                for (int j=0; j<jMax; j++)
                    uniqsFoundSolutions[i][j] = //Collections.synchronizedSet(
                            new ConcurrentHashMap<String,Integer>();//); //-1;
        }
    }

}
