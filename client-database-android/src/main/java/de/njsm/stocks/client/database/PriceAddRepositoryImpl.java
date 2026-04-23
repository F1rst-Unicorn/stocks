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

import de.njsm.stocks.client.business.PriceAddRepository;
import de.njsm.stocks.client.business.ScaledUnitRepository;
import de.njsm.stocks.client.business.entities.GroceryStoreForSelection;
import de.njsm.stocks.client.business.entities.IdImpl;
import de.njsm.stocks.client.business.entities.ScaledUnitForSelection;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

class PriceAddRepositoryImpl implements PriceAddRepository {

    private final ScaledUnitRepository scaledUnitRepository;

    private final GroceryStoreDao groceryStoreDao;

    @Inject
    PriceAddRepositoryImpl(ScaledUnitRepository scaledUnitRepository, GroceryStoreDao groceryStoreDao) {
        this.scaledUnitRepository = scaledUnitRepository;
        this.groceryStoreDao = groceryStoreDao;
    }


    @Override
    public Observable<List<ScaledUnitForSelection>> getUnits() {
        return scaledUnitRepository.getScaledUnitsForSelection();
    }

    @Override
    public Observable<List<GroceryStoreForSelection>> getGroceryStores() {
        return groceryStoreDao.getGroceryStoresForSelection()
                .map(v -> v.stream()
                        .map(data -> GroceryStoreForSelection.create(IdImpl.create(data.id), data.name))
                        .toList());
    }

    static class GroceryStoreData {
        int id;
        String name;
    }
}
