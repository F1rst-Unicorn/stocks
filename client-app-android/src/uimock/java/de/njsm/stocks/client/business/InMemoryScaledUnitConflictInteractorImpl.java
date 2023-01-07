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

import de.njsm.stocks.client.business.entities.UnitForListing;
import de.njsm.stocks.client.business.entities.UnitForSelection;
import de.njsm.stocks.client.business.entities.conflict.ScaledUnitEditConflictData;
import de.njsm.stocks.client.business.entities.conflict.ScaledUnitEditConflictFormData;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.math.BigDecimal;

import static java.util.Collections.singletonList;

class InMemoryScaledUnitConflictInteractorImpl implements ScaledUnitConflictInteractor {

    @Inject
    InMemoryScaledUnitConflictInteractorImpl() {
    }

    @Override
    public Observable<ScaledUnitEditConflictFormData> getScaledUnitEditConflict(long errorId) {
        return Observable.just(ScaledUnitEditConflictFormData.create(
                ScaledUnitEditConflictData.create(1, 2, 3, BigDecimal.valueOf(4), BigDecimal.valueOf(5), BigDecimal.valueOf(6),
                        UnitForListing.create(7, "original name", "original abbreviation"),
                        UnitForListing.create(8, "remote name", "remote abbreviation"),
                        UnitForListing.create(9, "local name", "local abbreviation")
                ),
                singletonList(UnitForSelection.create(8, "remote name"))
        ));
    }
}
