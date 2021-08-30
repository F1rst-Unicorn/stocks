/* stocks is client-server program to manage a household's food stock
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
 */

package de.njsm.stocks.android.db.views;

import androidx.annotation.NonNull;
import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.db.entities.VersionedData;

import java.time.Instant;

public class FoodWithLatestItemView extends VersionedData {

    private final int amount;

    private final Instant eatBy;

    private final String name;

    private final boolean toBuy;

    private final int expirationOffset;

    private final int location;

    private final String description;

    private final int storeUnit;

    public FoodWithLatestItemView(int id, @NonNull Instant validTimeStart, @NonNull Instant validTimeEnd, @NonNull Instant transactionTimeStart, @NonNull Instant transactionTimeEnd, int version, int initiates, int amount, Instant eatBy, String name, boolean toBuy, int expirationOffset, int location, String description, int storeUnit) {
        super(id, validTimeStart, validTimeEnd, transactionTimeStart, transactionTimeEnd, version, version);
        this.amount = amount;
        this.eatBy = eatBy;
        this.name = name;
        this.toBuy = toBuy;
        this.expirationOffset = expirationOffset;
        this.location = location;
        this.description = description;
        this.storeUnit = storeUnit;
    }

    public Food mapToFood() {
        return new Food(id, validTimeStart, validTimeEnd, transactionTimeStart, transactionTimeEnd, version, initiates, name, toBuy, expirationOffset, location, description, storeUnit);
    }

    public int getAmount() {
        return amount;
    }

    public Instant getEatBy() {
        return eatBy;
    }

    public String getName() {
        return name;
    }

    public boolean getToBuy() {
        return toBuy;
    }

    public int getExpirationOffset() {
        return expirationOffset;
    }

    public int getLocation() {
        return location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FoodWithLatestItemView foodWithLatestItemView = (FoodWithLatestItemView) o;

        if (amount != foodWithLatestItemView.amount) return false;
        if (!eatBy.equals(foodWithLatestItemView.eatBy)) return false;
        return name.equals(foodWithLatestItemView.name);
    }

    @Override
    public int hashCode() {
        int result = amount;
        result = 31 * result + eatBy.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
