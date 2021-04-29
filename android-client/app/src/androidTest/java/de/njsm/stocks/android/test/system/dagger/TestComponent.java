package de.njsm.stocks.android.test.system.dagger;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import de.njsm.stocks.android.dagger.RootComponent;
import de.njsm.stocks.android.dagger.modules.ActivityModule;
import de.njsm.stocks.android.dagger.modules.DbModule;
import de.njsm.stocks.android.dagger.modules.ViewModelModule;
import de.njsm.stocks.android.dagger.modules.WebModule;

@Singleton
@Component(modules = {
        AndroidInjectionModule.class,
        DbModule.class,
        WebModule.class,
        ViewModelModule.class,
        ActivityModule.class,
        TestUtilModule.class,
})
public abstract class TestComponent implements RootComponent {

    @Component.Builder
    public interface Builder {

        @BindsInstance
        Builder application(android.app.Application a);

        RootComponent build();
    }
}
