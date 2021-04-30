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

package de.njsm.stocks.android.test.system.dagger;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import de.njsm.stocks.android.dagger.RootComponent;
import de.njsm.stocks.android.dagger.modules.ActivityModule;
import de.njsm.stocks.android.dagger.modules.DbModule;
import de.njsm.stocks.android.dagger.modules.ViewModelModule;
import de.njsm.stocks.android.dagger.modules.WebModule;

@Singleton
@Component(modules = {
        AndroidInjectionModule.class,
        DbModule.class,
        WebModule.class,
        ViewModelModule.class,
        ActivityModule.class,
        TestUtilModule.class,
})
public abstract class TestComponent implements RootComponent {

    @Component.Builder
    public interface Builder {

        @BindsInstance
        Builder application(android.app.Application a);

        RootComponent build();
    }
}
