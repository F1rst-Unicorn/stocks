package de.njsm.stocks.android.dagger.modules;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Module
public class UtilModule {

    @Provides
    @Singleton
    Executor provideExecutor() {
        return Executors.newFixedThreadPool(1);
    }
}
