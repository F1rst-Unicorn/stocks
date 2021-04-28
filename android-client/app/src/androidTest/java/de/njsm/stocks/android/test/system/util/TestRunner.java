package de.njsm.stocks.android.test.system.util;

import android.app.Application;
import android.content.Context;

import androidx.test.runner.AndroidJUnitRunner;

import de.njsm.stocks.android.test.system.TestApplication;

public class TestRunner extends AndroidJUnitRunner {

    @Override
    public Application newApplication(ClassLoader cl, String className, Context context) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        return super.newApplication(cl, TestApplication.class.getCanonicalName(), context);
    }
}
