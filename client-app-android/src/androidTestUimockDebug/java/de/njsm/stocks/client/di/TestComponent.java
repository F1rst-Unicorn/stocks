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

import android.content.Context;
import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import de.njsm.stocks.client.TestApplication;
import de.njsm.stocks.client.business.FakeBusinessModule;
import de.njsm.stocks.client.execution.FakeExecutionModule;
import de.njsm.stocks.client.navigation.FakeNavigationArgsConsumerModule;
import de.njsm.stocks.client.presenter.ActivityViewModelModule;
import de.njsm.stocks.client.presenter.ViewModelModule;
import de.njsm.stocks.client.fragment.FragmentModule;
import de.njsm.stocks.client.activity.MainActivityTest;
import de.njsm.stocks.client.activity.StartupActivityTest;

import javax.inject.Singleton;

@Singleton
@Component(modules = {
        AndroidInjectionModule.class,
        FakeNavigationModule.class,
        FakeBusinessModule.class,
        FakeExecutionModule.class,
        ViewModelModule.class,
        ActivityViewModelModule.class,
        FragmentModule.class,
        ActivityModule.class,
        FakePrimitiveModule.class,
        FakeNavigationArgsConsumerModule.class,
})
public interface TestComponent {

    void inject(TestApplication application);

    void inject(StartupActivityTest test);

    void inject(MainActivityTest test);

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(TestApplication a);

        @BindsInstance
        Builder context(Context a);

        TestComponent build();
    }
}


