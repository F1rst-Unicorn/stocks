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

package de.njsm.stocks.client.navigation;

import dagger.Binds;
import dagger.Module;
import de.njsm.stocks.client.fragment.setupform.SetupFormFragmentArgumentProvider;

@Module
public interface NavigationModule {

    @Binds
    LocationListNavigator locationListNavigator(LocationListNavigatorImpl impl);

    @Binds
    SetupGreetingNavigator setupGreetingNavigator(SetupGreetingNavigatorImpl impl);

    @Binds
    SetupFormFragmentArgumentProvider setupFormFragmentArgumentProvider(SetupFormFragmentArgumentProviderImpl impl);

    @Binds
    Navigator navigator(NavigatorImpl impl);

    @Binds
    ErrorListNavigator errorListNavigator(ErrorListNavigatorImpl impl);

    @Binds
    BottomToolbarNavigator bottomToolbarNavigator(BottomToolbarNavigatorImpl impl);

    @Binds
    ErrorDetailsNavigator errorDetailsNavigatorImpl(ErrorDetailsNavigatorImpl impl);

    @Binds
    LocationEditNavigator locationEditNavigator(LocationEditNavigatorImpl impl);

    @Binds
    LocationConflictNavigator locationConflictNavigator(LocationConflictNavigatorImpl impl);

    @Binds
    UnitListNavigator UnitListNavigator(UnitListNavigatorImpl impl);

    @Binds
    ScaledUnitListNavigator ScaledUnitListNavigator(ScaledUnitListNavigatorImpl impl);

    @Binds
    UnitEditNavigator UnitEditNavigator(UnitEditNavigatorImpl impl);

    @Binds
    UnitConflictNavigator UnitConflictNavigator(UnitConflictNavigatorImpl impl);

    @Binds
    ScaledUnitEditNavigator ScaledUnitEditNavigator(ScaledUnitEditNavigatorImpl impl);

    @Binds
    ScaledUnitConflictNavigator ScaledUnitConflictNavigator(ScaledUnitConflictNavigatorImpl impl);

    @Binds
    OutlineNavigator OutlineNavigator(OutlineNavigatorImpl impl);
}
