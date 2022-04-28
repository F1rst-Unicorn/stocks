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
import de.njsm.stocks.client.execution.ExecutionModule;

import javax.inject.Singleton;

@Module(
        includes = {
                ExecutionModule.class,
        }
)
public interface BusinessModule {

    @Binds
    SetupInteractor setupInteractor(SetupInteractorImpl impl);

    @Binds
    SetupRunner setupRunner(SetupInteractorImpl impl);

    @Provides
    @Singleton
    static SetupInteractorImpl setupInteractorImpl(SettingsWriter settingsWriter,
                                                   CertificateFetcher certificateFetcher,
                                                   Registrator registrator,
                                                   CertificateStore certificateStore,
                                                   KeyGenerator keyPairGenerator) {
        return new SetupInteractorImpl(settingsWriter, certificateFetcher, registrator, certificateStore, keyPairGenerator);
    }

    @Binds
    Synchroniser synchroniser(SynchroniserImpl impl);

    @Binds
    SynchroniseInteractor synchroniseInteractor(SynchroniseInteractorImpl impl);

    @Binds
    LocationDeleter locationDeleter(LocationDeleterImpl impl);

    @Binds
    LocationListInteractor locationListInteractor(LocationListInteractorImpl impl);

    @Binds
    LocationAddInteractor locationAddInteractor(LocationAddInteractorImpl impl);

    @Binds
    ErrorRetryInteractor errorRetryInteractor(ErrorRetryInteractorImpl impl);

    @Binds
    ErrorListInteractor errorListInteractor(ErrorListInteractorImpl impl);

    @Binds
    ErrorStatusReporter errorStatusReporter(ErrorListInteractorImpl impl);

    @Binds
    LocationEditInteractor locationEditInteractorImpl(LocationEditInteractorImpl impl);

    @Binds
    LocationConflictInteractor locationConflictInteractor(LocationConflictInteractorImpl impl);

    @Binds
    UnitListInteractor unitListInteractor(UnitListInteractorImpl impl);
}
