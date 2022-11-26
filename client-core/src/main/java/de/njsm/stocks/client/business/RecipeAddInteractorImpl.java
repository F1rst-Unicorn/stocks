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
import de.njsm.stocks.client.business.entities.RecipeAddData;
import de.njsm.stocks.client.business.entities.RecipeAddForm;
import de.njsm.stocks.client.execution.Scheduler;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;

class RecipeAddInteractorImpl implements RecipeAddInteractor {

    private final RecipeAddRepository repository;

    private final Scheduler scheduler;

    private final ErrorRecorder errorRecorder;

    private final RecipeAddService service;

    private final Synchroniser synchroniser;

    @Inject
    RecipeAddInteractorImpl(RecipeAddRepository repository,
                            Scheduler scheduler,
                            ErrorRecorder errorRecorder,
                            RecipeAddService service,
                            Synchroniser synchroniser) {
        this.repository = repository;
        this.scheduler = scheduler;
        this.errorRecorder = errorRecorder;
        this.service = service;
        this.synchroniser = synchroniser;
    }

    @Override
    public Observable<RecipeAddData> getData() {
        return Observable.zip(
                repository.getFood(),
                repository.getUnits(),
                RecipeAddData::create
        );
    }

    @Override
    public void add(RecipeAddForm form) {
        scheduler.schedule(Job.create(Job.Type.ADD_RECIPE, () -> addInBackground(form)));
    }

    void addInBackground(RecipeAddForm form) {
        try {
            service.add(form);
            synchroniser.synchronise();
        } catch (SubsystemException e) {
            errorRecorder.recordRecipeAddError(e, form);
            synchroniser.synchroniseAfterError(e);
        }
    }
}
