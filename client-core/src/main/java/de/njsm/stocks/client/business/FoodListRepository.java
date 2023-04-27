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
import io.reactivex.rxjava3.core.Observable;

import java.time.Instant;
import java.util.List;

/**
 * At every consistent state of the client database it must hold that
 * for each {@link FoodForListingBaseData} b in the result list of {@link #getFood()}}
 * there exists a {@link StoredFoodAmount} a in the result list of {@link #getFoodAmounts()}
 * such that {@code b.id() == a.foodId()} and
 * for each {@link FoodForListingBaseData} b in the result list of {@link #getFood(Id)}}
 * there exists a {@link StoredFoodAmount} a in the result list of {@link #getFoodAmounts(Id)}
 * such that {@code b.id() == a.foodId()} and
 * for each {@link FoodForListingBaseData} b in the result list of {@link #getFoodBy(Id)}}
 * there exists a {@link StoredFoodAmount} a in the result list of {@link #getFoodAmountsIn(Id)}
 * such that {@code b.id() == a.foodId()}
 */
public interface FoodListRepository {

    Observable<List<FoodForListingBaseData>> getFood();

    Observable<FoodDetailsBaseData> getFood(Id<Food> id);

    Observable<List<FoodForListingBaseData>> getFoodBy(Id<Location> location);

    Observable<List<StoredFoodAmount>> getFoodAmounts();

    Observable<List<StoredFoodAmount>> getFoodAmounts(Id<Food> id);

    Observable<List<StoredFoodAmount>> getFoodAmountsIn(Id<Location> location);

    Observable<LocationName> getLocationName(Id<Location> location);

    Observable<List<FoodForEanNumberAssignment>> getForEanNumberAssignment();

    Observable<List<PlotByUnit<Instant>>> getAmountsOverTime(Id<Food> id);

    Observable<List<PlotPoint<Integer>>> getEatByExpirationHistogram(Id<Food> id);
}
