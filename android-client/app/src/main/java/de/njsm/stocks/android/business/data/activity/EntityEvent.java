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

package de.njsm.stocks.android.business.data.activity;

import org.threeten.bp.Instant;

import java.util.function.IntFunction;

import de.njsm.stocks.android.db.entities.VersionedData;
import de.njsm.stocks.android.util.paging.Key;

public abstract class EntityEvent<T extends VersionedData> implements Comparable<EntityEvent<?>>, EntityIconResourceProvider {

    private Key key;

    public abstract String describe(IntFunction<String> stringResourceResolver);

    public abstract Instant getTime();

    public abstract int getEventIconResource();

    public abstract VersionedData getEntity();

    public boolean equals(EntityEvent<?> obj) {
        VersionedData me = getEntity();
        VersionedData other = obj.getEntity();
        return me.id == other.id &&
                me.version == other.version &&
                me.transactionTimeStart.equals(other.transactionTimeStart);
    }

    @Override
    public int compareTo(EntityEvent<?> o) {
        return getTime().compareTo(o.getTime()) * -1;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public Key getKey() {
        return key;
    }
}
