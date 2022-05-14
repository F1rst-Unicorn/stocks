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
 */

package de.njsm.stocks.client.business;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import de.njsm.stocks.client.business.entities.Location;
import de.njsm.stocks.client.business.entities.SetupState;
import de.njsm.stocks.client.business.entities.Unit;
import de.njsm.stocks.client.execution.SchedulerStatusReporter;
import io.reactivex.rxjava3.core.Observable;

import java.util.concurrent.TimeUnit;

@Module
public interface UimockBusinessModule {

    @Binds
    LocationListInteractor locationListInteractor(InMemoryLocationListInteractorImpl implementation);

    @Binds
    EntityDeleter<Location> locationDeleter(InMemoryLocationDeleterImpl implementation);

    @Binds
    LocationAddInteractor locationAddInteractor(InMemoryLocationAddInteractorImpl implementation);

    @Provides
    static Synchroniser synchroniser() {
        return () -> {};
    }

    @Provides
    static SetupStatusChecker setupStatusChecker() {
        return () -> true;
    }

    @Provides
    static SetupInteractor registrationBackend() {
        return v ->
                Observable.just(SetupState.GENERATING_KEYS)
                        .mergeWith(
                                Observable.just(
                                        SetupState.FETCHING_CERTIFICATE,
                                        SetupState.VERIFYING_CERTIFICATE,
                                        SetupState.REGISTERING_KEY,
                                        SetupState.STORING_SETTINGS,
                                        SetupState.SUCCESS
                                ).zipWith(Observable.interval(1, TimeUnit.SECONDS), (state, i) -> state));
    }

    @Provides
    static SchedulerStatusReporter schedulerStatusReporter() {
        return () -> Observable.just(1);
    }

    @Binds
    ErrorRetryInteractor errorRetryInteractor(InMemoryErrorRetryInteractorImpl impl);

    @Binds
    ErrorListInteractor errorListInteractor(InMemoryErrorListInteractorImpl impl);

    @Provides
    static ErrorStatusReporter errorStatusReporter() {
        return () -> Observable.just(1);
    }

    @Binds
    LocationEditInteractor locationEditInteractor(InMemoryLocationEditInteractorImpl impl);

    @Binds
    LocationConflictInteractor locationConflictInteractor(InMemoryLocationConflictInteractorImpl impl);


    @Binds
    UnitListInteractor unitListInteractor(InMemoryUnitListInteractorImpl impl);

    @Binds
    EntityDeleter<Unit> UnitDeleter(InMemoryUnitDeleterImpl impl);

    @Binds
    ScaledUnitListInteractor ScaledUnitListInteractor(InMemoryScaledUnitListInteractorImpl impl);

    @Binds
    ScaledUnitDeleter ScaledUnitDeleter(InMemoryScaledUnitDeleterImpl impl);

    @Binds
    UnitAddInteractor UnitAddInteractor(InMemoryUnitAddInteractorImpl impl);

    @Binds
    UnitEditInteractor UnitEditInteractor(InMemoryUnitEditInteractorImpl impl);

    @Binds
    UnitConflictInteractor UnitConflictInteractor(InMemoryUnitConflictInteractorImpl impl);
}
