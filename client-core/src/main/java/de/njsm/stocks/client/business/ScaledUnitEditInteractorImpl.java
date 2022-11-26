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
import java.util.List;

import static de.njsm.stocks.client.business.ListSearcher.findFirstSuggestion;

class ScaledUnitEditInteractorImpl implements ScaledUnitEditInteractor {

    private final ScaledUnitEditRepository repository;

    private final ScaledUnitEditService service;

    private final Synchroniser synchroniser;

    private final ErrorRecorder errorRecorder;

    private final Scheduler scheduler;

    @Inject
    ScaledUnitEditInteractorImpl(ScaledUnitEditRepository repository, ScaledUnitEditService service, Synchroniser synchroniser, ErrorRecorder errorRecorder, Scheduler scheduler) {
        this.repository = repository;
        this.service = service;
        this.synchroniser = synchroniser;
        this.errorRecorder = errorRecorder;
        this.scheduler = scheduler;
    }

    @Override
    public Observable<ScaledUnitEditingFormData> getFormData(Id<ScaledUnit> id) {
        Observable<List<UnitForSelection>> units = repository.getUnitsForSelection();
        Observable<ScaledUnitToEdit> scaledUnitToEdit = repository.getScaledUnit(id);

        return Observable.zip(units, scaledUnitToEdit, (unitList, scaledUnit) ->
                ScaledUnitEditingFormData.create(scaledUnit.id(), scaledUnit.scale(),
                        findFirstSuggestion(unitList, scaledUnit::unit)));
    }

    @Override
    public void edit(ScaledUnitToEdit form) {
        scheduler.schedule(Job.create(Job.Type.EDIT_SCALED_UNIT, () -> editInBackground(form)));
    }

    void editInBackground(ScaledUnitToEdit form) {
        ScaledUnitForEditing localScaledUnit = repository.getScaledUnitForSending(form);
        if (form.scale().compareTo(localScaledUnit.scale()) == 0 &&
                form.unit() == localScaledUnit.unit())
            return;

        ScaledUnitForEditing networkData = form.withVersion(localScaledUnit.version());
        try {
            service.edit(networkData);
            synchroniser.synchronise();
        } catch (SubsystemException e) {
            errorRecorder.recordScaledUnitEditError(e, networkData);
            synchroniser.synchroniseAfterError(e);
        }
    }
}
