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
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Singleton;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    @Provides
    @Singleton
    static LocationAddInteractor locationAddInteractor() {
        return mock(LocationAddInteractor.class);
    }

    @Provides
    @Singleton
    static ErrorRetryInteractor errorRetryInteractor() {
        return mock(ErrorRetryInteractor.class);
    }

    @Provides
    @Singleton
    static ErrorStatusReporter errorStatusReporter() {
        ErrorStatusReporter result = mock(ErrorStatusReporter.class);
        when(result.getNumberOfErrors()).thenReturn(Observable.just(0));
        return result;
    }

    @Binds
    ErrorListInteractor errorListInteractor(FakeErrorListInteractor impl);

    @Provides
    @Singleton
    static FakeErrorListInteractor fakeErrorListInteractor() {
        return new FakeErrorListInteractor();
    }

    @Binds
    LocationEditInteractor locationEditInteractor(FakeLocationEditInteractor impl);

    @Provides
    @Singleton
    static FakeLocationEditInteractor fakeLocationEditInteractor() {
        return new FakeLocationEditInteractor();
    }

    @Binds
    LocationConflictInteractor locationConflictInteractor(FakeLocationConflictInteractor impl);

    @Provides
    @Singleton
    static FakeLocationConflictInteractor fakeLocationConflictInteractor() {
        return new FakeLocationConflictInteractor();
    }

    @Binds
    UnitListInteractor unitListInteractor(FakeUnitListInteractor unitListInteractor);

    @Provides
    @Singleton
    static FakeUnitListInteractor fakeunitListInteractor() {
        return new FakeUnitListInteractor();
    }

    @Provides
    @Singleton
    static UnitDeleter unitDeleter() {
        return mock(UnitDeleter.class);
    }

    @Binds
    ScaledUnitListInteractor ScaledUnitListInteractor(FakeScaledUnitListInteractor impl);

    @Provides
    @Singleton
    static FakeScaledUnitListInteractor FakeScaledUnitListInteractor() {
        return new FakeScaledUnitListInteractor();
    }

    @Provides
    @Singleton
    static ScaledUnitDeleter ScaledUnitDeleter() {
        return mock(ScaledUnitDeleter.class);
    }

    @Provides
    @Singleton
    static UnitAddInteractor UnitAddInteractor() {
        return mock(UnitAddInteractor.class);
    }
}
