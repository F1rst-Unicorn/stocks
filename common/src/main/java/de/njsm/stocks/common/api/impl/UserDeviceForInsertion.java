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

package de.njsm.stocks.common.api.impl;

import de.njsm.stocks.common.api.Insertable;
import de.njsm.stocks.common.api.UserDevice;
import de.njsm.stocks.common.api.visitor.InsertableVisitor;

import java.util.Objects;

public class UserDeviceForInsertion implements Insertable<UserDevice> {

    private final String name;

    private final int belongsTo;

    public UserDeviceForInsertion(String name, int belongsTo) {
        this.name = name;
        this.belongsTo = belongsTo;
    }

    public String getName() {
        return name;
    }

    public int getBelongsTo() {
        return belongsTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDeviceForInsertion that = (UserDeviceForInsertion) o;
        return getBelongsTo() == that.getBelongsTo() && getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getBelongsTo());
    }

    @Override
    public boolean isContainedIn(UserDevice entity) {
        return name.equals(entity.getName()) &&
                belongsTo == entity.getBelongsTo();
    }

    @Override
    public <I, O> O accept(InsertableVisitor<I, O> visitor, I argument) {
        return visitor.userDeviceForInsertion(this, argument);
    }
}
