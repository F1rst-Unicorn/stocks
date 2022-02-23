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

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.presenter.ViewModelModule;
import de.njsm.stocks.client.view.FragmentModule;
import de.njsm.stocks.client.view.LocationListFragmentTest;
import de.njsm.stocks.client.view.SetupFormFragmentTest;
import de.njsm.stocks.client.view.SetupGreetingFragmentTest;

import javax.inject.Singleton;

@Singleton
@Component(modules = {
        AndroidInjectionModule.class,
        FragmentModule.class,
        ViewModelModule.class,
        MockBusinessModule.class,
        MockNavigationModule.class,
        TestActivityModule.class,
        BusinessBindModule.class,
})
public interface TestComponent {

    void inject(Application application);

    void inject(LocationListFragmentTest test);

    void inject(SetupGreetingFragmentTest test);

    void inject(SetupFormFragmentTest test);

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(Application a);

        TestComponent build();
    }
}


