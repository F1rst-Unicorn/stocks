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

package de.njsm.stocks.client.database;

import de.njsm.stocks.client.business.EmptyFoodRepository;
import de.njsm.stocks.client.business.EntityDeleteRepository;
import de.njsm.stocks.client.business.entities.*;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.toList;

class FoodRepositoryImpl implements EmptyFoodRepository, EntityDeleteRepository<Food> {

    private final FoodDao foodDao;

    @Inject
    FoodRepositoryImpl(FoodDao foodDao) {
        this.foodDao = foodDao;
    }

    @Override
    public Observable<List<EmptyFood>> get() {
        return foodDao.getCurrentEmptyFood()
                .map(v -> v.stream().map(f ->
                        EmptyFood.create(f.id(), f.name(), f.toBuy(), NoStoredAmount.create(f.unitAbbreviation()))
                        ).collect(toList()));
    }

    @Override
    public Versionable<Food> getEntityForDeletion(Id<Food> id) {
        return foodDao.getForDeletion(id.id());
    }
}
