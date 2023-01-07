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

class FoodItemEditInteractorImpl implements FoodItemEditInteractor {

    private final FoodItemEditRepository repository;

    private final FoodItemEditService service;

    private final Synchroniser synchroniser;

    private final ErrorRecorder errorRecorder;

    private final Scheduler scheduler;

    private final Localiser localiser;

    @Inject
    FoodItemEditInteractorImpl(FoodItemEditRepository repository,
                               FoodItemEditService service,
                               Synchroniser synchroniser,
                               ErrorRecorder errorRecorder,
                               Scheduler scheduler,
                               Localiser localiser) {

        this.repository = repository;
        this.service = service;
        this.synchroniser = synchroniser;
        this.errorRecorder = errorRecorder;
        this.scheduler = scheduler;
        this.localiser = localiser;
    }

    @Override
    public Observable<FoodItemEditFormData> getFormData(Id<FoodItem> foodItem) {
        var foodItemBaseData = repository.getFoodItem(foodItem);
        var locations = repository.getLocations();
        var units = repository.getScaledUnits();

        return Observable.zip(foodItemBaseData, locations, units, (f, l, u) -> FoodItemEditFormData.create(
                foodItem.id(),
                f.food(),
                localiser.toLocalDate(f.eatBy()),
                ListSearcher.findFirstSuggestion(l, f::storedIn),
                ListSearcher.findFirstSuggestion(u, f::unit)
        ));
    }

    @Override
    public void edit(FoodItemToEdit item) {
        scheduler.schedule(Job.create(Job.Type.EDIT_FOOD_ITEM, () -> editInBackground(item)));
    }

    void editInBackground(FoodItemToEdit input) {
        FoodItemForEditing localData = repository.getFoodItemForSending(input);
        if (localiser.toLocalDate(localData.eatBy()).equals(input.eatBy()) &&
                localData.storedIn() == input.storedIn() &&
                localData.unit() == input.unit()) {
            return;
        }

        FoodItemForEditing networkData = input.withVersion(localData.version(), localiser);
        try {
            service.edit(networkData);
            synchroniser.synchronise();
        } catch (SubsystemException e) {
            errorRecorder.recordFoodItemEditError(e, networkData);
            synchroniser.synchroniseAfterError(e);
        }
    }
}
