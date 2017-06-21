package de.njsm.stocks.backend.util;

import android.util.Log;
import de.njsm.stocks.Config;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Date;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    private File directory;

    public ExceptionHandler(File directory) {
        this.directory = directory;
    }

    @Override
    public void uncaughtException(Thread faultyThread, Throwable occuredException) {
        Log.i(Config.log, "Caught runtime exception", occuredException);

        PrintWriter pw = null;
        try {
            String timestamp = Config.TECHNICAL_DATE_FORMAT.format(new Date());
            String fileName = "crashlog_" + timestamp + ".txt";
            FileOutputStream os = new FileOutputStream(new File(directory, fileName));
            pw = new PrintWriter(os);
            pw.println(occuredException.getClass().getSimpleName());
            pw.println(timestamp);
            occuredException.printStackTrace(pw);

        } catch(Exception e) {
            Log.e(Config.log, "Exception during crash logging", e);
        } finally {
            IOUtils.closeQuietly(pw);
        }
    }
}
