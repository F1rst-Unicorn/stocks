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

import de.njsm.stocks.client.business.entities.Identifiable;
import de.njsm.stocks.client.business.entities.ScaledUnit;
import de.njsm.stocks.client.business.entities.ScaledUnitForListing;
import de.njsm.stocks.client.testdata.ScaledUnitsForListing;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class InMemoryScaledUnitDeleterImpl implements EntityDeleter<ScaledUnit> {

    private final BehaviorSubject<List<ScaledUnitForListing>> data;

    @Inject
    InMemoryScaledUnitDeleterImpl(ScaledUnitsForListing unitsForListing) {
        this.data = unitsForListing.getData();
    }

    @Override
    public void delete(Identifiable<ScaledUnit> unit) {
        data.firstElement().subscribe(list -> {
            List<ScaledUnitForListing> newList = new ArrayList<>(list);
            newList.removeIf(v -> v.id() == unit.id());
            data.onNext(newList);
        });
    }
}
