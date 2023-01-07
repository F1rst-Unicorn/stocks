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

import de.njsm.stocks.client.business.FoodListRepository;
import de.njsm.stocks.client.business.entities.*;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

class FoodListRepositoryImpl implements FoodListRepository {

    private final FoodDao foodDao;

    private final LocationDao locationDao;

    @Inject
    FoodListRepositoryImpl(FoodDao foodDao, LocationDao locationDao) {
        this.foodDao = foodDao;
        this.locationDao = locationDao;
    }

    @Override
    public Observable<List<FoodForListingBaseData>> getFood() {
        return foodDao.getCurrentFood();
    }

    @Override
    public Observable<List<FoodForListingBaseData>> getFoodBy(Id<Location> location) {
        return foodDao.getCurrentFoodBy(location.id());
    }

    @Override
    public Observable<List<StoredFoodAmount>> getFoodAmounts() {
        return foodDao.getAmounts();
    }

    @Override
    public Observable<List<StoredFoodAmount>> getFoodAmountsIn(Id<Location> location) {
        return foodDao.getAmountsStoredIn(location.id());
    }

    @Override
    public Observable<LocationName> getLocationName(Id<Location> location) {
        return locationDao.getCurrentLocation(location.id())
                .map(v -> LocationName.create(v.name()));
    }

    @Override
    public Observable<List<FoodForEanNumberAssignment>> getForEanNumberAssignment() {
        return foodDao.getForEanNumberAssignment();
    }
}
