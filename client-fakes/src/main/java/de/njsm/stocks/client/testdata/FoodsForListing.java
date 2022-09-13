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

import de.njsm.stocks.client.business.entities.EmptyFood;
import de.njsm.stocks.client.business.entities.NoStoredAmount;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FoodsForListing {

    private final BehaviorSubject<List<EmptyFood>> data;

    public FoodsForListing(List<EmptyFood> data) {
        this.data = BehaviorSubject.createDefault(data);
    }

    public static List<EmptyFood> getEmpty() {
        return new ArrayList<>(Arrays.asList(
                EmptyFood.create(1, "Banana", false, NoStoredAmount.create("p")),
                EmptyFood.create(4, "Cheese", true, NoStoredAmount.create("g"))
        ));
    }

    public BehaviorSubject<List<EmptyFood>> getData() {
        return data;
    }
}
