package de.njsm.stocks.android.dagger.modules;

import android.app.Application;
import android.content.Context;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Module
public abstract class UtilModule {

    @Provides
    @Singleton
    static Executor provideExecutor() {
        return Executors.newFixedThreadPool(1);
    }

    @Binds
    abstract Context getContext(Application a);
}
