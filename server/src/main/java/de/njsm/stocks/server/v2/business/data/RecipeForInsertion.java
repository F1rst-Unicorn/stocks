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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.db.jooq.tables.records.RecipeRecord;
import org.jooq.InsertOnDuplicateStep;
import org.jooq.InsertSetStep;

import java.time.Duration;

import static de.njsm.stocks.server.v2.db.jooq.Tables.RECIPE;

@AutoValue
@JsonDeserialize(builder = AutoValue_RecipeForInsertion.class)
public abstract class RecipeForInsertion implements Insertable<RecipeRecord, Recipe>, Validatable {

    public abstract String name();

    public abstract String instructions();

    public abstract Duration duration();

    public static Builder builder() {
        return new AutoValue_RecipeForInsertion.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder name(String v);

        public abstract Builder instructions(String v);

        public abstract Builder duration(Duration v);

        public abstract RecipeForInsertion build();
    }

    @Override
    public InsertOnDuplicateStep<RecipeRecord> insertValue(InsertSetStep<RecipeRecord> arg, Principals principals) {
        return arg.columns(RECIPE.NAME, RECIPE.INSTRUCTIONS, RECIPE.DURATION, RECIPE.INITIATES)
                .values(name(), instructions(), duration(), principals.getDid());
    }

    @Override
    public boolean isContainedIn(Recipe entity) {
        return name().equals(entity.getName()) &&
                instructions().equals(entity.getInstructions()) &&
                duration().equals(entity.getDuration());
    }

    @Override
    public boolean isValid() {
        return name() != null && instructions() != null && duration() != null;
    }
}
