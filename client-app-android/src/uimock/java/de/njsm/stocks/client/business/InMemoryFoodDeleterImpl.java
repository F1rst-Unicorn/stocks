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

import de.njsm.stocks.client.business.entities.EmptyFood;
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.Identifiable;
import de.njsm.stocks.client.testdata.FoodsForListing;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

class InMemoryFoodDeleterImpl implements EntityDeleter<Food> {

    private final BehaviorSubject<List<EmptyFood>> data;

    @Inject
    InMemoryFoodDeleterImpl(FoodsForListing foodsForListing) {
        this.data = foodsForListing.getData();
    }

    @Override
    public void delete(Identifiable<Food> location) {
        data.firstElement().subscribe(list -> {
            List<EmptyFood> newList = new ArrayList<>(list);
            newList.removeIf(v -> v.id() == location.id());
            data.onNext(newList);
        });
    }
}
