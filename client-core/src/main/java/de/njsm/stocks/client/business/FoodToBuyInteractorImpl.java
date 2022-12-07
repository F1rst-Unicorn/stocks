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

import javax.inject.Inject;

class FoodToBuyInteractorImpl implements FoodToBuyInteractor {

    private final FoodToBuyRepository repository;

    private final FoodToBuyService service;

    private final ErrorRecorder recorder;

    private final Synchroniser synchroniser;

    private final Scheduler scheduler;

    @Inject
    FoodToBuyInteractorImpl(FoodToBuyRepository repository, FoodToBuyService service, ErrorRecorder recorder, Synchroniser synchroniser, Scheduler scheduler) {
        this.repository = repository;
        this.service = service;
        this.recorder = recorder;
        this.synchroniser = synchroniser;
        this.scheduler = scheduler;
    }

    @Override
    public void manageFoodToBuy(FoodToBuy food) {
        scheduler.schedule(Job.create(Job.Type.UPDATE_SHOPPING_LIST, () -> runInBackground(food, food)));
    }

    @Override
    public void manageFoodToBuy(FoodToToggleBuy food) {
        scheduler.schedule(Job.create(Job.Type.UPDATE_SHOPPING_LIST, () -> runInBackground(food, food)));
    }

    private void runInBackground(Id<Food> food, ShoppingFlagModifying shoppingFlagModifying) {
        FoodForBuying current = repository.getCurrentFood(food);

        if (!shoppingFlagModifying.needsAction(current.toBuy()))
            return;

        FoodForBuying networkData = FoodForBuying.create(current.id(), current.version(), !current.toBuy());
        try {
            service.editToBuy(networkData);
            synchroniser.synchronise();
        } catch (SubsystemException e) {
            recorder.recordFoodToBuyError(e, networkData);
            synchroniser.synchroniseAfterError(e);
        }
    }
}
