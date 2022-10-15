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

import de.njsm.stocks.client.business.entities.FoodItemForListing;
import de.njsm.stocks.client.business.entities.StoredAmount;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class FoodItemsForListing {


    public static List<FoodItemForListing> get() {
        return Arrays.asList(
                FoodItemForListing.create(
                        123,
                        StoredAmount.create(BigDecimal.TEN, "g"),
                        "Fridge",
                        LocalDate.ofEpochDay(0),
                        "Jack",
                        "Mobile"
                ),
                FoodItemForListing.create(
                        444,
                        StoredAmount.create(BigDecimal.TEN, "g"),
                        "Fridge",
                        LocalDate.now().plusDays(2),
                        "Jack",
                        "Mobile"
                ),
                FoodItemForListing.create(
                        456,
                        StoredAmount.create(BigDecimal.ONE, "g"),
                        "Fridge",
                        LocalDate.now().plusDays(7),
                        "Juliette",
                        "Laptop"
                )
        );
    }
}
