package de.njsm.stocks.android.util;

import android.util.Log;

public class Logger {

    private Class<?> tag;

    public Logger(Class<?> tag) {
        this.tag = tag;
    }

    public void v(String message, Throwable t) {
        Log.v(tag.getCanonicalName(), message, t);
    }

    public void v(String message) {
        Log.v(tag.getCanonicalName(), message);
    }

    public void d(String message, Throwable t) {
        Log.d(tag.getCanonicalName(), message, t);
    }

    public void d(String message) {
        Log.d(tag.getCanonicalName(), message);
    }

    public void i(String message, Throwable t) {
        Log.i(tag.getCanonicalName(), message, t);
    }

    public void i(String message) {
        Log.i(tag.getCanonicalName(), message);
    }

    public void w(String message, Throwable t) {
        Log.w(tag.getCanonicalName(), message, t);
    }

    public void w(String message) {
        Log.w(tag.getCanonicalName(), message);
    }

    public void e(String message, Throwable t) {
        Log.e(tag.getCanonicalName(), message, t);
    }

    public void e(String message) {
        Log.e(tag.getCanonicalName(), message);
    }
}
