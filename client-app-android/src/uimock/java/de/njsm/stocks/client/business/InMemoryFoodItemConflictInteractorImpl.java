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

import de.njsm.stocks.client.business.entities.LocationForListing;
import de.njsm.stocks.client.business.entities.LocationForSelection;
import de.njsm.stocks.client.business.entities.ScaledUnitForListing;
import de.njsm.stocks.client.business.entities.ScaledUnitForSelection;
import de.njsm.stocks.client.business.entities.conflict.FoodItemEditConflictData;
import de.njsm.stocks.client.business.entities.conflict.FoodItemEditConflictFormData;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;

import static java.util.Collections.singletonList;

class InMemoryFoodItemConflictInteractorImpl implements FoodItemConflictInteractor {

    @Inject
    InMemoryFoodItemConflictInteractorImpl() {
    }

    @Override
    public Observable<FoodItemEditConflictFormData> getEditConflict(long errorId) {
        return Observable.just(FoodItemEditConflictFormData.create(
                FoodItemEditConflictData.create(1, 2, 3, "original name",
                        LocalDate.ofEpochDay(1),
                        LocalDate.ofEpochDay(2),
                        LocalDate.ofEpochDay(3),
                        LocationForListing.create(7, "original location"),
                        LocationForListing.create(8, "remote location"),
                        LocationForListing.create(9, "local location"),
                        ScaledUnitForListing.create(10, "original", BigDecimal.valueOf(11)),
                        ScaledUnitForListing.create(12, "remote", BigDecimal.valueOf(12)),
                        ScaledUnitForListing.create(13, "local", BigDecimal.valueOf(14))),
                singletonList(LocationForSelection.create(9, "Fridge")),
                singletonList(ScaledUnitForSelection.create(13, "g", BigDecimal.TEN))
        ));
    }
}
