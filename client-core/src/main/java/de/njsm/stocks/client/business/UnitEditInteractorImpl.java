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

import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.execution.Scheduler;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;

class UnitEditInteractorImpl implements UnitEditInteractor {

    private final UnitRepository repository;

    private final UnitEditService editService;

    private final Synchroniser synchroniser;

    private final Scheduler scheduler;

    private final ErrorRecorder errorRecorder;

    @Inject
    UnitEditInteractorImpl(UnitRepository repository, UnitEditService editService, Synchroniser synchroniser, Scheduler scheduler, ErrorRecorder errorRecorder) {
        this.repository = repository;
        this.editService = editService;
        this.synchroniser = synchroniser;
        this.scheduler = scheduler;
        this.errorRecorder = errorRecorder;
    }

    @Override
    public Observable<UnitToEdit> get(Id<Unit> id) {
        return repository.getUnit(id);
    }

    @Override
    public void edit(UnitToEdit formData) {
        scheduler.schedule(Job.create(Job.Type.EDIT_UNIT, () -> editInBackground(formData)));
    }

    void editInBackground(UnitToEdit formData) {
        UnitForEditing currentState = repository.getCurrentDataBeforeEditing(formData);
        if (formData.isContainedIn(currentState)) {
            return;
        }
        UnitForEditing dataToNetwork = formData.addVersion(currentState.version());

        try {
            editService.edit(dataToNetwork);
            synchroniser.synchronise();
        } catch (SubsystemException e) {
            errorRecorder.recordUnitEditError(e, dataToNetwork);
            synchroniser.synchroniseAfterError(e);
        }
    }
}
