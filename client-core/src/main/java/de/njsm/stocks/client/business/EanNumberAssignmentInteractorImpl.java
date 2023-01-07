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

class EanNumberAssignmentInteractorImpl implements EanNumberAssignmentInteractor {

    private final FoodListRepository repository;

    private final EanNumberAddService service;

    private final Synchroniser synchroniser;

    private final Scheduler scheduler;

    private final ErrorRecorder errorRecorder;

    @Inject
    EanNumberAssignmentInteractorImpl(FoodListRepository repository, EanNumberAddService service, Synchroniser synchroniser, Scheduler scheduler, ErrorRecorder errorRecorder) {
        this.repository = repository;
        this.service = service;
        this.synchroniser = synchroniser;
        this.scheduler = scheduler;
        this.errorRecorder = errorRecorder;
    }

    @Override
    public Observable<List<FoodForEanNumberAssignment>> get() {
        return repository.getForEanNumberAssignment();
    }

    @Override
    public void assignEanNumber(Id<Food> food, EanNumberForLookup eanNumber) {
        scheduler.schedule(Job.create(Job.Type.ADD_EAN_NUMBER, () -> assignEanNumberInternally(food, eanNumber)));
    }

    private void assignEanNumberInternally(Id<Food> food, EanNumberForLookup eanNumber) {
        var form = EanNumberAddForm.create(food, eanNumber.eanNumber());

        try {
            service.add(form);
            synchroniser.synchronise();
        } catch (SubsystemException e) {
            errorRecorder.recordEanNumberAddError(e, form);
            synchroniser.synchroniseAfterError(e);
        }
    }
}
