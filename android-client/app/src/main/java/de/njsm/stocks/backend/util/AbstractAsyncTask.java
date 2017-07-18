package de.njsm.stocks.backend.util;

import android.content.ContextWrapper;
import android.os.AsyncTask;


public abstract class AbstractAsyncTask<S, T, U> extends AsyncTask<S, T, U> {

    protected ContextWrapper context;

    public AbstractAsyncTask(ContextWrapper context) {
        this.context = context;
    }

    @Override
    protected U doInBackground(S... params) {

        if (android.os.Debug.isDebuggerConnected()) {
            android.os.Debug.waitForDebugger();
        }

        Thread.UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();
        if (! (handler instanceof ExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(context.getFilesDir(),
                    Thread.getDefaultUncaughtExceptionHandler()));
        }

        return doInBackgroundInternally(params);
    }

    protected abstract U doInBackgroundInternally(S[] params);
}
