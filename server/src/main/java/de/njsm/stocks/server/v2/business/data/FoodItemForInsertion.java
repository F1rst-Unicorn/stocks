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

package de.njsm.stocks.server.v2.business.data;

import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.db.jooq.tables.records.FoodItemRecord;
import org.jooq.InsertOnDuplicateStep;
import org.jooq.InsertSetStep;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

import static de.njsm.stocks.server.v2.db.jooq.Tables.FOOD_ITEM;

public class FoodItemForInsertion implements Insertable<FoodItemRecord, FoodItem> {

    private final Instant eatByDate;

    private final int ofType;

    private final int storedIn;

    private final int registers;

    private final int buys;

    public FoodItemForInsertion(Instant eatByDate, int ofType, int storedIn, int registers, int buys) {
        this.eatByDate = eatByDate;
        this.ofType = ofType;
        this.storedIn = storedIn;
        this.registers = registers;
        this.buys = buys;
    }

    public Instant getEatByDate() {
        return eatByDate;
    }

    public int getOfType() {
        return ofType;
    }

    public int getStoredIn() {
        return storedIn;
    }

    public int getRegisters() {
        return registers;
    }

    public Identifiable<Food> getOfTypeFood() {
        return () -> ofType;
    }

    public int getBuys() {
        return buys;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FoodItemForInsertion that = (FoodItemForInsertion) o;
        return getOfType() == that.getOfType() && getStoredIn() == that.getStoredIn() && getRegisters() == that.getRegisters() && getBuys() == that.getBuys() && getEatByDate().equals(that.getEatByDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEatByDate(), getOfType(), getStoredIn(), getRegisters(), getBuys());
    }

    @Override
    public InsertOnDuplicateStep<FoodItemRecord> insertValue(InsertSetStep<FoodItemRecord> insertInto, Principals principals) {
        return insertInto.columns(FOOD_ITEM.EAT_BY,
                FOOD_ITEM.STORED_IN,
                FOOD_ITEM.OF_TYPE,
                FOOD_ITEM.REGISTERS,
                FOOD_ITEM.BUYS,
                FOOD_ITEM.INITIATES)
                .values(OffsetDateTime.from(eatByDate.atOffset(ZoneOffset.UTC)),
                        storedIn,
                        ofType,
                        registers,
                        buys,
                        principals.getDid());
    }
}
