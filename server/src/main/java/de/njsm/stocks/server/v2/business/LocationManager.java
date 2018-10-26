package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.server.v2.business.data.Location;
import de.njsm.stocks.server.v2.db.FoodItemHandler;
import de.njsm.stocks.server.v2.db.LocationHandler;
import fj.data.Validation;

import java.util.List;

public class LocationManager {

    private LocationHandler locationHandler;

    private FoodItemHandler foodItemHandler;

    public LocationManager(LocationHandler locationHandler,
                           FoodItemHandler foodItemHandler) {
        this.locationHandler = locationHandler;
        this.foodItemHandler = foodItemHandler;
    }

    public StatusCode put(Location location) {
        return locationHandler.add(location)
                .toEither().left().orValue(StatusCode.SUCCESS);
    }

    public Validation<StatusCode, List<Location>> get() {
        return locationHandler.get();
    }

    public StatusCode rename(Location item, String newName) {
        return locationHandler.rename(item, newName);
    }

    public StatusCode delete(Location l, boolean cascadeOnFoodItems) {
        if (cascadeOnFoodItems) {
            StatusCode deleteFoodResult = foodItemHandler.deleteItemsStoredIn(l);

            if (deleteFoodResult != StatusCode.SUCCESS)
                return deleteFoodResult;
        }

        return locationHandler.delete(l);
    }
}
