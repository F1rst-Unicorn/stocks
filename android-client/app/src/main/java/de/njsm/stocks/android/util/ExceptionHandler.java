package de.njsm.stocks.android.util;

import org.threeten.bp.Instant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final Logger LOG = new Logger(ExceptionHandler.class);

    private File directory;

    private Thread.UncaughtExceptionHandler androidHandler;

    public ExceptionHandler(File directory, Thread.UncaughtExceptionHandler androidHandler) {
        this.directory = directory;
        this.androidHandler = androidHandler;
    }

    @Override
    public void uncaughtException(Thread faultyThread, Throwable occuredException) {
        LOG.i("Caught runtime exception", occuredException);

        PrintWriter pw;
        String timestamp = Config.TECHNICAL_DATE_FORMAT.format(Instant.now());
        String fileName = "crashlog_" + timestamp + ".txt";
        try (FileOutputStream os = new FileOutputStream(new File(directory, fileName))) {
            pw = new PrintWriter(os);
            pw.println(occuredException.getClass().getSimpleName());
            pw.println(timestamp);
            occuredException.printStackTrace(pw);

        } catch(Exception e) {
            LOG.e("Exception during crash logging", e);
        } finally {
            androidHandler.uncaughtException(faultyThread, occuredException);
        }
    }
}
