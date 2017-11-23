package edu.generalpuzzle.main;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.io.PrintWriter;
import java.io.StringWriter;

public final class ShortFormatter extends Formatter {

//    Date dat = new Date();
//    SimpleDateFormat f = new SimpleDateFormat("h:mm:ss");

    private static long millis = -1;

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    @Override
    public String format(LogRecord record) {

        StringBuilder sb = new StringBuilder();

        if (millis == -1)
            millis = record.getMillis();

        String ms = Long.toString((record.getMillis() - millis) % 100000000);
        for (int i=ms.length(); i< 9; i++)
            sb.append(" ");
        sb.append(ms);

//        dat.setTime(record.getMillis());
//        sb.append(f.format(dat))
//            .append("." + record.getMillis() % 1000 + " ");

        // TODO thread name?
        sb.append(" [thread").
            append(record.getThreadID()).
            append("] ");

        sb.append(record.getLevel().getLocalizedName())
            .append(" (");

        if (record.getSourceClassName() != null) {
            String className = record.getSourceClassName();
            int lastIndex = className.lastIndexOf(".");
            if (lastIndex >= 0)
                sb.append(className.substring(lastIndex+1));
            else
                sb.append(className);
        } else {
            sb.append(record.getLoggerName());
        }
        if (record.getSourceMethodName() != null) {
            sb.append(".")
                .append(record.getSourceMethodName());
        }

        sb.append(":")
            .append(getLineNumber());

        sb.append(") - ")
            .append(record.getMessage()) // formatMessage(record))
            .append(LINE_SEPARATOR);

        if (record.getThrown() != null) {
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                sb.append(sw.toString());
            } catch (Exception ex) {
                // ignore
            }
        }

        return sb.toString();
//        return "";
    }

    // based on LogRecord.inferCaller
    private String getLineNumber() {
        // Get the stack trace.
        StackTraceElement stack[] = (new Throwable()).getStackTrace();
        // First, search back to a method in the Logger class.
        int ix = 0;
        while (ix < stack.length) {
            StackTraceElement frame = stack[ix];
            String cname = frame.getClassName();
            if (cname.equals("java.util.logging.Logger")) {
                break;
            }
            ix++;
        }
        // Now search for the first frame before the "Logger" class.
        while (ix < stack.length) {
            StackTraceElement frame = stack[ix];
            String cname = frame.getClassName();
            if (!cname.equals("java.util.logging.Logger")) {
            // We've found the relevant frame.
                return Integer.toString(frame.getLineNumber());
            }
            ix++;
        }
    	// We haven't found a suitable frame, so just punt.  This is
        // OK as we are only committed to making a "best effort" here.
        return "?";
    }

}
