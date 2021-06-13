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

package de.njsm.stocks.android.business.data.eventlog;

import org.threeten.bp.Instant;

import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.User;
import de.njsm.stocks.android.db.entities.UserDevice;
import de.njsm.stocks.android.db.entities.VersionedData;

public abstract class DeletedEntityEvent<T extends VersionedData> extends EntityEvent<T> {

    T entity;

    public DeletedEntityEvent(User initiatorUser, UserDevice initiatorDevice, T entity) {
        super(initiatorUser, initiatorDevice);
        this.entity = entity;
    }

    @Override
    public Instant getTime() {
        return entity.transactionTimeStart;
    }

    @Override
    public int getEventIconResource() {
        return R.drawable.baseline_delete_black_24;
    }

    @Override
    public T getEntity() {
        return entity;
    }
}
