package de.njsm.stocks.android.test.system.dagger;

import android.app.Application;
import android.content.Context;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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
        return Executors.newFixedThreadPool(1);
    }

    @Binds
    abstract Context getContext(Application a);

    @Binds
    @Singleton
    abstract IdlingResource getIdlingResource(CounterIdlingResource v);
}
