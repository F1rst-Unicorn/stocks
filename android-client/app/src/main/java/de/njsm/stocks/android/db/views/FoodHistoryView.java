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
import de.njsm.stocks.android.business.data.activity.ChangedFoodEvent;
import de.njsm.stocks.android.business.data.activity.DeletedEntityEvent;
import de.njsm.stocks.android.business.data.activity.DeletedFoodEvent;
import de.njsm.stocks.android.business.data.activity.NewEntityEvent;
import de.njsm.stocks.android.business.data.activity.NewFoodEvent;
import de.njsm.stocks.android.db.entities.User;
import de.njsm.stocks.android.db.entities.UserDevice;

public class FoodHistoryView extends AbstractHistoryView<FoodWithLocationName> {

    public FoodHistoryView(FoodWithLocationName version1, FoodWithLocationName version2, boolean isFirst, User initiatorUser, UserDevice initiatorUserDevice) {
        super(version1, version2, isFirst, initiatorUser, initiatorUserDevice);
    }

    @Override
    NewEntityEvent<?> getNewEntityEvent() {
        return new NewFoodEvent(initiatorUser, initiatorUserDevice, version1);
    }

    @Override
    ChangedEntityEvent<?> getChangedEntityEvent() {
        return new ChangedFoodEvent(initiatorUser, initiatorUserDevice, version1, version2);
    }

    @Override
    DeletedEntityEvent<?> getDeletedEntityEvent() {
        return new DeletedFoodEvent(initiatorUser, initiatorUserDevice, version1);
    }
}
