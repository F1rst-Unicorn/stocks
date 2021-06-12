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

package de.njsm.stocks.android.frontend.editfood;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class FoodComparison {

    private final FoodInConflict original;

    private final FoodInConflict remote;

    private final FoodInConflict local;

    public FoodComparison(FoodInConflict original, FoodInConflict remote, FoodInConflict local) {
        this.original = original;
        this.remote = remote;
        this.local = local;
    }

    public boolean compareName(BiConsumer<String, Boolean> consumer) {
        return new FieldComparator<>(original.getName(), remote.getName(), local.getName()).compare(consumer);
    }

    public boolean compareStoreUnit(BiConsumer<Integer, Boolean> consumer) {
        return new FieldComparator<>(original.getStoreUnit(), remote.getStoreUnit(), local.getStoreUnit()).compare(consumer);
    }

    public boolean compareLocation(BiConsumer<Integer, Boolean> consumer) {
        return new FieldComparator<>(original.getLocation(), remote.getLocation(), local.getLocation()).compare(consumer);
    }

    public boolean compareExpirationOffset(BiConsumer<Integer, Boolean> consumer) {
        return new FieldComparator<>(original.getExpirationOffset(), remote.getExpirationOffset(), local.getExpirationOffset()).compare(consumer);
    }

    public boolean compareDescription(Function<Integer, String> stringProvider, BiConsumer<String, Boolean> consumer) {
        return new MergingTextFieldComparator(original.getDescription(), remote.getDescription(), local.getDescription())
                .compare(stringProvider, consumer);
    }
}
