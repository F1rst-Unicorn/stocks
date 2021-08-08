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


import java.util.Objects;

public class UserForGetting extends VersionedData implements Versionable<User>, User {

    private final String name;

    public UserForGetting(int id, int version, String name) {
        super(id, version);
        this.name = name;
    }

    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UserForGetting that = (UserForGetting) o;
        return name().equals(that.name());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name());
    }

    @Override
    public boolean isContainedIn(User item, boolean increment) {
        return User.super.isContainedIn(item, increment) &&
                name.equals(item.name());
    }
}
