package de.njsm.stocks.server.internal;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class Formatter extends SimpleFormatter {

    private final Date dat = new Date();

    @Override
    public synchronized String format(LogRecord record) {
        dat.setTime(record.getMillis());
        String source;
        if (record.getSourceClassName() != null) {
            source = record.getSourceClassName();
            if (record.getSourceMethodName() != null) {
                source += " " + record.getSourceMethodName();
            }
        } else {
            source = record.getLoggerName();
        }
        String message = formatMessage(record);
        String throwable = "";
        if (record.getThrown() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println();
            record.getThrown().printStackTrace(pw);
            pw.close();
            throwable = sw.toString();
        }
        return String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$-6s %2$s %5$s%6$s%n",
                dat,
                source,
                record.getLoggerName(),
                record.getLevel().getName(),
                message,
                throwable);
    }
}
