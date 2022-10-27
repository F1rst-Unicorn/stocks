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
import java.util.Optional;

class FoodEditInteractorImpl implements FoodEditInteractor {

    private final FoodEditRepository repository;

    private final FoodEditService service;

    private final Synchroniser synchroniser;

    private final ErrorRecorder errorRecorder;

    private final Scheduler scheduler;

    @Inject
    FoodEditInteractorImpl(FoodEditRepository repository, FoodEditService service, Synchroniser synchroniser, ErrorRecorder errorRecorder, Scheduler scheduler) {
        this.repository = repository;
        this.service = service;
        this.synchroniser = synchroniser;
        this.errorRecorder = errorRecorder;
        this.scheduler = scheduler;
    }

    @Override
    public Observable<FoodEditingFormData> getFormData(Id<Food> id) {
        return Observable.zip(
                repository.getFood(id),
                repository.getScaledUnitsForSelection(),
                repository.getLocations(),
                (food, units, locations) -> {
                    ListWithSuggestion<ScaledUnitForSelection> unitPosition = ListSearcher.findFirstSuggestion(units, food::storeUnit);
                    Optional<Integer> locationPosition = food.location().map(v -> ListSearcher.findFirst(locations, v));
                    return FoodEditingFormData.create(food.id(), food.name(), food.expirationOffset(), locations, locationPosition, unitPosition, food.description());
        });
    }

    @Override
    public void edit(FoodToEdit food) {
        scheduler.schedule(Job.create(Job.Type.EDIT_FOOD, () -> editInBackground(food)));
    }

    public void editInBackground(FoodToEdit editedFood) {
        FoodForEditing localFood = repository.getFoodForSending(editedFood);
        if (localFood.name().equals(editedFood.name()) &&
                localFood.expirationOffset().equals(editedFood.expirationOffset()) &&
                localFood.location().equals(editedFood.location()) &&
                localFood.storeUnit() == editedFood.storeUnit() &&
                localFood.description().equals(editedFood.description()))
            return;

        FoodForEditing networkData = editedFood.withVersion(localFood.version());
        try {
            service.edit(networkData);
            synchroniser.synchronise();
        } catch (SubsystemException e) {
            errorRecorder.recordFoodEditError(e, networkData);
            synchroniser.synchroniseAfterError(e);
        }
    }
}
