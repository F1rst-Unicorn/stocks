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

package de.njsm.stocks.android.db.entities;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import org.threeten.bp.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE)
@Entity(primaryKeys = {"_id", "version", "transaction_time_start"})
public class Food extends VersionedData {

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "to_buy")
    public boolean toBuy;

    @ColumnInfo(name = "expiration_offset")
    public int expirationOffset;

    @ColumnInfo(name = "location")
    public int location;

    @ColumnInfo(name = "description", defaultValue = "")
    @NonNull
    public String description;

    public Food(String name, boolean toBuy, int expirationOffset, int location, @NonNull String description) {
        this.name = name;
        this.toBuy = toBuy;
        this.expirationOffset = expirationOffset;
        this.location = location;
        this.description = description;
    }

    public Food(int position, int id, @NonNull Instant validTimeStart, @NonNull Instant validTimeEnd, @NonNull Instant transactionTimeStart, @NonNull Instant transactionTimeEnd, int version, int initiates, String name, boolean toBuy, int expirationOffset, int location, @NonNull String description) {
        super(id, validTimeStart, validTimeEnd, transactionTimeStart, transactionTimeEnd, version, initiates);
        this.name = name;
        this.toBuy = toBuy;
        this.expirationOffset = expirationOffset;
        this.location = location;
        this.description = description;
        setPosition(position);
    }

    @Ignore
    public Food() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Food food = (Food) o;

        if (toBuy != food.toBuy) return false;
        if (expirationOffset != food.expirationOffset) return false;
        if (location != food.location) return false;
        return name.equals(food.name);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (toBuy ? 1 : 0);
        result = 31 * result + expirationOffset;
        result = 31 * result + location;
        return result;
    }
}
