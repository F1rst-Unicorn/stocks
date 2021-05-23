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

import de.njsm.stocks.android.business.data.activity.differ.LocationDescriptionChangedGenerator;
import de.njsm.stocks.android.business.data.activity.differ.LocationNameChangedGenerator;
import de.njsm.stocks.android.db.entities.Location;
import de.njsm.stocks.android.db.entities.User;
import de.njsm.stocks.android.db.entities.UserDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

public class ChangedLocationEvent extends ChangedEntityEvent<Location> implements LocationIconResourceProvider {

    public ChangedLocationEvent(User initiatorUser, UserDevice initiatorDevice, Location oldEntity, Location newEntity) {
        super(initiatorUser, initiatorDevice, oldEntity, newEntity);
    }

    @Override
    protected List<PartialDiffGenerator<Location>> getDiffers(IntFunction<String> stringResourceResolver, SentenceObject object) {
        List<PartialDiffGenerator<Location>> differs = new ArrayList<>();
        differs.add(new LocationNameChangedGenerator(stringResourceResolver, oldEntity, newEntity, object));
        differs.add(new LocationDescriptionChangedGenerator(stringResourceResolver, oldEntity, newEntity, object));
        return differs;
    }

    @Override
    protected String getExplicitObject() {
        return oldEntity.name;
    }

    @Override
    public <I, O> O accept(EventVisitor<I, O> visitor, I arg) {
        return visitor.changedLocationEvent(this, arg);
    }
}
