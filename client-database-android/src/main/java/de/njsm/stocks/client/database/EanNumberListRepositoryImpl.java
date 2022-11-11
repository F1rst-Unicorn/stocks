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

import de.njsm.stocks.client.business.EanNumberListRepository;
import de.njsm.stocks.client.business.entities.EanNumberForListing;
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.Id;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.toList;

class EanNumberListRepositoryImpl implements EanNumberListRepository {

    private final EanNumberDao eanNumberDao;

    @Inject
    EanNumberListRepositoryImpl(EanNumberDao eanNumberDao) {
        this.eanNumberDao = eanNumberDao;
    }

    @Override
    public Observable<List<EanNumberForListing>> get(Id<Food> food) {
        return eanNumberDao.get(food.id()).map(v -> v.stream()
                .map(row -> EanNumberForListing.create(row.id(), row.number()))
                .collect(toList()));
    }
}
