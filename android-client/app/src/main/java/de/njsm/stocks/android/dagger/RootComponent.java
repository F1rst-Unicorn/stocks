package de.njsm.stocks.android.dagger;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;
import de.njsm.stocks.android.Application;
import de.njsm.stocks.android.dagger.modules.*;

import javax.inject.Singleton;

@Singleton
@Component(modules = {
        AndroidInjectionModule.class,
        DbModule.class,
        WebModule.class,
        ViewModelModule.class,
        ActivityModule.class,
        UtilModule.class,
})
public interface RootComponent extends AndroidInjector<Application> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(android.app.Application a);

        RootComponent build();
    }
}


