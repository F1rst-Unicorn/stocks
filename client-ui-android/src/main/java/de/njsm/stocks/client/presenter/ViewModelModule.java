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

package de.njsm.stocks.client.presenter;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import de.njsm.stocks.client.business.*;
import de.njsm.stocks.client.di.ViewModelFactory;
import de.njsm.stocks.client.di.ViewModelKey;
import de.njsm.stocks.client.execution.SchedulerStatusReporter;

import javax.inject.Provider;
import java.util.Map;

@Module
public class ViewModelModule {

    @Provides
    ViewModelProvider.Factory viewModelFactory(Map<Class<? extends ViewModel>, Provider<ViewModel>> providerMap) {
        return new ViewModelFactory(providerMap);
    }

    @Provides
    @IntoMap
    @ViewModelKey(LocationListViewModel.class)
    ViewModel locationViewModel(LocationListInteractor locationListInteractor, LocationDeleter locationDeleter, Synchroniser synchroniser) {
        return new LocationListViewModel(locationListInteractor, locationDeleter, synchroniser);
    }

    @Provides
    @IntoMap
    @ViewModelKey(SetupViewModel.class)
    ViewModel setupViewModel(SetupInteractor setupInteractor) {
        return new SetupViewModel(setupInteractor);
    }

    @Provides
    @IntoMap
    @ViewModelKey(LocationAddViewModel.class)
    ViewModel locationAddViewModel(LocationAddInteractor locationAddInteractor) {
        return new LocationAddViewModel(locationAddInteractor);
    }

    @Provides
    @IntoMap
    @ViewModelKey(ToolbarViewModel.class)
    ViewModel toolbarViewModel(SchedulerStatusReporter schedulerStatusReporter, ErrorStatusReporter errorStatusReporter) {
        return new ToolbarViewModel(schedulerStatusReporter, errorStatusReporter);
    }

    @Provides
    @IntoMap
    @ViewModelKey(ErrorListViewModel.class)
    ViewModel errorListViewModel(Synchroniser synchroniser, ErrorRetryInteractor errorRetryInteractor, ErrorListInteractor errorListInteractor) {
        return new ErrorListViewModel(errorListInteractor, errorRetryInteractor, synchroniser);
    }

    @Provides
    @IntoMap
    @ViewModelKey(ErrorDetailsViewModel.class)
    ViewModel errorDetailsViewModel(ErrorListInteractor errorListInteractor, ErrorRetryInteractor errorRetryInteractor) {
        return new ErrorDetailsViewModel(errorListInteractor, errorRetryInteractor);
    }

    @Provides
    @IntoMap
    @ViewModelKey(LocationEditViewModel.class)
    ViewModel locationEditViewModel(LocationEditInteractor locationEditInteractor) {
        return new LocationEditViewModel(locationEditInteractor);
    }

    @Provides
    @IntoMap
    @ViewModelKey(LocationConflictViewModel.class)
    ViewModel locationConflictViewModel(LocationConflictInteractor locationConflictInteractor, ErrorRetryInteractor errorRetryInteractor) {
        return new LocationConflictViewModel(locationConflictInteractor, errorRetryInteractor);
    }
}
