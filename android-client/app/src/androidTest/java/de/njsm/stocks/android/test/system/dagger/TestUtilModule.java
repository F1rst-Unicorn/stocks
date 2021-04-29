package de.njsm.stocks.android.test.system.dagger;

import android.app.Application;
import android.content.Context;

import androidx.test.espresso.idling.concurrent.IdlingThreadPoolExecutor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import de.njsm.stocks.android.dagger.modules.UtilModule;
import de.njsm.stocks.android.util.idling.CounterIdlingResource;
import de.njsm.stocks.android.util.idling.IdlingResource;

@Module
abstract class TestUtilModule extends UtilModule {

    @Provides
    @Singleton
    static Executor provideExecutor() {
        return new IdlingThreadPoolExecutor("stocks threads", 1, 100, 1,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(100),
                Executors.defaultThreadFactory());
    }

    @Binds
    abstract Context getContext(Application a);

    @Binds
    @Singleton
    abstract IdlingResource getIdlingResource(CounterIdlingResource v);
}
