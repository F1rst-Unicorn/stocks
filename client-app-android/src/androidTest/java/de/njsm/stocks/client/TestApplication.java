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

package de.njsm.stocks.client;

import androidx.annotation.NonNull;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;
import de.njsm.stocks.client.di.DaggerTestComponent;
import de.njsm.stocks.client.di.TestComponent;

import javax.inject.Inject;

public class TestApplication extends android.app.Application implements HasAndroidInjector {

    private DispatchingAndroidInjector<Object> injector;

    private TestComponent dagger;

    @Override
    public void onCreate() {
        super.onCreate();
        getDaggerRoot()
                .inject(this);
    }

    public TestComponent getDaggerRoot() {
        if (dagger == null)
            dagger = DaggerTestComponent
                .builder()
                .application(this)
                .build();

        return dagger;
    }

    @Inject
    public void setInjector(@NonNull DispatchingAndroidInjector<Object> injector) {
        this.injector = injector;
    }

    @Override
    public AndroidInjector<Object> androidInjector() {
        return injector;
    }
}
