package de.njsm.stocks.android.dagger.modules;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.njsm.stocks.android.frontend.setup.SetupActivity;
import de.njsm.stocks.android.frontend.startup.StartupActivity;
import de.njsm.stocks.android.frontend.user.UserActivity;

@Module
public abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract UserActivity contributeUserActivity();

    @ContributesAndroidInjector
    abstract StartupActivity contributeStartupActivity();

    @ContributesAndroidInjector
    abstract SetupActivity contributeSetupActivity();
}
