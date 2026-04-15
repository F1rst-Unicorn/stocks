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

package de.njsm.stocks.client.testdata;


import de.njsm.stocks.client.business.entities.GroceryStoreForListing;
import de.njsm.stocks.client.business.entities.GroceryStoresForListing;
import de.njsm.stocks.client.business.entities.IdImpl;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import java.util.ArrayList;
import java.util.List;

public class GroceryStoresForListingData {

    private final BehaviorSubject<GroceryStoresForListing> data;

    public GroceryStoresForListingData(GroceryStoresForListing data) {
        this.data = BehaviorSubject.createDefault(data);
    }

    public static GroceryStoresForListing generate() {
        return GroceryStoresForListing.create(
                IdImpl.create(3),
                "BigShop",
                new ArrayList<>(List.of(
                GroceryStoreForListing.create(IdImpl.create(1), 0, "Zürich HB"),
                GroceryStoreForListing.create(IdImpl.create(2), 0, "Aarau"),
                GroceryStoreForListing.create(IdImpl.create(4), 0, "Lugano")
        )));
    }

    public BehaviorSubject<GroceryStoresForListing> getData() {
        return data;
    }
}
