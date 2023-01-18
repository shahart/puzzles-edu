package edu.generalpuzzle.main;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.TargetError;

import edu.generalpuzzle.examples.tangram.GridTang;
import edu.generalpuzzle.examples.tangram.Parts_Tang;
import edu.generalpuzzle.infra.*;
import edu.generalpuzzle.infra.engines.*;
import edu.generalpuzzle.infra.engines.trivial.TrivialRecursiveEngineStrategy;
import edu.generalpuzzle.infra.engines.trivial.TrivialIterativeEngineStrategy;
import edu.generalpuzzle.infra.engines.dlx_hadoop.DlxEngineStrategy;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//import jsr166y.ForkJoinPool;
//import extra166y.ParallelArray;
//import extra166y.Ops;

// TO PROFILE: -agentlib:yjpagent=sampling,noj2ee
// in Yourkit 5 -agentlib:yjp5agent=cpu=samples
// -agentlib:hprof=cpu=samples,interval=1 -Xprof

public final class Main {

    public static final Logger log = Logger.getLogger(Main.class.getName());

    public static final String FILE_NAME = "saves/state_puz";

    private boolean autoDebug;

    // no COMM with main.JMain

    private ParallelEngineStrategy engine[] = new ParallelEngineStrategy[1];
    private int engines;
//    ParallelArray<ParallelEngineStrategy> pa;

    private long knownSolutions = 1;

    private int fromPart;
    private int toPart;

    public String cases;

    private double timeToAll;
    private long triedParts = 0;
    private int uniqueSolutions = 0;

    private boolean matchesLoaded = false;


    public String getStatus(int id) {
        return engine[id].getStatistics();
    }

    public void buildCases() {
        cases = ""; // "\n";
        if (new File("config").list() == null)
            return ;
        for (String s: new File("config").list()) 
            if (s.contains("parts"))
                cases += s.substring(0, s.indexOf("_parts")) + "\n";
    }

    /** for parallel engines, determine if id finised */
	public boolean isAlive(int id) {
        if (engine[id] != null)
            return engine[id].isAlive();
        else
            return false;
    }

    public int getEngineId(int i) { return engine[i].getEngineId();}

    /** ratio of work done by engine id */
	public double getRatio(int id) {
        if (engine[id] == null)
            return 0;
        else
            return engine[id].getRatio();
    }

    /** splits the work between the engines, only on the 1st level of the search tree */
	public void engineSetRange(int _fromPart, int _toPart) {
        fromPart = _fromPart;
        toPart = _toPart;
    }

    public int getEnginesAmount() {
        return engine.length;
    }

    // end COMM with main.JMain

    /** validity of entry in the myPzl.properties */
	private String valid(String s, Properties p, String value) {
        String defaultValue = p.getProperty(s);
        if (defaultValue == null)
            System.out.println("error: myPzl.properties (" + s + ") not found - using defaults: " + value);
        else {
            String tmp = p.getProperty(s, value).split(" ")[0];
            StringBuffer res = new StringBuffer();
            for (int i=0; i<tmp.length(); i++)
                if (Character.isLetter(tmp.charAt(i)) || Character.isDigit(tmp.charAt(i)))
                    res.append(tmp.charAt(i));
            return res.toString();
        }
        return defaultValue;
    }

    /** read the myPzl.properties */
	public void readINI(boolean list) {
        engines = 0;
        Properties p = new Properties();
        try {
            p.load(new FileInputStream("myPzl.properties"));
            if (list)
                p.list(System.out);
            EngineStrategy.set_ENGINE_TYPE(Integer.parseInt(valid("ENGINE_TYPE",p, "1")));
            EngineStrategy.set_DL_SPLITS(Integer.parseInt(valid("DL_SPLITS",p, "0")));
            EngineStrategy.set_FULL_OUTPUT(Boolean.parseBoolean(valid("FULL_OUTPUT", p, "false")));
            EngineStrategy.set_GENERATE_BY_ALL(Boolean.parseBoolean(valid("GENERATE_BY_ALL", p, "false")));
//            EngineStrategy.set_NO_ORIENTATIONS(Boolean.parseBoolean(valid("NO_ORIENTATIONS", p, "false")));
            if (EngineStrategy.NO_ORIENTATIONS)
                EngineStrategy.set_GENERATE_BY_ALL(true);
            EngineStrategy.set_GRAPH_FOR_ALL(Boolean.parseBoolean(valid("GRAPH_FOR_ALL", p, "false")));
            EngineStrategy.set_S_HEURI(Boolean.parseBoolean(valid("DL_SIZE_HEURISTIC", p, "true")));

//            if (EngineStrategy.get_ENGINE_TYPE() != EngineStrategy.ENGINE_TYPE_DLX) // TODO 2010
//                EngineStrategy.set_S_HEURI(false);

            EngineStrategy.set_INTERNAL_VIEWER(Boolean.parseBoolean(valid("INTERNAL_VIEWER", p, "false"))); // TODO in linux- choose internal auto
            EngineStrategy.set_ST_HEURI(Boolean.parseBoolean(valid("STRANDED_HEURISTIC", p, "true")));
            EngineStrategy.set_AUTO_GRAPH_IT(Boolean.parseBoolean(valid("AUTO_GRAPH_IT", p, "true")));
            autoDebug = Boolean.parseBoolean(valid("AUTO_DEBUG", p, "false"));
            engines = Integer.parseInt(valid("THREADS", p, "1"));
            if (engines > Runtime.getRuntime().availableProcessors())
                System.out.println("note. too many threads\n");
        }
        catch (IOException e) {
            System.out.println("error: myPzl.properties not found - using defaults.");
        }
        catch (NumberFormatException e) {
            System.out.println("error: myPzl.properties - wrong value " + e.getMessage());
        }
        if (engines == 0)// || engines == 1) // TODO 12 if more than one tprocessor, otherwise 1
            engines = Runtime.getRuntime().availableProcessors();
        // engine = new ParallelEngineStrategy[12]; //engines]; //parArr 12 or engines
    }

    public static ParallelEngineStrategy getConcreteEngine(Parts parts, IGrid grid, int userEngine) {
//        parts.getParts().remove(11); // var8
        switch (userEngine) {
            case EngineStrategy.ENGINE_TYPE_RECURSIVE:
                return new TrivialRecursiveEngineStrategy(parts, grid);
            case EngineStrategy.ENGINE_TYPE_ITERATIVE:
                return new TrivialIterativeEngineStrategy(parts, grid);
            default:
                EngineStrategy.set_ENGINE_TYPE(EngineStrategy.ENGINE_TYPE_DLX);
                return new DlxEngineStrategy(parts, grid);
        }
    }

    public static void main(String args[]) {

        System.setProperty("log4j1.compatibility","true");

        Main main = new Main();
        main.buildCases();
        try {
            main.go(args);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("\nDONE\n");
    }

//    private static final Ops.Procedure<ParallelEngineStrategy> solveIt =
//        new Ops.Procedure<ParallelEngineStrategy>() {
//          public void op(ParallelEngineStrategy s) {
//              s.call();
//          }
//        };
//
//    private static final Ops.ObjectToLong<ParallelEngineStrategy> sumIt =
//        new Ops.ObjectToLong<ParallelEngineStrategy>() {
//            public long op(ParallelEngineStrategy engine) {
//                return engine.getTotalSolutions();
//            }
//        };

    public long getSum() {
        long sum = 0;
        if (engine != null)
            for (ParallelEngineStrategy p:engine)
                if (p!= null)
                    sum += p.getUniqueSolutions();
        return sum;
    }

    public long getTime() {
        long sum = 0;
        if (engine != null)
            for (ParallelEngineStrategy p:engine)
                if (p!= null)
                    sum += p.getElapsedTime();
        return sum / 1000;
    }

    public long getTime(int i) {
        long sum = 0;
        if (engine != null)
            if (engine[i]!= null)
                return engine[i].getElapsedTime();
        return sum / 1000;
    }

    public String go(String args[]) {

        Tee.getInstance();

//        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
//        int results = compiler.run(null, null, null, "Foo.java");
//
//
//        compiler = ToolProvider.getSystemJavaCompiler();
//        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
//        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
//        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromStrings(Arrays.asList("Foo.java"));
//        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null,
//        null, compilationUnits);
//        boolean success = task.call();
//        // fileManager.close();
//
//        System.exit(0);

        boolean assertsEnabled = false;
        assert assertsEnabled = true; // note the trick, not == but =
        if (! assertsEnabled)
            System.out.println("note: assertsDisabled\n");

//        PropertyConfigurator.configure("myLog.properties");

        readINI(false); // true

        if (args.length < 1) {
            System.out.println("error: missing problemId");
            System.out.println(cases);
            return "missing problemId";
        }

        StringBuffer argsBuffer = new StringBuffer();
        for (String arg: args)
            if (! arg.equalsIgnoreCase(Tee.DEBUG_CODE))
                argsBuffer.append(arg).append('_');
        argsBuffer.setLength(argsBuffer.length()-1);

//        int problemId = Integer.parseInt(args[0]);

        int size = args.length;
        if (args[size-1].equalsIgnoreCase(Tee.DEBUG_CODE))
            size --;

        Args.args = new String[size];
        for (int i=0; i<size; i++)
            Args.args[i] = args[i];

        IGrid atLeastGrid = null;
        Parts partsOk = null;

        String exitMain;

        if (false) { // debug mode- TODO?
            args[0] = "manual";
            exitMain = manualCustom();
        }
        else
            exitMain = custom(args, atLeastGrid, partsOk);

        if (exitMain != null)
            return exitMain;

        int partsSize = engine[0].getParts().getParts().size();
        if (toPart == Integer.MAX_VALUE || toPart == 0 || toPart > partsSize)
            toPart = partsSize;

        Properties p = new Properties();
        try { p.load(new FileInputStream("results.properties"));} catch (Exception e) {}
        int originalPsize = p.size();


        List<String> sortedArgsBuffer = new ArrayList<String>();
        for (int i=1; i<args.length; i++)
            sortedArgsBuffer.add(args[i]);
        Collections.sort(sortedArgsBuffer);

        argsBuffer = new StringBuffer(args[0]);
        for (String arg: sortedArgsBuffer)
            if (! arg.equalsIgnoreCase(Tee.DEBUG_CODE))
                argsBuffer.append("_").append(arg);

        if (matchesLoaded)
            argsBuffer.append("_matches");

        String knownValueString = p.getProperty(argsBuffer.toString());

//        if (EngineStrategy.get_ENGINE_TYPE() != EngineStrategy.ENGINE_TYPE_DLX) {
            System.out.println("\n\nargs - total solutions");
//            p.list(System.out);
            if (knownValueString != null) {
                knownSolutions = Long.parseLong(knownValueString);
                System.out.println("\nknownValue found - " + NumberFormat.getInstance().format(knownSolutions));
                if (engine[0].getImpliedSize() > 1)
                    System.out.println("\tunique -  " + NumberFormat.getInstance().format(knownSolutions / engine[0].getImpliedSize()));

                // TODO estimate if uniq!=total, by multiply by the total/uniq
            }
            else {
                System.out.println("\nnot found (for time estimations)");
                if (knownSolutions != 1)
                    System.out.println("using zero value");
            }
//        }

        // TODO knownValue per thread
        for (EngineStrategy engin: engine)
            engin.setForRatio(knownSolutions);

        System.out.println();
        GraphIt.setFolderArgs(argsBuffer + "/");
        GraphIt.setArgs(argsBuffer.toString() + "_" +
            (EngineStrategy.get_ENGINE_TYPE() == EngineStrategy.ENGINE_TYPE_DLX  ? "MC" : "BT") +
            (EngineStrategy.S_HEURISTIC ? "_SZ" : "") +
            (EngineStrategy.ST_HEURISTIC ? "_ST" : ""));

        new File("tmp" + File.separator + argsBuffer.toString()); //args[0]);

        int enginePart = partsSize/ engine.length;
        int currPart = fromPart;

        log.info(argsBuffer.toString());
        log.info("using " + engine[0].getClass().getName());

        List<Callable<Integer>> tasks = new LinkedList<Callable<Integer>>();

        for (int i=0; i<engine.length; i++) {
            if (i == engine.length-1 && currPart + enginePart < partsSize) // handle the last part
                enginePart = toPart - currPart;
            engine[i].setRange(currPart, currPart + enginePart);
            currPart += enginePart;
            tasks.add(engine[i]); // parArr
        }

        Thread.currentThread().setPriority(Thread.NORM_PRIORITY-1);

        System.gc();

        timeToAll = System.currentTimeMillis();

//        ForkJoinPool fj = new ForkJoinPool(2);
//        pa =  ParallelArray.createUsingHandoff(engine, fj);
//        pa.apply(solveIt);
//

//        new File("tmp").mkdir();
//        new File("tmp/" + argsBuffer.toString()).mkdir(); //Args.args[0]).mkdir();

        ExecutorService executor = Executors.newFixedThreadPool(engines); //engine.length+1); // parArr
        try {
            new File("tmp").mkdir();
            new File("tmp/" + argsBuffer.toString()).mkdir(); //Args.args[0]).mkdir();

            executor.invokeAll(tasks);
        }
        catch (Exception e) {
            e.printStackTrace();                                                                                                                                                                                    
        }
        executor.shutdown();
        
        // duplicates statistics

        int sum=0;
        int count=0;

        if (Shared.getInstance().uniqsFoundSolutions != null)
            for (int i=0; i< Shared.getInstance().uniqsFoundSolutions.length; i++)
                for (int j=0; j< Shared.getInstance().uniqsFoundSolutions[i].length; j++)
                    if (Shared.getInstance().uniqsFoundSolutions[i][j] != null && ! Shared.getInstance().uniqsFoundSolutions[i][j].isEmpty()) {
                        sum += Shared.getInstance().uniqsFoundSolutions[i][j].size();
                        count++;
                    }


        if (sum > 0)
            System.out.println("sum = " + sum + " count = " + count);


        log.info("finished");

        log.info("parallel time: " + (System.currentTimeMillis() -timeToAll)/1000);
        timeToAll = (System.currentTimeMillis() - timeToAll) / 1000;

        int totalSolutions = 0;

        for (ParallelEngineStrategy engin: engine) {
            triedParts += engin.getTriedParts();
            uniqueSolutions += engin.getUniqueSolutions();
            totalSolutions += engin.getTotalSolutions();
        }

        System.out.println("total " + getTime() + " seconds");

        if (//EngineStrategy.get_ENGINE_TYPE() != EngineStrategy.ENGINE_TYPE_DLX &&
            engine[0].getLastSolution().length() > 0 && toPart - fromPart == partsSize) { // TODO or
        // update, even if argsBuffer not changed, the gridParts order might do
            String newKnownValue =
                    Integer.toString(totalSolutions);

            p.put(argsBuffer.toString(), newKnownValue);
            if (p.size() < originalPsize)
                System.out.println("error: some error with results.properties keys\nNo save");
            else
                try {
                    FileOutputStream out = new FileOutputStream("results.properties");
//                    Writer w = new FileWriter("results.properties");
//                    BufferedWriter bw = new BufferedWriter(w);
//                    for (Map.Entry<Object, Object> entry :p.entrySet()) {
//                        bw.write(entry.getKey() + "=" + entry.getValue());
//                        bw.newLine();
//                    }
                    p.store(out, "args (problemId_[SORTED dimensions]) = results (total solutions)"); // newline for linux
//                    bw.close();
//                    w.close();
                    add_to_paper_Statistics(argsBuffer.toString());
                }
                catch (Exception e) {
                    System.out.println("error: " + e);
                }
        }

        NumberFormat nf = NumberFormat.getInstance();
        System.out.print("\nnumber of ");
        if (! EngineStrategy.GENERATE_BY_ALL)
            System.out.print("unique ");
        System.out.print("solutions " + nf.format(uniqueSolutions) + " ");
        System.out.print("\ntried " + nf.format(triedParts) + " parts");

        log.info((! EngineStrategy.GENERATE_BY_ALL ? "unique " : "") + "solutions: " + uniqueSolutions);

        if (totalSolutions != uniqueSolutions) {
            System.out.println("\n\nsymmetries: " + totalSolutions/ uniqueSolutions + " ==>");
            System.out.println("number of total solutions " + nf.format(totalSolutions));
        }

        if (totalSolutions != knownSolutions && knownSolutions != 0)
            System.out.println("\nNOT FINISHED (maybe)");

        new java.io.File(Main.FILE_NAME).delete();



        try {
            StringBuffer argsBuffer2 = new StringBuffer();
            for (String arg: Args.args)
                argsBuffer2.append(arg).append('_');
            argsBuffer2.setLength(argsBuffer2.length()-1);
            String file = argsBuffer2.toString() + (EngineStrategy.get_ENGINE_TYPE() == EngineStrategy.ENGINE_TYPE_DLX ? "_MC" : "_BT");
			file += EngineStrategy.S_HEURISTIC ? "_SZ" : "";
			file += EngineStrategy.ST_HEURISTIC ? "_ST" : "";
			file += "_engine";
            new File("tmp").mkdir();
            new File("tmp/" + argsBuffer.toString()).mkdir(); //Args.args[0]).mkdir();
            if (System.getProperty("os.name").contains("Windows")) {
                Runtime.getRuntime().exec("cmd /c copy GraphLayout.log tmp" + File.separator + argsBuffer.toString() /*Args.args[0] */+ File.separator + file + ".log");
                Runtime.getRuntime().exec("cmd /c copy " + Tee.TEE_FILE + "  tmp" + File.separator + argsBuffer.toString() /*Args.args[0] */+ File.separator + file + "_console.log" );
            }
            else {
                Runtime.getRuntime().exec("/bin/cp GraphLayout.log tmp" + File.separator + argsBuffer.toString() /*Args.args[0] */+ File.separator + file + ".log");
                Runtime.getRuntime().exec("/bin/cp " + Tee.TEE_FILE + "  tmp" + File.separator + argsBuffer.toString() /*Args.args[0] */+ File.separator + file + "_console.log" );
            }
        }
        catch (Exception e) {}


        return null;
    }

    private void add_to_paper_Statistics(String args) throws Exception {

        final int FIELDS = 15;

        String stats[] = new String[FIELDS];
        stats[0] = args;
        stats[1] = ""; // "add your comment";
        stats[2] = String.valueOf(engine[0].gridSize);
        stats[3] = String.valueOf(engine[0].getParts().getParts().size());
        stats[4] = String.valueOf(engine[0].totalOrientations);
        stats[5] = String.valueOf(engine[0].totalReducedOrientations);
        stats[6] = String.valueOf((uniqueSolutions));
        stats[7] = String.valueOf(engine[0].getImpliedSize());

        stats[8] = EngineStrategy.get_ENGINE_TYPE() == EngineStrategy.ENGINE_TYPE_DLX  ? "MC" : "BT";
        stats[8] += EngineStrategy.S_HEURISTIC ? "+SZ" : "";
        stats[8] += EngineStrategy.ST_HEURISTIC ? "+ST" : "";

        NumberFormat nf = NumberFormat.getInstance();

        stats[9] = String.valueOf(triedParts);    // not nf as commas are not allowed
        stats[10] = nf.format(ParallelEngineStrategy.timeTo1st);
        stats[11] = nf.format(timeToAll); //getTime()); // TODO precision
        stats[12] = ParallelEngineStrategy.first_sol;
        stats[13] = ParallelEngineStrategy.first_sol_short;
        stats[FIELDS-1] = new SimpleDateFormat("d/M/y").format(new Date());

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < stats.length; i++) {
            if (i != 0) sb.append(", ");
            sb.append(stats[i]);
        }

        System.out.println(sb.toString());

        List<String> lines = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader("paperStatistics.csv"));
            String s = br.readLine();
            s = br.readLine();
            while (s != null) {
                lines.add(s);
//                System.out.println(s);
                s = br.readLine();
            }
            br.close();
            lines.add(sb.toString() + "\n");
//            lines.remove(0);
            Collections.sort(lines);

            // uniq

            int ssize = lines.size();

            String t = "";
            for (int i=0; i<ssize; i++) {
                String str = lines.get(i);
                if ( str.substring(0, relevantContent(str)). compareTo(t.substring(0, relevantContent(t))) != 0)
                    t = str;
                else {
                    lines.remove(i--);
                    ssize--;
                }
            }

        }
        catch (FileNotFoundException e) {
        }

        Writer w = new FileWriter("paperStatistics.csv");
        BufferedWriter bw = new BufferedWriter(w);
        w.write(".Lattice, puzzle name, puzzle size, Parts orig., Parts total, parts reduced, Solutions unique, symmetries, method, branches, Time to first, time to all, solution, short solution, date\n");
        for (String s: lines) {
            bw.write(s);
            bw.newLine();
        }
//            bw.newLine();w.write(s + "\n"); // TODO newline for linux

        bw.close();
        w.close();
    }

    /** debug */
	private String custom(String[] args, IGrid atLeastGrid, Parts partsOk) {
        PuzzleException.addTrace = true;
        if (args.length < 1 || (args.length == 1 && args[0].equalsIgnoreCase("DEBUG_CODE"))) {
            System.out.println("missing caseId"); // , try\n");
            return "missing caseId";
        }

        String argsToScript[] = new String[args.length-1];
        for (int i=1; i<args.length; i++)
            argsToScript[i-1] = Args.args[i]; //

        Interpreter interpreter = new Interpreter();
        String shuffle = "";

        int i =0;
        do {
            try {
				interpreter.set("bsh.args", argsToScript);

                if (i == 0)
                    System.out.println("\ncustom (" + args[0] + ") loaded - starting...\n");
                IGrid grid = (IGrid) interpreter.source("config/" + args[0] + "_grid.bsh");
                if (grid == null) {
					System.out.println(interpreter.get("error"));
                    return (String)interpreter.get("error");
				}
                atLeastGrid = grid;
                Parts parts = (Parts) interpreter.source("config/" + args[0] + "_parts.bsh");

                if (parts == null) {
					System.out.println(interpreter.get("error"));
                    return (String)interpreter.get("error");
				}

                if (engine.length == 1) {
                    int partsSize = parts.getParts().size();
                    // 10/5/10 change
//                    if (EngineStrategy.ENGINE_TYPE != EngineStrategy.ENGINE_TYPE_DLX &&
//                            EngineStrategy.S_HEURISTIC == true)
//                        partsSize = 1;
                    // 10/5/10 change - end
                    engine = new ParallelEngineStrategy[partsSize];
                }

                partsOk = parts;
                interpreter.set("parts", parts);
                try {
                    Parts.matches = (boolean[][]) interpreter.source("config/" + args[0] + "_matches.bsh");
                    matchesLoaded = true;
                    System.out.println("matches.bsh - Loaded\n");
                }
                catch (FileNotFoundException e) { }
                catch (Exception e) { System.out.println(e + "\n"); }
                for (IPart part: parts.getParts()) {
                    parts.complete(part);
                    if (i == 0) 
                        if (part != parts.getUniquePart())
                            System.out.println(part);
                }

                if (i == 0 && parts != null) {

                    try {
                        Parts partsII = (Parts) interpreter.source("config/" + args[0] + "_parts.bsh");
                        IPart unique = partsII.getUniquePart();
                        partsII.clearUnique();
                        partsII.complete(unique);
                        System.out.println(unique + " <- unique");
                    }
                    catch (Exception e) {}

                    Collections.shuffle(parts.getParts());

                    StringBuffer shuffleBuffer  = new StringBuffer();
                    for (IPart p: parts.getParts())
                        shuffleBuffer.append(String.valueOf((char)p.getId()));

//                    Random random = new Random();
//                    for (int first = 0; first<shuffleBuffer.length(); first++) {
//                        int second = random.nextInt(shuffleBuffer.length());
//                        char temp = shuffleBuffer.charAt(first);
//                        shuffleBuffer.setCharAt(first, shuffleBuffer.charAt(second));
//                        shuffleBuffer.setCharAt(second, temp);
//                    }

                    shuffle = shuffleBuffer.toString();
                    parts.reorder(shuffle);
                    System.out.println("\nshuffle result: " + shuffle);

                    System.out.println("\nsolving Type: " + grid.getCells().get(0).getClass().getName());
                    System.out.println("parts: " + parts.getParts().size());
                    System.out.println("grid cells: " + (grid.getLeftCells())); // - grid.getPlaceHolders()));
                    System.out.println("\ncustom loaded - successfully\n");

                    if (!autoDebug || args[args.length-1].equalsIgnoreCase(Tee.DEBUG_CODE))
                        System.out.println("AUTO_DEBUG: false");

                    else {

                        System.out.println("DEBUGGING.");
                        System.out.println("\nexamples: parts.get('F').rotate();");
                        System.out.println(" grid.show(); parts.show('F'); ");
                        System.out.println("\nto stop: exit or kill\n");

                        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                        String line = "";

                        while (true) { //! line.contains("exit")) {
                            try {
                                System.out.print(">>");
                                line = reader.readLine();
                                if (line.trim().equalsIgnoreCase("kill")) {
                                    System.out.println("User abort");
                                    return "user abort";
    //                                    System.exit(0);
                                }
                                else if (line.trim().equalsIgnoreCase("exit") || line.trim().equalsIgnoreCase("quit"))
                                    break;
                                else {
                                    System.out.println(line);
                                    try {
                                        Object o = interpreter.eval(line);
                                        if (o != null)
                                            System.out.println(o);
                                    }
                                    catch (EvalError e) {
                                        System.out.println(e.getMessage());
                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (Exception e) {
                            }
                        }

                        System.out.println("end of debugging\n");
                    }
                }
//                else
//                if (parts != null)
//                    for (IPart part: parts.getParts()) {
//                        parts.complete(part);
//                        System.out.println(part);
//                    }


                if (parts != null) {
                    if (i>0)
                        parts.reorder(shuffle);
                    engine[i] = getConcreteEngine(parts, grid, EngineStrategy.get_ENGINE_TYPE());
                }

            }
            catch (FileNotFoundException e) {
                String s = e.getMessage();
                System.out.println(s.substring(s.lastIndexOf("config")));
            }
            catch (TargetError e ) {
                System.out.println("The script or code called by the script threw an exception: "+ e); // getTarget() );
            }
            catch (EvalError e )    {
                System.out.println("There was an error in evaluating the script:" + e );
            }
            catch (IOException e2 )    {
                System.out.println("some IO error:" + e2 );
            }
            if (partsOk == null) {
                if (atLeastGrid != null)
                    try {
                        EngineStrategy.set_AUTO_GRAPH_IT(true);
                        System.out.println("At least the grid is valid...");
                        atLeastGrid.show(); //GraphIt.getInstance().showGrid(atLeastGrid);// interpreter.source("showGrid.bsh");
                    }
                    catch (Exception e2) {
                        System.out.println("but can't show it: " + e2.toString());
                    }
                return "At least the grid is valid...";            // TODO + the error from the catches
            }
            if (i == 0) {

            }
            i++;
        }
        while (i < engine.length);

        if (//Parts.locksUsed.size() == 0 && // no locks, so these are positive numbers
           (Parts.matches == null || Parts.matches[1] == null)) {
            int keys = Parts.keysUsed.size();
            Parts.matches = new boolean [keys+1][keys+1];
            for (int j=1; j<=keys; j++) {
                Parts.matches[j] = new boolean[keys+1];
                Parts.matches[j][j] = true;
            }
        }
        return null;
    }

    /** no debug, hard code the case */
	private String manualCustom() {
        PuzzleException.addTrace = false;
        System.out.println("MANUAL===============");
        int i=0;
        do {
            Parts parts;
            IGrid grid;

            // from 3d_grid.bsh - note the casting into the concrete IGrid impl.

//            grid = new Grid2DExamples();
//            ((Grid2DExamples)grid).buildCheckers();
//
            grid = new GridTang();
            ((GridTang)grid).buildAnother(); // TODO remove the grid.reorder in the EngineStrategy
//            grid.buildSquare(2,2);

            int newLoc[] = new int[grid.getCells().size()];
            for (int j=0; j<newLoc.length; j++)
                newLoc[j] = j;
            System.out.println(grid.showBuf(newLoc));

            // end

//            parts = new Parts2D_Examples();
//            ((Parts2D_Examples)parts).build_Checkers();

//            if (engine.length == 1) {
//                int partsSize = parts.getParts().size();
//                engine = new ParallelEngineStrategy[partsSize];
//            }
//
            parts = new Parts_Tang();
            ((Parts_Tang)parts).buildRealTangram();

            for (IPart p:parts.getParts()) {
                parts.complete(p);
                System.out.println(p);
            }

//            if (parts.getUniquePart() != null) {
//                if (! EngineStrategy.GENERATE_BY_ALL)
//                    parts.getUniquePart().totalRotations = 1;
//                System.out.println(parts.getUniquePart());
//            }
            
            Collections.shuffle(parts.getParts());
            
//            parts.show('H');

            // end


            engine[i] = getConcreteEngine(parts, grid, EngineStrategy.get_ENGINE_TYPE());

            System.out.println(grid.showBuf(newLoc));


            for (IPart p: parts.getParts()) {
                System.out.println(p);//(char)p.getId());
                for (int r=p.getTotalRotations()-1; r>=0; r--) {
                    p.rotate(r);
                    System.out.println("rotation: " + r);
                    int cellOrig = grid.getCurrCellIndex();
                    for (int cell=0; cell<grid.getCells().size(); cell++) {
                        grid.setCurrCellIndex(cell);
                        if (grid.canPut(p)) {
                            System.out.println(grid.showBuf(newLoc));
                            grid.removeLast(p);
                            break;
                        }
                    }
                    grid.setCurrCellIndex(cellOrig);
                }
            }

            i++;

        }
        while (i < engine.length);

        return null;
    }

    private int relevantContent(String s) {
        int i= s.length();
        if (i==0) return i;
        i--;         while (s.charAt(i) != ',')            i--; // date
        i--;         while (s.charAt(i) != ',')            i--; // short sol
        i--;         while (s.charAt(i) != ',')            i--; // sol
        i--;         while (s.charAt(i) != ',')            i--; // time to all
        i--;         while (s.charAt(i) != ',')            i--; // time to 1st
        i--;         while (s.charAt(i) != ',')            i--; // branches - varies between parts.reorder()
        return i+1;
    }
    
}
