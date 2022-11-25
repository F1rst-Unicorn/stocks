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

import de.njsm.stocks.client.business.entities.Job;
import de.njsm.stocks.client.business.entities.Settings;
import de.njsm.stocks.client.execution.Scheduler;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import javax.inject.Inject;

class SettingsInteractorImpl implements SettingsInteractor {

    private final de.njsm.stocks.client.business.Settings settings;

    private final SettingsWriter writer;

    private final NetworkConnectionUpdater networkConnectionUpdater;

    private final Synchroniser synchroniser;

    private final Scheduler scheduler;

    @Inject
    SettingsInteractorImpl(de.njsm.stocks.client.business.Settings settings,
                           SettingsWriter writer,
                           NetworkConnectionUpdater updater,
                           Synchroniser synchroniser,
                           Scheduler scheduler) {
        this.settings = settings;
        this.writer = writer;
        this.networkConnectionUpdater = updater;
        this.synchroniser = synchroniser;
        this.scheduler = scheduler;
    }

    @Override
    public Observable<Settings> getData() {
        BehaviorSubject<Settings> result = BehaviorSubject.create();
        scheduler.schedule(Job.create(Job.Type.GET_SETTINGS, () -> loadSettings(result)));
        return result;
    }

    private void loadSettings(BehaviorSubject<Settings> result) {
        var data = Settings.create(
                settings.getServerName(),
                settings.getCaPort(),
                settings.getRegistrationPort(),
                settings.getServerPort()
        );
        result.onNext(data);
    }

    @Override
    public void clearSearchHistory() {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void performFullSync() {
        synchroniser.synchroniseFull();
    }

    @Override
    public void updateServerName(String v) {
        networkConnectionUpdater.updateServerName(v);
        scheduler.schedule(Job.create(Job.Type.SAVE_SETTINGS, () -> writer.updateServerName(v)));
    }

    @Override
    public void updateCaPort(int port) {
        scheduler.schedule(Job.create(Job.Type.SAVE_SETTINGS, () -> writer.updateCaPort(port)));
    }

    @Override
    public void updateRegistrationPort(int port) {
        scheduler.schedule(Job.create(Job.Type.SAVE_SETTINGS, () -> writer.updateRegistrationPort(port)));
    }

    @Override
    public void updateServerPort(int port) {
        networkConnectionUpdater.updateServerPort(port);
        scheduler.schedule(Job.create(Job.Type.SAVE_SETTINGS, () -> writer.updateServerPort(port)));
    }
}
