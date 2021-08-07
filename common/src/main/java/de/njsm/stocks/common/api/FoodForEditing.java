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

import java.time.Period;
import java.util.Objects;
import java.util.Optional;

public class FoodForEditing extends VersionedData implements Versionable<Food> {

    private final String newName;

    private final Optional<Period> expirationOffset;

    private final Optional<Integer> location;

    private final Optional<String> description;

    private final Optional<Integer> storeUnit;

    public FoodForEditing(int id, int version, String newName, Period expirationOffset, Integer location, String description, Integer storeUnit) {
        super(id, version);
        this.newName = newName;
        this.expirationOffset = Optional.ofNullable(expirationOffset);
        this.location = Optional.ofNullable(location);
        this.description = Optional.ofNullable(description);
        this.storeUnit = Optional.ofNullable(storeUnit);
    }

    public String getNewName() {
        return newName;
    }

    public Optional<Integer> getLocationOptional() {
        return location;
    }

    public Optional<Period> getExpirationOffsetOptional() {
        return expirationOffset;
    }

    public Optional<String> getDescription() {
        return description;
    }

    public Optional<Integer> getStoreUnit() {
        return storeUnit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FoodForEditing that = (FoodForEditing) o;
        return getExpirationOffsetOptional().equals(that.getExpirationOffsetOptional())
                && getLocationOptional().equals(that.getLocationOptional())
                && getNewName().equals(that.getNewName())
                && getDescription().equals(that.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getNewName(), getExpirationOffsetOptional(), getLocationOptional(), getDescription(), getStoreUnit());
    }

    @Override
    public boolean isContainedIn(Food item) {
        return Versionable.super.isContainedIn(item) &&
                newName.equals(item.name()) &&
                location.map(v -> v.equals(item.location()) || (
                        v == 0 && item.location() == null
                        )).orElse(true) &&
                expirationOffset.map(v -> v.equals(item.expirationOffset())).orElse(true) &&
                description.map(v -> v.equals(item.description())).orElse(true) &&
                storeUnit.map(v -> v.equals(item.storeUnit())).orElse(true);
    }
}
