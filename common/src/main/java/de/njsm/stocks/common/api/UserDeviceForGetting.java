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

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;

public class UserDeviceForGetting extends VersionedData implements Versionable<UserDevice>, UserDevice {
    private final String name;

    private final int belongsTo;

    public UserDeviceForGetting(int id, int version, String name, int belongsTo) {
        super(id, version);
        this.name = name;
        this.belongsTo = belongsTo;
    }

    public String name() {
        return name;
    }

    @JsonIgnore
    public int belongsTo() {
        return belongsTo;
    }

    /**
     * JSON property name. Keep for backward compatibility
     */
    public int getUserId() {
        return belongsTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UserDeviceForGetting that = (UserDeviceForGetting) o;
        return belongsTo() == that.belongsTo() && name().equals(that.name());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name(), belongsTo());
    }

    @Override
    public boolean isContainedIn(UserDevice item) {
        return UserDevice.super.isContainedIn(item) &&
                name.equals(item.name()) &&
                belongsTo == item.belongsTo();
    }
}
