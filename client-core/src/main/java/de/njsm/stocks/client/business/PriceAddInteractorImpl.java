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

import de.njsm.stocks.client.business.entities.GroceryStoreForSelection;
import de.njsm.stocks.client.business.entities.Job;
import de.njsm.stocks.client.business.entities.PriceAddForm;
import de.njsm.stocks.client.business.entities.ScaledUnitForSelection;
import de.njsm.stocks.client.execution.Scheduler;
import io.reactivex.rxjava3.core.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

class PriceAddInteractorImpl implements PriceAddInteractor {

    private static final Logger LOG = LoggerFactory.getLogger(PriceAddInteractorImpl.class);

    private final PriceAddRepository priceAddRepository;

    private final PriceAddService priceAddService;

    private final Scheduler scheduler;

    private final ErrorRecorder errorRecorder;

    private final Synchroniser synchroniser;

    @Inject
    PriceAddInteractorImpl(PriceAddRepository priceAddRepository, PriceAddService priceAddService, Scheduler scheduler, ErrorRecorder errorRecorder, Synchroniser synchroniser) {
        this.priceAddRepository = priceAddRepository;
        this.priceAddService = priceAddService;
        this.scheduler = scheduler;
        this.errorRecorder = errorRecorder;
        this.synchroniser = synchroniser;
    }


    @Override
    public Observable<List<ScaledUnitForSelection>> getUnits() {
        return priceAddRepository.getUnits();
    }

    @Override
    public Observable<List<GroceryStoreForSelection>> getGroceryStores() {
        return priceAddRepository.getGroceryStores();
    }

    @Override
    public void addPrice(PriceAddForm form) {
        scheduler.schedule(Job.create(Job.Type.ADD_PRICE, () -> addInBackground(form)));
    }

    private void addInBackground(PriceAddForm form) {
        try {
            priceAddService.addPrice(form);
            synchroniser.synchronise();
        } catch (SubsystemException e) {
            LOG.warn("failed to add price " + form);
            errorRecorder.recordPriceAddError(e, form);
        }
    }
}
