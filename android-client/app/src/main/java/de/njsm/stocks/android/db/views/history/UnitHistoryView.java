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

import de.njsm.stocks.android.business.data.eventlog.*;
import de.njsm.stocks.android.db.entities.Unit;
import de.njsm.stocks.android.db.entities.User;
import de.njsm.stocks.android.db.entities.UserDevice;

public class UnitHistoryView extends AbstractHistoryView<Unit> {

    public UnitHistoryView(Unit version1, Unit version2, boolean isFirst, User initiatorUser, UserDevice initiatorUserDevice) {
        super(version1, version2, isFirst, initiatorUser, initiatorUserDevice);
    }

    @Override
    NewEntityEvent<?> getNewEntityEvent() {
        return new NewUnitEvent(initiatorUser, initiatorUserDevice, version1);
    }

    @Override
    ChangedEntityEvent<?> getChangedEntityEvent() {
        return new ChangedUnitEvent(initiatorUser, initiatorUserDevice, version1, version2);
    }

    @Override
    DeletedEntityEvent<?> getDeletedEntityEvent() {
        return new DeletedUnitEvent(initiatorUser, initiatorUserDevice, version1);
    }
}