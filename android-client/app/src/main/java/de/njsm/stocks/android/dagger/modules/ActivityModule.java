package de.njsm.stocks.android.dagger.modules;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.njsm.stocks.android.frontend.crashlog.CrashLogListFragment;
import de.njsm.stocks.android.frontend.device.DeviceFragment;
import de.njsm.stocks.android.frontend.emptyfood.EmptyFoodFragment;
import de.njsm.stocks.android.frontend.fooditem.FoodFragment;
import de.njsm.stocks.android.frontend.locations.LocationFragment;
import de.njsm.stocks.android.frontend.main.MainActivity;
import de.njsm.stocks.android.frontend.main.OutlineFragment;
import de.njsm.stocks.android.frontend.settings.SettingsFragment;
import de.njsm.stocks.android.frontend.startup.StartupFragment;
import de.njsm.stocks.android.frontend.user.UserFragment;

@Module
public abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract UserFragment contributeUserFragment();

    @ContributesAndroidInjector
    abstract EmptyFoodFragment contributeEmptyFoodFragment();

    @ContributesAndroidInjector
    abstract FoodFragment contributeFoodFragment();

    @ContributesAndroidInjector
    abstract LocationFragment contributeLocationFragment();

    @ContributesAndroidInjector
    abstract DeviceFragment contributeUserDeviceFragment();

    @ContributesAndroidInjector
    abstract OutlineFragment contributeOutlineFragment();

    @ContributesAndroidInjector
    abstract StartupFragment contributeStartupFragment();

    @ContributesAndroidInjector
    abstract SettingsFragment contributeSettingsFragment();

    @ContributesAndroidInjector
    abstract CrashLogListFragment contributeCrashLogFragment();

    @ContributesAndroidInjector
    abstract MainActivity contributeMainActivity();
}
