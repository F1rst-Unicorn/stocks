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

package de.njsm.stocks.common.api;

import de.njsm.stocks.common.api.visitor.InsertableVisitor;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public class FoodItemForInsertion implements Insertable<FoodItem> {

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
    public boolean isContainedIn(FoodItem entity) {
        return eatByDate.equals(entity.eatByDate()) &&
                ofType == entity.ofType() &&
                storedIn == entity.storedIn() &&
                registers == entity.registers() &&
                buys == entity.buys() &&
                unit.map(v -> v.equals(entity.unit())).orElse(true);
    }

    @Override
    public <I, O> O accept(InsertableVisitor<I, O> visitor, I argument) {
        return visitor.foodItemForInsertion(this, argument);
    }
}
