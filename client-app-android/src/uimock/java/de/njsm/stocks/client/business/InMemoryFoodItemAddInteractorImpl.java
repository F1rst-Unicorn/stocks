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
import de.njsm.stocks.client.testdata.LocationsForSelection;
import de.njsm.stocks.client.testdata.ScaledUnitsForSelection;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.time.LocalDate;

class InMemoryFoodItemAddInteractorImpl implements FoodItemAddInteractor {

    @Inject
    InMemoryFoodItemAddInteractorImpl() {
    }

    @Override
    public Observable<FoodItemAddData> getFormData(Id<Food> food) {
        return Observable.just(FoodItemAddData.create(
                FoodForSelection.create(food.id(), "Banana"),
                LocalDate.ofEpochDay(5),
                ListWithSuggestion.create(LocationsForSelection.generate(), 1),
                ListWithSuggestion.create(ScaledUnitsForSelection.generate(), 1)));
    }

    @Override
    public void add(FoodItemForm item) {

    }
}
