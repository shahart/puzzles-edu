package edu.generalpuzzle.main;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/** allows determine if there's a console (for debug), since this is a requirement --> need for TEE function <p>
 * Created by IntelliJ IDEA.
 * Date: 19/03/2009
 */
public class Tee extends PrintStream {

    private static Tee teeInstance = null;

    private PrintStream teeFile;

    public static final boolean INSIDE_IDE = false; // to debug from inside IDE (= no console)
    public static final String DEBUG_CODE = "DEBUG_CODE";

    public static final String TEE_FILE = "console.log";

    private List<String> window = new ArrayList<String>();
    private int lines = 0;
    private int realLines = 0;
    public static final int MAX = 100;
    private StringBuffer curr = new StringBuffer();
    
    public static Tee getInstance() {
        if (teeInstance == null)
            teeInstance = new Tee(System.out);
        return teeInstance;
    }

    private Tee(OutputStream o) {
        super(o);
        try {
            teeFile = new PrintStream(new BufferedOutputStream(new FileOutputStream(TEE_FILE))); // ,true - for auto flush, not works
            System.setOut(this);
        } catch (IOException e) {
            System.out.println("can't open " + TEE_FILE + " for writing");
        }
    }
	
	
        // /**
        // * Closes the main stream. 
        // * The second stream is just flushed but <b>not</b> closed.
        // * @see java.io.PrintStream#close()
        // */
		/*
        @Override
        public void close() {
            // just for documentation
            super.close();
        }

        @Override
        public void flush() {
            super.flush();
            teeFile.flush();
        }

        @Override
        public void write(byte[] buf, int off, int len) {
            super.write(buf, off, len);
            teeFile.write(buf, off, len);
        }

        @Override
        public void write(int b) {
            super.write(b);
            teeFile.write(b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            super.write(b);
            teeFile.write(b);
        }
		*/

	
    private void teeSplit(String s) {
        int lastN = s.lastIndexOf('\n');
        if (lastN >= 0) {
            String sarr[] = s.split("\n");
            for (int i=0; i<sarr.length; i++) {
                String s2 = sarr[i];
                super.print(s2);
                teeFile.print(s2);
                if (i < sarr.length-1 || lastN == s.length()-1) {
                    super.print("\n");
                    teeFile.println();
                }
            }
        }
        else {
            super.print(s);
            teeFile.print(s);
        }
    }

    public void println(String s) {
        teeSplit(s);
        super.print("\n");
        teeFile.println();
        teeFile.flush();
        curr.append(s);
        windowIt();
    }

    private void windowIt() { //String s) {
        ++lines;
        ++realLines;
        for (int i=0; i<curr.length(); i++)     // TODO still "missing" count
            if (curr.charAt(i) == '\n')
                ++realLines;
        window.add(curr.append("\n").toString());
        curr.setLength(0);
        if (lines > MAX) {
            lines = MAX;
            window.remove(0);
        }
    }

    public String getWindow() {
        StringBuffer j = new StringBuffer();
        for (String s: window)
            j.append(s);//.append("\n");
        return j.toString();
    }

    public int getWindowSize() {
        return realLines;
    }


    public void print(String s) {
        teeSplit(s);
        teeFile.flush();
        curr.append(s);
    }

    public void print(Object obj) {
        teeSplit(obj.toString());
        teeFile.flush();
        curr.append(obj.toString());
    }

    public void println(Object obj) {
		if (obj == null) return;
        teeSplit(obj.toString());
        super.print("\n");
        teeFile.println();
        teeFile.flush();
        curr.append(obj.toString());
        windowIt();
    }

    public void println() {
        super.println();
        teeFile.println();
        teeFile.flush();
        windowIt();
    }
	
}
