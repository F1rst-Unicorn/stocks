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
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

class FoodToBuyInteractorImpl implements FoodToBuyInteractor {

    private final FoodToBuyRepository repository;

    private final FoodToBuyService service;

    private final FoodRegrouper foodRegrouper;

    private final ErrorRecorder recorder;

    private final Synchroniser synchroniser;

    private final Scheduler scheduler;

    @Inject
    FoodToBuyInteractorImpl(FoodToBuyRepository repository, FoodToBuyService service, FoodRegrouper foodRegrouper, ErrorRecorder recorder, Synchroniser synchroniser, Scheduler scheduler) {
        this.repository = repository;
        this.service = service;
        this.foodRegrouper = foodRegrouper;
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

    @Override
    public Observable<List<FoodWithAmountForListing>> getFoodToBuy() {
        Observable<List<StoredFoodAmount>> presentAmounts = repository.getFoodAmountsToBuy();
        Observable<List<StoredFoodAmount>> absentAmounts = repository.getFoodDefaultUnitOfFoodWithoutItems();
        Observable<List<StoredFoodAmount>> allAmounts = Observable.combineLatest(presentAmounts, absentAmounts,
                (u, v) -> Stream.concat(u.stream(), v.stream())
                        .collect(toList()));

        return Observable.combineLatest(
                repository.getFoodToBuy(),
                allAmounts,
                (v, u) -> foodRegrouper.regroup(v, u, FoodWithAmountForListing::create, FoodWithAmountForListing::name)
        );

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
