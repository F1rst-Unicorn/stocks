package de.njsm.stocks.android;

import android.app.Activity;
import android.app.Fragment;
import com.jakewharton.threetenabp.AndroidThreeTen;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasFragmentInjector;
import de.njsm.stocks.android.dagger.DaggerRootComponent;
import de.njsm.stocks.android.util.ExceptionHandler;

import javax.inject.Inject;

public class Application extends android.app.Application
        implements HasActivityInjector,
        HasFragmentInjector {

    private DispatchingAndroidInjector<Activity> injector;

    private DispatchingAndroidInjector<Fragment> fragmentInjector;

    @Override public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
        DaggerRootComponent
                .builder()
                .application(this)
                .build()
            .inject(this);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getFilesDir(),
                Thread.getDefaultUncaughtExceptionHandler()));
    }

    @Inject
    public void setInjector(DispatchingAndroidInjector<Activity> injector) {
        this.injector = injector;
    }

    @Inject
    public void setFragmentInjector(DispatchingAndroidInjector<Fragment> fragmentInjector) {
        this.fragmentInjector = fragmentInjector;
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return injector;
    }

    @Override
    public AndroidInjector<Fragment> fragmentInjector() {
        return fragmentInjector;
    }
}
