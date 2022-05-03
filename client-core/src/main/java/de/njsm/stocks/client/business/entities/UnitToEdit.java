/*
 * stocks is client-server program to manage a household's food stock
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
 *
 */

package de.njsm.stocks.client.business.entities;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class UnitToEdit implements Identifiable<Unit>, UnitFields {

    public static UnitToEdit create(int id, String name, String abbreviation) {
        return UnitToEdit.builder()
                .id(id)
                .name(name)
                .abbreviation(abbreviation)
                .build();
    }

    public UnitForEditing addVersion(int version) {
        return UnitForEditing.builder()
                .id(id())
                .version(version)
                .name(name())
                .abbreviation(abbreviation())
                .build();
    }

    public static Builder builder() {
        return new AutoValue_UnitToEdit.Builder();
    }

    public boolean isContainedIn(UnitForEditing currentState) {
        return id() == id() &&
                name().equals(currentState.name()) &&
                abbreviation().equals(currentState.abbreviation());
    }

    @AutoValue.Builder
    public abstract static class Builder
            implements Identifiable.Builder<Builder>, UnitFields.Builder<Builder> {
        public abstract UnitToEdit build();
    }
}
