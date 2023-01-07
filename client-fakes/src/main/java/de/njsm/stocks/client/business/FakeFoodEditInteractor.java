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

import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.FoodEditingFormData;
import de.njsm.stocks.client.business.entities.FoodToEdit;
import de.njsm.stocks.client.business.entities.Id;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import java.util.Optional;

public class FakeFoodEditInteractor implements FoodEditInteractor {

    private final BehaviorSubject<FoodEditingFormData> data;

    private Optional<FoodToEdit> formData = Optional.empty();

    public FakeFoodEditInteractor() {
        this.data = BehaviorSubject.create();
    }

    @Override
    public Observable<FoodEditingFormData> getFormData(Id<Food> id) {
        return data;
    }

    @Override
    public void edit(FoodToEdit food) {
        this.formData = Optional.of(food);
    }

    public void reset() {
        formData = Optional.empty();
    }

    public void setData(FoodEditingFormData dataToEdit) {
        this.data.onNext(dataToEdit);
    }

    public Optional<FoodToEdit> getFormData() {
        return formData;
    }
}
