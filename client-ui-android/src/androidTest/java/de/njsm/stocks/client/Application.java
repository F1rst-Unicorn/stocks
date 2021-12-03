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

package de.njsm.stocks.client;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.support.HasSupportFragmentInjector;
import de.njsm.stocks.client.di.DaggerTestComponent;
import de.njsm.stocks.client.di.TestComponent;

import javax.inject.Inject;

public class Application extends android.app.Application implements HasActivityInjector, HasSupportFragmentInjector {

    private DispatchingAndroidInjector<Activity> injector;

    private DispatchingAndroidInjector<Fragment> fragmentInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        getDaggerRoot()
                .inject(this);
    }

    public TestComponent getDaggerRoot() {
        return DaggerTestComponent
                .builder()
                .application(this)
                .build();
    }

    @Inject
    public void setInjector(@NonNull DispatchingAndroidInjector<Activity> injector) {
        this.injector = injector;
    }

    @Inject
    public void setFragmentInjector(@NonNull DispatchingAndroidInjector<Fragment> fragmentInjector) {
        this.fragmentInjector = fragmentInjector;
    }

    @NonNull
    @Override
    public AndroidInjector<Activity> activityInjector() {
        return injector;
    }

    @NonNull
    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentInjector;
    }
}
