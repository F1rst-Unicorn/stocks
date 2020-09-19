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

import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.VersionedData;

public abstract class ChangedEntityEvent<T extends VersionedData> extends EntityEvent<T> {

    final T oldEntity;

    final T newEntity;

    public ChangedEntityEvent(T oldEntity, T newEntity) {
        this.oldEntity = oldEntity;
        this.newEntity = newEntity;
    }

    @Override
    public Instant getTime() {
        return oldEntity.transactionTimeStart;
    }

    @Override
    public int getEventIconResource() {
        return R.drawable.ic_create_black_24;
    }

    @Override
    public T getEntity() {
        return oldEntity;
    }
}
