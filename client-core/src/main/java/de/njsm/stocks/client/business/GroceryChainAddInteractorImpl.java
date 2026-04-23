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

import de.njsm.stocks.client.business.entities.GroceryChainAddForm;
import de.njsm.stocks.client.business.entities.Job;
import de.njsm.stocks.client.execution.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

class GroceryChainAddInteractorImpl implements GroceryChainAddInteractor {

    private static final Logger LOG = LoggerFactory.getLogger(GroceryChainAddInteractorImpl.class);

    private final Scheduler scheduler;

    private final ErrorRecorder errorRecorder;

    private final GroceryChainAddService groceryChainAddService;

    private final Synchroniser synchroniser;

    @Inject
    GroceryChainAddInteractorImpl(Scheduler scheduler, ErrorRecorder errorRecorder, GroceryChainAddService groceryChainAddService, Synchroniser synchroniser) {
        this.scheduler = scheduler;
        this.errorRecorder = errorRecorder;
        this.groceryChainAddService = groceryChainAddService;
        this.synchroniser = synchroniser;
    }

    @Override
    public void addGroceryChain(GroceryChainAddForm form) {
        scheduler.schedule(Job.create(Job.Type.ADD_GROCERY_CHAIN, () -> addGroceryChainInBackground(form)));
    }

    private void addGroceryChainInBackground(GroceryChainAddForm form) {
        try {
            groceryChainAddService.addGroceryChain(form);
            synchroniser.synchronise();
        } catch (SubsystemException e) {
            LOG.warn("failed to add grocery chain " + form);
            errorRecorder.recordGroceryChainAddError(e, form);
        }
    }
}
