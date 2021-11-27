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

package de.njsm.stocks.clientold.business.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import de.njsm.stocks.clientold.business.data.visitor.AbstractVisitor;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Location extends VersionedData {

    public String name;

    public Location(int id, int version, String name) {
        super(id, version);
        this.name = name;
    }

    public Location(String name) {
        this.name = name;
    }

    public Location() {
    }

    @Override
    public <I, O> O accept(AbstractVisitor<I, O> visitor, I arg) {
        return visitor.location(this, arg);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location other = (Location) o;
        return id == other.id &&
                version == other.version &&
                Objects.equals(name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, version);
    }

    @Override
    public String toString() {
        return "Location (" + id + ", " + name + ", " + version + ")";
    }
}
