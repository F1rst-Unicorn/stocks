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

import com.fasterxml.jackson.annotation.JsonGetter;
import com.google.common.base.Preconditions;

public interface UserDevice extends Entity<UserDevice> {

    @JsonGetter
    String name();

    @JsonGetter
    int belongsTo();

    interface Builder<T> {

        T name(String v);

        T belongsTo(int v);
    }

    @Override
    default boolean isContainedIn(UserDevice item, boolean increment) {
        return Entity.super.isContainedIn(item, increment) &&
                name().equals(item.name()) &&
                belongsTo() == item.belongsTo();
    }

    @Override
    default void validate() {
        Entity.super.validate();
        Preconditions.checkState(belongsTo() > 0, "belongsTo id is invalid");
    }
}
