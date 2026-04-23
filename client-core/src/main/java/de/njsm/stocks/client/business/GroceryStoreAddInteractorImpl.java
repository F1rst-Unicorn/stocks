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
import io.reactivex.rxjava3.core.Maybe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

class GroceryStoreAddInteractorImpl implements GroceryStoreAddInteractor {

    private static final Logger LOG = LoggerFactory.getLogger(GroceryStoreAddInteractorImpl.class);

    private final GroceryStoreAddService groceryStoreAddService;

    private final GroceryStoreRepository groceryStoreRepository;

    private final Synchroniser synchroniser;

    private final ErrorRecorder errorRecorder;

    private final Scheduler scheduler;

    @Inject
    GroceryStoreAddInteractorImpl(GroceryStoreAddService groceryStoreAddService, GroceryStoreRepository groceryStoreRepository, Synchroniser synchroniser, ErrorRecorder errorRecorder, Scheduler scheduler) {
        this.groceryStoreAddService = groceryStoreAddService;
        this.groceryStoreRepository = groceryStoreRepository;
        this.synchroniser = synchroniser;
        this.errorRecorder = errorRecorder;
        this.scheduler = scheduler;
    }

    @Override
    public void addGroceryStore(GroceryStoreAddForm form) {
        scheduler.schedule(Job.create(Job.Type.ADD_GROCERY_STORE, () -> addInBackground(form)));

    }

    private void addInBackground(GroceryStoreAddForm form) {
        try {
            groceryStoreAddService.addGroceryStore(form);
            synchroniser.synchronise();
        } catch (SubsystemException e) {
            LOG.warn("failed to add grocery store " + form);
            errorRecorder.recordGroceryStoreAddError(e, form);
        }
    }

    @Override
    public Maybe<GroceryStoreAddData> getFormData(Id<GroceryChain> groceryChain) {
        return groceryStoreRepository.getGroceryChain(groceryChain.toId())
                .map(v -> GroceryStoreAddData.create(v.name()))
                .firstElement();
    }
}
