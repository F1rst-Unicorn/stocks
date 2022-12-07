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
import de.njsm.stocks.client.business.entities.conflict.FoodEditConflictData;
import de.njsm.stocks.client.business.entities.conflict.FoodEditConflictFormData;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.Period;

import static java.util.Collections.singletonList;
import static java.util.Optional.of;

class InMemoryFoodConflictInteractorImpl implements FoodConflictInteractor {

    @Inject
    InMemoryFoodConflictInteractorImpl() {
    }

    @Override
    public Observable<FoodEditConflictFormData> getEditConflict(long errorId) {
        return Observable.just(FoodEditConflictFormData.create(
                FoodEditConflictData.create(1, 2, 3,
                        "original name", "remote name", "local name",
                        false, false, true,
                        Period.ofDays(4), Period.ofDays(5), Period.ofDays(6),
                        of(LocationForListing.create(7, "original location")),
                        of(LocationForListing.create(8, "remote location")),
                        of(LocationForListing.create(9, "local location")),
                        ScaledUnitForListing.create(10, "original", BigDecimal.valueOf(11)),
                        ScaledUnitForListing.create(12, "remote", BigDecimal.valueOf(12)),
                        ScaledUnitForListing.create(13, "local", BigDecimal.valueOf(14)),
                        "original description", "remote description", "local description"),
                singletonList(LocationForSelection.create(7, "Fridge")),
                singletonList(ScaledUnitForSelection.create(10, "g", BigDecimal.TEN))
        ));
    }
}
