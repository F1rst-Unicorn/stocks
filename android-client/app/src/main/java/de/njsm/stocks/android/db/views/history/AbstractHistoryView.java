/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2021  The stocks developers
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

package de.njsm.stocks.android.db.views.history;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;

import de.njsm.stocks.android.business.data.eventlog.ChangedEntityEvent;
import de.njsm.stocks.android.business.data.eventlog.DeletedEntityEvent;
import de.njsm.stocks.android.business.data.eventlog.EntityEvent;
import de.njsm.stocks.android.business.data.eventlog.NewEntityEvent;
import de.njsm.stocks.android.db.entities.User;
import de.njsm.stocks.android.db.entities.UserDevice;
import de.njsm.stocks.android.db.entities.VersionedData;

public abstract class AbstractHistoryView<T extends VersionedData> {

    @Embedded(prefix = "version1_")
    T version1;

    @Embedded(prefix = "version2_")
    T version2;

    @ColumnInfo(name = "is_first")
    boolean isFirst;

    @Embedded(prefix = "initiator_user_")
    User initiatorUser;

    @Embedded(prefix = "initiator_user_device_")
    UserDevice initiatorUserDevice;

    public AbstractHistoryView(T version1, T version2, boolean isFirst, User initiatorUser, UserDevice initiatorUserDevice) {
        this.version1 = version1;
        this.version2 = version2;
        this.isFirst = isFirst;
        this.initiatorUser = initiatorUser;
        this.initiatorUserDevice = initiatorUserDevice;
    }

    abstract NewEntityEvent<?> getNewEntityEvent();

    abstract ChangedEntityEvent<?> getChangedEntityEvent();

    abstract DeletedEntityEvent<?> getDeletedEntityEvent();

    public EntityEvent<?> mapToEvent() {
        if (version2 != null) {
            return getChangedEntityEvent();
        } else if (isFirst) {
            return getNewEntityEvent();
        } else {
            return getDeletedEntityEvent();
        }
    }
}
