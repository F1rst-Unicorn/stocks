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
import org.jooq.impl.DSL;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.Optional;

import static de.njsm.stocks.server.v2.db.jooq.Tables.FOOD_ITEM;
import static de.njsm.stocks.server.v2.db.jooq.Tables.SCALED_UNIT;

public class FoodItemForInsertion implements Insertable<FoodItemRecord, FoodItem> {

    private final Instant eatByDate;

    private final int ofType;

    private final int storedIn;

    private final int registers;

    private final int buys;

    private final Optional<Integer> unit;

    public FoodItemForInsertion(Instant eatByDate, int ofType, int storedIn, int registers, int buys, Integer unit) {
        this.eatByDate = eatByDate;
        this.ofType = ofType;
        this.storedIn = storedIn;
        this.registers = registers;
        this.buys = buys;
        this.unit = Optional.ofNullable(unit);
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

    public Optional<Integer> getUnit() {
        return unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FoodItemForInsertion)) return false;
        FoodItemForInsertion that = (FoodItemForInsertion) o;
        return getOfType() == that.getOfType() && getStoredIn() == that.getStoredIn() && getRegisters() == that.getRegisters() && getBuys() == that.getBuys() && getEatByDate().equals(that.getEatByDate()) && getUnit().equals(that.getUnit());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEatByDate(), getOfType(), getStoredIn(), getRegisters(), getBuys(), getUnit());
    }

    @Override
    public InsertOnDuplicateStep<FoodItemRecord> insertValue(InsertSetStep<FoodItemRecord> insertInto, Principals principals) {
        if (unit.isEmpty())
            return insertInto.columns(FOOD_ITEM.EAT_BY,
                    FOOD_ITEM.STORED_IN,
                    FOOD_ITEM.OF_TYPE,
                    FOOD_ITEM.REGISTERS,
                    FOOD_ITEM.BUYS,
                    FOOD_ITEM.INITIATES,
                    FOOD_ITEM.UNIT)
                    .select(DSL.select(
                            DSL.inline(OffsetDateTime.from(eatByDate.atOffset(ZoneOffset.UTC))),
                            DSL.inline(storedIn),
                            DSL.inline(ofType),
                            DSL.inline(registers),
                            DSL.inline(buys),
                            DSL.inline(principals.getDid()),
                            DSL.min(SCALED_UNIT.ID))
                            .from(SCALED_UNIT));
        else
            return insertInto.columns(FOOD_ITEM.EAT_BY,
                    FOOD_ITEM.STORED_IN,
                    FOOD_ITEM.OF_TYPE,
                    FOOD_ITEM.REGISTERS,
                    FOOD_ITEM.BUYS,
                    FOOD_ITEM.INITIATES,
                    FOOD_ITEM.UNIT)
                    .values(OffsetDateTime.from(eatByDate.atOffset(ZoneOffset.UTC)),
                            storedIn,
                            ofType,
                            registers,
                            buys,
                            principals.getDid(),
                            unit.get());

    }

    @Override
    public boolean isContainedIn(FoodItem entity) {
        return eatByDate.equals(entity.getEatByDate()) &&
                ofType == entity.getOfType() &&
                storedIn == entity.getStoredIn() &&
                registers == entity.getRegisters() &&
                buys == entity.getBuys() &&
                unit.map(v -> v.equals(entity.getUnit())).orElse(true);
    }
}
