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

import de.njsm.stocks.android.business.data.activity.ChangedEntityEvent;
import de.njsm.stocks.android.business.data.activity.ChangedLocationEvent;
import de.njsm.stocks.android.business.data.activity.DeletedEntityEvent;
import de.njsm.stocks.android.business.data.activity.DeletedLocationEvent;
import de.njsm.stocks.android.business.data.activity.NewEntityEvent;
import de.njsm.stocks.android.business.data.activity.NewLocationEvent;
import de.njsm.stocks.android.db.entities.Location;

public class LocationHistoryView extends AbstractHistoryView<Location> {

    public LocationHistoryView(Location version1, Location version2) {
        super(version1, version2);
    }

    @Override
    NewEntityEvent<?> getNewEntityEvent() {
        return new NewLocationEvent(version1);
    }

    @Override
    ChangedEntityEvent<?> getChangedEntityEvent() {
        return new ChangedLocationEvent(version1, version2);
    }

    @Override
    DeletedEntityEvent<?> getDeletedEntityEvent() {
        return new DeletedLocationEvent(version1);
    }
}
