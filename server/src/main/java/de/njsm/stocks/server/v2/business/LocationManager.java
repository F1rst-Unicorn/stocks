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

    public StatusCode rename(Location item, String newName) {
        return runOperation(() -> locationHandler.rename(item, newName));
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
