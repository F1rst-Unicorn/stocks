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
 *
 */

package de.njsm.stocks.client.di;

import android.app.Activity;
import android.content.Context;
import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.UimockBusinessModule;
import de.njsm.stocks.client.navigation.NavigationModule;
import de.njsm.stocks.client.presenter.ViewModelModule;
import de.njsm.stocks.client.testdata.DataModule;
import de.njsm.stocks.client.fragment.FragmentModule;

import javax.inject.Singleton;

@Singleton
@Component(
        modules = {
                AndroidInjectionModule.class,
                PrimitiveModule.class,
                DataModule.class,
                UimockBusinessModule.class,
                ViewModelModule.class,
                NavigationModule.class,
                NavigationArgConsumerModule.class,
                AppNavigationModule.class,
                ActivityModule.class,
                FragmentModule.class,
        }
)
public interface RootComponent {

    void inject(Application application);

    void inject(Activity activity);

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(android.app.Application a);

        @BindsInstance
        Builder application(Application a);

        @BindsInstance
        Builder context(Context a);

        RootComponent build();
    }
}
