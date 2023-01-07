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

class EanNumberListInteractorImpl implements EanNumberListInteractor {

    private final EanNumberListRepository repository;

    private final EanNumberAddService service;

    private final Scheduler scheduler;

    private final Synchroniser synchroniser;

    private final ErrorRecorder errorRecorder;

    @Inject
    EanNumberListInteractorImpl(EanNumberListRepository repository, EanNumberAddService service, Scheduler scheduler, Synchroniser synchroniser, ErrorRecorder errorRecorder) {
        this.repository = repository;
        this.service = service;
        this.scheduler = scheduler;
        this.synchroniser = synchroniser;
        this.errorRecorder = errorRecorder;
    }


    @Override
    public Observable<List<EanNumberForListing>> get(Id<Food> food) {
        return repository.get(food);
    }

    @Override
    public void add(EanNumberAddForm eanNumberAddForm) {
        scheduler.schedule(Job.create(Job.Type.ADD_EAN_NUMBER, () -> addInBackground(eanNumberAddForm)));
    }

    private void addInBackground(EanNumberAddForm eanNumberAddForm) {
        try {
            service.add(eanNumberAddForm);
            synchroniser.synchronise();
        } catch (SubsystemException e) {
            errorRecorder.recordEanNumberAddError(e, eanNumberAddForm);
        }
    }
}
