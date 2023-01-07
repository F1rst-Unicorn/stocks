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

package de.njsm.stocks.client.presenter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.ViewModel;
import de.njsm.stocks.client.business.SettingsInteractor;
import de.njsm.stocks.client.business.entities.Settings;
import io.reactivex.rxjava3.core.BackpressureStrategy;

import javax.inject.Inject;

public class SettingsViewModel extends ViewModel {

    private final SettingsInteractor interactor;

    @Inject
    SettingsViewModel(SettingsInteractor interactor) {
        this.interactor = interactor;
    }


    public void clearSearchHistory() {
        interactor.clearSearchHistory();
    }

    public void performFullSync() {
        interactor.performFullSync();
    }

    public void updateServerName(String v) {
        interactor.updateServerName(v);
    }

    public void updateCaPort(int port) {
        interactor.updateCaPort(port);
    }

    public void updateRegistrationPort(int port) {
        interactor.updateRegistrationPort(port);
    }

    public void updateServerPort(int port) {
        interactor.updateServerPort(port);
    }

    public LiveData<Settings> getSettings() {
        return LiveDataReactiveStreams.fromPublisher(
                interactor.getData().toFlowable(BackpressureStrategy.LATEST)
        );
    }
}
