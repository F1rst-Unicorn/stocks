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

package de.njsm.stocks.android.db.views;

import androidx.room.Embedded;

import de.njsm.stocks.android.business.data.activity.ChangedEntityEvent;
import de.njsm.stocks.android.business.data.activity.DeletedEntityEvent;
import de.njsm.stocks.android.business.data.activity.EntityEvent;
import de.njsm.stocks.android.business.data.activity.NewEntityEvent;
import de.njsm.stocks.android.db.entities.VersionedData;
import de.njsm.stocks.android.util.Config;

public abstract class AbstractHistoryView<T extends VersionedData> {

    @Embedded(prefix = "version1_")
    T version1;

    @Embedded(prefix = "version2_")
    T version2;

    public AbstractHistoryView(T version1, T version2) {
        this.version1 = version1;
        this.version2 = version2;
    }

    abstract NewEntityEvent<?> getNewEntityEvent();

    abstract ChangedEntityEvent<?> getChangedEntityEvent();

    abstract DeletedEntityEvent<?> getDeletedEntityEvent();

    public EntityEvent<?> mapToEvent() {
        if (version1.version == 0 && version2 == null && version1.validTimeEnd.equals(Config.API_INFINITY)) {
            return getNewEntityEvent();
        } else if (version2 != null) {
            return getChangedEntityEvent();
        } else {
            return getDeletedEntityEvent();
        }
    }
}
