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

package de.njsm.stocks.client.business;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import de.njsm.stocks.client.execution.Scheduler;

import javax.inject.Singleton;

import static org.mockito.Mockito.mock;

@Module
public interface FakeBusinessModule {

    @Binds
    LocationListInteractor locationListInteractor(FakeLocationListInteractor locationListInteractor);

    @Provides
    @Singleton
    static FakeLocationListInteractor fakeLocationListInteractor() {
        return new FakeLocationListInteractor();
    }

    @Provides
    @Singleton
    static LocationDeleter locationDeleter() {
        return mock(LocationDeleter.class);
    }

    @Provides
    @Singleton
    static Synchroniser synchroniser() {
        return mock(Synchroniser.class);
    }


    @Provides
    @Singleton
    static SetupStatusChecker setupStatusChecker() {
        return mock(SetupStatusChecker.class);
    }

    @Provides
    @Singleton
    static SetupRunner setupRunner() {
        return mock(SetupRunner.class);
    }

    @Provides
    @Singleton
    static Scheduler scheduler() {
        return mock(Scheduler.class);
    }
}
