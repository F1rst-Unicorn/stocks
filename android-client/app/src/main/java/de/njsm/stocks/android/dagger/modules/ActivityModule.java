package de.njsm.stocks.android.dagger.modules;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.njsm.stocks.android.frontend.main.MainActivity;
import de.njsm.stocks.android.frontend.main.OutlineFragment;
import de.njsm.stocks.android.frontend.startup.StartupFragment;
import de.njsm.stocks.android.frontend.user.UserFragment;

@Module
public abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract UserFragment contributeUserFragment();

    @ContributesAndroidInjector
    abstract OutlineFragment contributeOutlineFragment();

    @ContributesAndroidInjector
    abstract StartupFragment contributeStartupActivity();

    @ContributesAndroidInjector
    abstract MainActivity contributeMainActivity();
}
