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
import de.njsm.stocks.server.v2.db.jooq.tables.records.RecipeRecord;
import org.jooq.InsertOnDuplicateStep;
import org.jooq.InsertSetStep;

import java.time.Duration;
import java.util.Objects;

import static de.njsm.stocks.server.v2.db.jooq.Tables.RECIPE;

public class RecipeForInsertion implements Insertable<RecipeRecord, Recipe> {

    private final String name;

    private final String instructions;

    private final Duration duration;

    public RecipeForInsertion(String name, String instructions, Duration duration) {
        this.name = name;
        this.instructions = instructions;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public String getInstructions() {
        return instructions;
    }

    public Duration getDuration() {
        return duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecipeForInsertion)) return false;
        RecipeForInsertion that = (RecipeForInsertion) o;
        return getName().equals(that.getName()) && getInstructions().equals(that.getInstructions()) && getDuration().equals(that.getDuration());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getInstructions(), getDuration());
    }

    @Override
    public InsertOnDuplicateStep<RecipeRecord> insertValue(InsertSetStep<RecipeRecord> arg, Principals principals) {
        return arg.columns(RECIPE.NAME, RECIPE.INSTRUCTIONS, RECIPE.DURATION, RECIPE.INITIATES)
                .values(name, instructions, duration, principals.getDid());
    }

    @Override
    public boolean isContainedIn(Recipe entity) {
        return name.equals(entity.getName()) &&
                instructions.equals(entity.getInstructions()) &&
                duration.equals(entity.getDuration());
    }
}
