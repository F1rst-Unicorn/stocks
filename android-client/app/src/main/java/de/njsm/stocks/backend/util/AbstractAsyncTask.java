package de.njsm.stocks.backend.util;

import android.os.AsyncTask;

import java.io.File;


public abstract class AbstractAsyncTask<S, T, U> extends AsyncTask<S, T, U> {

    protected final File exceptionFileDirectory;

    public AbstractAsyncTask(File exceptionFileDirectory) {
        this.exceptionFileDirectory = exceptionFileDirectory;
    }

    @Override
    protected U doInBackground(S... params) {

        if (android.os.Debug.isDebuggerConnected()) {
            android.os.Debug.waitForDebugger();
        }

        Thread.UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();
        if (! (handler instanceof ExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(exceptionFileDirectory,
                    Thread.getDefaultUncaughtExceptionHandler()));
        }

        return doInBackgroundInternally(params);
    }

    protected abstract U doInBackgroundInternally(S[] params);
}
