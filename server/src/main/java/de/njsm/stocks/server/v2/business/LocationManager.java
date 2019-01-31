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
        this.locationHandler = locationHandler;
        this.foodItemHandler = foodItemHandler;
    }

    public StatusCode put(Location location) {
        StatusCode result = locationHandler.add(location)
                .toEither().left().orValue(StatusCode.SUCCESS);
        return finishTransaction(result, locationHandler);
    }

    public Validation<StatusCode, List<Location>> get() {
        return finishTransaction(locationHandler.get(), locationHandler);
    }

    public StatusCode rename(Location item, String newName) {
        return finishTransaction(locationHandler.rename(item, newName), locationHandler);
    }

    public StatusCode delete(Location l, boolean cascadeOnFoodItems) {
        if (cascadeOnFoodItems) {
            StatusCode deleteFoodResult = foodItemHandler.deleteItemsStoredIn(l);

            if (deleteFoodResult != StatusCode.SUCCESS)
                return finishTransaction(deleteFoodResult, foodItemHandler);
        }

        StatusCode result = locationHandler.delete(l);
        return finishTransaction(result, locationHandler);
    }
}
