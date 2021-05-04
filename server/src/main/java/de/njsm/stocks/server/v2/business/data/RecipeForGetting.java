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

import java.time.Period;
import java.util.Objects;

public class RecipeForGetting extends VersionedData implements Recipe {

    private final String name;

    private final String instructions;

    private final Period duration;

    public RecipeForGetting(int id, int version, String name, String instructions, Period duration) {
        super(id, version);
        this.name = name;
        this.instructions = instructions;
        this.duration = duration;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getInstructions() {
        return instructions;
    }

    @Override
    public Period getDuration() {
        return duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecipeForGetting)) return false;
        if (!super.equals(o)) return false;
        RecipeForGetting that = (RecipeForGetting) o;
        return getName().equals(that.getName()) && getInstructions().equals(that.getInstructions()) && getDuration().equals(that.getDuration());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getName(), getInstructions(), getDuration());
    }

    @Override
    public boolean isContainedIn(Recipe entity) {
        return Recipe.super.isContainedIn(entity) &&
                name.equals(entity.getName()) &&
                instructions.equals(entity.getInstructions()) &&
                duration.equals(entity.getDuration());
    }
}
