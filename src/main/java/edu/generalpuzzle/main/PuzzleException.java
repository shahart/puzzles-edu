package edu.generalpuzzle.main;

/** catch exceptions inside BSH - the debug feature <p>
 * Created by IntelliJ IDEA.
 * Date: 17/03/2009
 */
public class PuzzleException extends RuntimeException {

    public static boolean addTrace = false;

    private static String myArrays_toString() {
        StringBuffer str = new StringBuffer("\n\n");
        // todo 3rd party, like commons-lang3:3.12.0, ExceptionUtils
        if (addTrace) {
            StackTraceElement[] stackTraceElement = new Exception().getStackTrace();
            for (int i=2; i<stackTraceElement.length; i++)
                if (stackTraceElement[i].toString().contains("sun.reflect"))
                    break;
                else
                    str.append("\tat ").append(stackTraceElement[i]).append("\n");
        }

        return str.toString();
    }

    public PuzzleException(String msg) {
        super(msg + myArrays_toString());
    }

}

