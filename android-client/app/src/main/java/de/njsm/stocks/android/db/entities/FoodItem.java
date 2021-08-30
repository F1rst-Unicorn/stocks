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
import androidx.room.Index;

import java.time.Instant;
import java.util.Objects;

@Entity(tableName = "fooditem", primaryKeys = {"_id", "version", "transaction_time_start"},
        indices = {
                @Index(value = {"_id", "valid_time_start", "valid_time_end"}, name = "fooditem_current"),
                @Index(value = {"_id"}, name = "fooditem_pkey"),
                @Index(value = {"transaction_time_start"}, name = "fooditem_transaction_time_start"),
                @Index(value = {"transaction_time_end"}, name = "fooditem_transaction_time_end"),
        })
public class FoodItem extends VersionedData {

    @ColumnInfo(name = "eat_by")
    @NonNull
    public Instant eatByDate;

    @ColumnInfo(name = "of_type")
    @NonNull
    public int ofType;

    @ColumnInfo(name = "stored_in")
    @NonNull
    public int storedIn;

    @ColumnInfo(name = "registers")
    @NonNull
    public int registers;

    @ColumnInfo(name = "buys")
    @NonNull
    public int buys;

    @ColumnInfo(name = "unit")
    @NonNull
    public int unit;

    public FoodItem(int id,
                    @NonNull Instant validTimeStart,
                    @NonNull Instant validTimeEnd,
                    @NonNull Instant transactionTimeStart,
                    @NonNull Instant transactionTimeEnd,
                    int version,
                    int initiates,
                    Instant eatByDate,
                    int ofType,
                    int storedIn,
                    int registers,
                    int buys,
                    int unit) {
        super(id, validTimeStart, validTimeEnd, transactionTimeStart, transactionTimeEnd, version, initiates);
        this.eatByDate = eatByDate;
        this.ofType = ofType;
        this.storedIn = storedIn;
        this.registers = registers;
        this.buys = buys;
        this.unit = unit;
    }

    @NonNull
    public Instant getEatByDate() {
        return eatByDate;
    }

    public void setEatByDate(@NonNull Instant eatByDate) {
        this.eatByDate = eatByDate;
    }

    public int getOfType() {
        return ofType;
    }

    public void setOfType(int ofType) {
        this.ofType = ofType;
    }

    public int getStoredIn() {
        return storedIn;
    }

    public void setStoredIn(int storedIn) {
        this.storedIn = storedIn;
    }

    public int getRegisters() {
        return registers;
    }

    public void setRegisters(int registers) {
        this.registers = registers;
    }

    public int getBuys() {
        return buys;
    }

    public void setBuys(int buys) {
        this.buys = buys;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FoodItem)) return false;
        FoodItem foodItem = (FoodItem) o;
        return ofType == foodItem.ofType && storedIn == foodItem.storedIn && registers == foodItem.registers && buys == foodItem.buys && unit == foodItem.unit && eatByDate.equals(foodItem.eatByDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eatByDate, ofType, storedIn, registers, buys, unit);
    }
}
