/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.njsm.stocks.client.fragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.njsm.stocks.client.fragment.errordetails.ErrorDetailsFragment;
import de.njsm.stocks.client.fragment.errorlist.ErrorListFragment;
import de.njsm.stocks.client.fragment.locationadd.LocationAddFragment;
import de.njsm.stocks.client.fragment.locationconflict.LocationConflictFragment;
import de.njsm.stocks.client.fragment.locationedit.LocationEditFragment;
import de.njsm.stocks.client.fragment.locationlist.LocationListFragment;
import de.njsm.stocks.client.fragment.setupform.SetupFormFragment;
import de.njsm.stocks.client.fragment.setupgreet.SetupGreetingFragment;
import de.njsm.stocks.client.fragment.unitlist.UnitListFragment;
import de.njsm.stocks.client.fragment.unittabs.UnitTabsFragment;

@Module
public abstract class FragmentModule {

    @ContributesAndroidInjector
    public abstract LocationListFragment locationListFragment();

    @ContributesAndroidInjector
    public abstract LocationAddFragment locationAddFragment();

    @ContributesAndroidInjector
    public abstract SetupGreetingFragment setupGreetingFragment();

    @ContributesAndroidInjector
    public abstract SetupFormFragment setupFormFragment();

    @ContributesAndroidInjector
    public abstract BottomToolbarFragment bottomToolbarFragment();

    @ContributesAndroidInjector
    public abstract ErrorListFragment errorListFragment();

    @ContributesAndroidInjector
    public abstract ErrorDetailsFragment errorDetailsFragment();

    @ContributesAndroidInjector
    public abstract LocationEditFragment locationEditFragment();

    @ContributesAndroidInjector
    public abstract LocationConflictFragment locationConflictFragment();

    @ContributesAndroidInjector
    public abstract UnitListFragment unitListFragment();

    @ContributesAndroidInjector
    public abstract UnitTabsFragment UnitTabsFragment();
}
