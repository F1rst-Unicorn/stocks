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
import de.njsm.stocks.client.navigation.LocationListNavigator;
import de.njsm.stocks.client.navigation.SetupFormNavigator;
import de.njsm.stocks.client.navigation.SetupGreetingNavigator;
import de.njsm.stocks.client.view.SetupFormFragmentArgumentProvider;

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
}
