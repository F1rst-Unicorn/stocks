package de.njsm.stocks.android;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.jakewharton.threetenabp.AndroidThreeTen;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.support.HasSupportFragmentInjector;
import de.njsm.stocks.android.dagger.DaggerRootComponent;
import de.njsm.stocks.android.util.ExceptionHandler;

import javax.inject.Inject;

public class Application
        extends android.app.Application
        implements HasActivityInjector, HasSupportFragmentInjector {

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

        if (! (Thread.getDefaultUncaughtExceptionHandler() instanceof ExceptionHandler))
            Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getFilesDir(),
                    Thread.getDefaultUncaughtExceptionHandler()));
    }

    @Inject
    public void setInjector(@NonNull DispatchingAndroidInjector<Activity> injector) {
        this.injector = injector;
    }

    @Inject
    public void setFragmentInjector(@NonNull DispatchingAndroidInjector<Fragment> fragmentInjector) {
        this.fragmentInjector = fragmentInjector;
    }

    @NonNull
    @Override
    public AndroidInjector<Activity> activityInjector() {
        return injector;
    }

    @NonNull
    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentInjector;
    }
}
