/*
 * stocks is client-server program to manage a household's food stock
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
 *
 */

package de.njsm.stocks.server.v2.business;


import de.njsm.stocks.common.api.*;
import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.db.FoodHandler;
import de.njsm.stocks.server.v2.db.FoodItemHandler;
import de.njsm.stocks.server.v2.db.LocationHandler;
import de.njsm.stocks.server.v2.db.jooq.tables.records.LocationRecord;

public class LocationManager extends BusinessObject<LocationRecord, Location> implements
        BusinessGettable<LocationRecord, Location>,
        BusinessAddable<LocationRecord, Location>,
        BusinessDeletable<LocationForDeletion, Location> {

    private final LocationHandler locationHandler;

    private final FoodItemHandler foodItemHandler;

    private final FoodHandler foodHandler;

    public LocationManager(LocationHandler locationHandler,
                           FoodHandler foodHandler, FoodItemHandler foodItemHandler) {
        super(locationHandler);
        this.locationHandler = locationHandler;
        this.foodHandler = foodHandler;
        this.foodItemHandler = foodItemHandler;
    }

    public StatusCode put(LocationForInsertion location) {
        return add(location);
    }

    public StatusCode rename(LocationForRenaming item) {
        return runOperation(() -> locationHandler.rename(item));
    }

    public StatusCode edit(LocationForEditing item) {
        return runOperation(() -> locationHandler.edit(item));
    }

    public StatusCode delete(LocationForDeletion l) {
        return runOperation(() -> {
            if (l.cascade()) {
                StatusCode deleteFoodResult = foodItemHandler.deleteItemsStoredIn(l);

                if (deleteFoodResult != StatusCode.SUCCESS)
                    return deleteFoodResult;
            }

            return foodHandler.unregisterDefaultLocation(l)
                    .bind(() -> locationHandler.delete(l));
        });
    }

    @Override
    public void setPrincipals(Principals principals) {
        super.setPrincipals(principals);
        foodHandler.setPrincipals(principals);
        foodItemHandler.setPrincipals(principals);
    }

    public StatusCode setDescription(LocationForSetDescription data) {
        return runOperation(() -> locationHandler.setDescription(data));
    }
}
