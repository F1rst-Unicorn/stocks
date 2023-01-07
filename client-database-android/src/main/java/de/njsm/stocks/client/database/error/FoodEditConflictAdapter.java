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

package de.njsm.stocks.client.database.error;

import de.njsm.stocks.client.database.FoodDbEntity;
import de.njsm.stocks.client.database.NullablePreservedId;
import de.njsm.stocks.client.database.PreservedId;

import java.time.Instant;
import java.time.Period;

public abstract class FoodEditConflictAdapter {

    FoodDbEntity remote;

    static FoodEditConflictAdapter fromFoodEdit(FoodEditEntity foodEdit) {
        return new FoodEditConflictAdapter() {
            @Override
            public PreservedId food() {
                return foodEdit.food();
            }

            @Override
            public String name() {
                return foodEdit.name();
            }

            @Override
            public boolean toBuy() {
                return remote.toBuy();
            }

            @Override
            public PreservedId storeUnit() {
                return foodEdit.storeUnit();
            }

            @Override
            public NullablePreservedId location() {
                return foodEdit.location();
            }

            @Override
            public Instant executionTime() {
                return foodEdit.executionTime();
            }

            @Override
            public int version() {
                return foodEdit.version();
            }

            @Override
            public Period expirationOffset() {
                return foodEdit.expirationOffset();
            }

            @Override
            public String description() {
                return foodEdit.description();
            }
        };
    }

    static FoodEditConflictAdapter fromFoodToBuy(FoodToBuyEntity food) {
        return new FoodEditConflictAdapter() {
            @Override
            public PreservedId food() {
                return food.food();
            }

            @Override
            public String name() {
                return remote.name();
            }

            @Override
            public boolean toBuy() {
                return food.toBuy();
            }

            @Override
            public PreservedId storeUnit() {
                return PreservedId.create(remote.storeUnit(), food.food().transactionTime());
            }

            @Override
            public NullablePreservedId location() {
                return NullablePreservedId.create(remote.location(), food.food().transactionTime());
            }

            @Override
            public Instant executionTime() {
                return food.executionTime();
            }

            @Override
            public int version() {
                return food.version();
            }

            @Override
            public Period expirationOffset() {
                return remote.expirationOffset();
            }

            @Override
            public String description() {
                return remote.description();
            }
        };
    }

    abstract PreservedId food();

    abstract String name();

    abstract boolean toBuy();

    abstract PreservedId storeUnit();

    abstract NullablePreservedId location();

    abstract Instant executionTime();

    abstract int version();

    abstract Period expirationOffset();

    abstract String description();

    void setRemote(FoodDbEntity remote) {
        this.remote = remote;
    }
}
