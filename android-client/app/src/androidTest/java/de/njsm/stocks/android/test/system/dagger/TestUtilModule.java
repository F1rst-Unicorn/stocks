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

import android.app.Application;
import android.content.Context;

import androidx.test.espresso.idling.concurrent.IdlingThreadPoolExecutor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import de.njsm.stocks.android.dagger.modules.UtilModule;
import de.njsm.stocks.android.util.idling.CounterIdlingResource;
import de.njsm.stocks.android.util.idling.IdlingResource;

@Module
abstract class TestUtilModule extends UtilModule {

    @Provides
    @Singleton
    static Executor provideExecutor() {
        return new IdlingThreadPoolExecutor("stocks threads", 1, 100, 1,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(100),
                Executors.defaultThreadFactory());
    }

    @Binds
    abstract Context getContext(Application a);

    @Binds
    @Singleton
    abstract IdlingResource getIdlingResource(CounterIdlingResource v);
}
