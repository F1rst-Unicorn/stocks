/* stocks is client-server program to manage a household's food stock
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

package de.njsm.stocks.client.di;

import dagger.Module;
import dagger.Provides;
import de.njsm.stocks.client.business.SetupInteractor;
import de.njsm.stocks.client.navigation.*;
import de.njsm.stocks.client.fragment.setupform.SetupFormFragmentArgumentProvider;

import javax.inject.Singleton;

import static org.mockito.Mockito.mock;

@Module
class FakeNavigationModule {

    @Provides
    @Singleton
    LocationListNavigator locationListNavigator() {
        return mock(LocationListNavigator.class);
    }

    @Provides
    @Singleton
    SetupGreetingNavigator setupGreetingNavigator() {
        return mock(SetupGreetingNavigator.class);
    }

    @Provides
    @Singleton
    SetupFormFragmentArgumentProvider setupFormFragmentArgumentProvider() {
        return mock(SetupFormFragmentArgumentProvider.class);
    }

    @Provides
    @Singleton
    SetupInteractor registrationBackend() {
        return mock(SetupInteractor.class);
    }

    @Provides
    @Singleton
    SetupFormNavigator setupFragmentNavigator() {
        return mock(SetupFormNavigator.class);
    }

    @Provides
    @Singleton
    Navigator navigator() {
        return mock(Navigator.class);
    }

    @Provides
    @Singleton
    BottomToolbarNavigator bottomToolbarNavigator() {
        return mock(BottomToolbarNavigator.class);
    }

    @Provides
    @Singleton
    ErrorListNavigator errorListNavigator() {
        return mock(ErrorListNavigator.class);
    }

    @Provides
    @Singleton
    ErrorDetailsNavigator errorDetailsNavigator() {
        return mock(ErrorDetailsNavigator.class);
    }

    @Provides
    @Singleton
    LocationEditNavigator locationEditNavigator() {
        return mock(LocationEditNavigator.class);
    }

    @Provides
    @Singleton
    LocationConflictNavigator locationConflictNavigator() {
        return mock(LocationConflictNavigator.class);
    }

    @Provides
    @Singleton
    UnitListNavigator unitListNavigator() {
        return mock(UnitListNavigator.class);
    }

    @Provides
    @Singleton
    ScaledUnitListNavigator ScaledUnitListNavigator() {
        return mock(ScaledUnitListNavigator.class);
    }

    @Provides
    @Singleton
    UnitEditNavigator  UnitEditNavigator() {
        return mock(UnitEditNavigator.class);
    }

    @Provides
    @Singleton
    UnitConflictNavigator  UnitConflictNavigator() {
        return mock(UnitConflictNavigator.class);
    }

    @Provides
    @Singleton
    ScaledUnitEditNavigator  ScaledUnitEditNavigator() {
        return mock(ScaledUnitEditNavigator.class);
    }
}
