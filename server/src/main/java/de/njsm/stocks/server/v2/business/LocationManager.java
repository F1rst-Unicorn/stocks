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

package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.server.v2.business.data.Location;
import de.njsm.stocks.server.v2.db.FoodItemHandler;
import de.njsm.stocks.server.v2.db.LocationHandler;
import fj.data.Validation;

import java.util.List;

public class LocationManager extends BusinessObject {

    private LocationHandler locationHandler;

    private FoodItemHandler foodItemHandler;

    public LocationManager(LocationHandler locationHandler,
                           FoodItemHandler foodItemHandler) {
        super(locationHandler);
        this.locationHandler = locationHandler;
        this.foodItemHandler = foodItemHandler;
    }

    public StatusCode put(Location location) {
        return runOperation(() -> locationHandler.add(location)
                .toEither().left().orValue(StatusCode.SUCCESS));
    }

    public Validation<StatusCode, List<Location>> get() {
        return runFunction(() -> {
            locationHandler.setReadOnly();
            return locationHandler.get();
        });
    }

    public StatusCode rename(Location item) {
        return runOperation(() -> locationHandler.rename(item));
    }

    public StatusCode delete(Location l, boolean cascadeOnFoodItems) {
        return runOperation(() -> {
            if (cascadeOnFoodItems) {
                StatusCode deleteFoodResult = foodItemHandler.deleteItemsStoredIn(l);

                if (deleteFoodResult != StatusCode.SUCCESS)
                    return deleteFoodResult;
            }

            return locationHandler.delete(l);
        });
    }
}
