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
import de.njsm.stocks.server.v2.db.jooq.tables.records.FoodRecord;
import org.jooq.InsertOnDuplicateStep;
import org.jooq.InsertSetStep;
import org.jooq.impl.DSL;

import java.util.Objects;
import java.util.Optional;

import static de.njsm.stocks.server.v2.db.jooq.Tables.FOOD;
import static de.njsm.stocks.server.v2.db.jooq.Tables.SCALED_UNIT;

public class FoodForInsertion implements Insertable<FoodRecord, Food> {

    private final String name;

    private final Optional<Integer> storeUnit;

    public FoodForInsertion(String name, Integer storeUnit) {
        this.name = name;
        this.storeUnit = Optional.ofNullable(storeUnit);
    }

    public String getName() {
        return name;
    }

    public Optional<Integer> getStoreUnit() {
        return storeUnit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FoodForInsertion)) return false;
        FoodForInsertion that = (FoodForInsertion) o;
        return getName().equals(that.getName()) && getStoreUnit().equals(that.getStoreUnit());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getStoreUnit());
    }

    @Override
    public InsertOnDuplicateStep<FoodRecord> insertValue(InsertSetStep<FoodRecord> insertInto, Principals principals) {
        if (storeUnit.isEmpty())
            return insertInto.columns(FOOD.NAME, FOOD.INITIATES, FOOD.STORE_UNIT)
                    .select(DSL.select(DSL.inline(name), DSL.inline(principals.getDid()), DSL.min(SCALED_UNIT.ID))
                            .from(SCALED_UNIT));

        else
            return insertInto.columns(FOOD.NAME, FOOD.INITIATES, FOOD.STORE_UNIT)
                    .values(name, principals.getDid(), storeUnit.get());
    }

    @Override
    public boolean isContainedIn(Food entity) {
        return name.equals(entity.getName()) &&
                storeUnit.map(v -> v.equals(entity.getStoreUnit())).orElse(true);
    }
}
