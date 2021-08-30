/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2021  The stocks developers
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
 */

package de.njsm.stocks.android.business.data.conflict;

import java.time.Instant;

import java.util.function.BiConsumer;

public class FoodItemComparison extends DataComparison<FoodItemInConflict> {

    public FoodItemComparison(FoodItemInConflict original, FoodItemInConflict remote, FoodItemInConflict local) {
        super(original, remote, local);
    }

    public boolean compareEatByDate(BiConsumer<Instant, Boolean> consumer) {
        return new FieldComparator<>(original.getEatByDate(), remote.getEatByDate(), local.getEatByDate()).compare(consumer);
    }

    public boolean compareLocation(BiConsumer<Integer, Boolean> consumer) {
        return new FieldComparator<>(original.getLocation(), remote.getLocation(), local.getLocation()).compare(consumer);
    }

    public boolean compareUnit(BiConsumer<Integer, Boolean> consumer) {
        return new FieldComparator<>(original.getUnit(), remote.getUnit(), local.getUnit()).compare(consumer);
    }
}
