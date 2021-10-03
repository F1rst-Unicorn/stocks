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

package de.njsm.stocks.android.frontend.locations;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import de.njsm.stocks.android.db.entities.Location;
import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.android.repo.LocationRepository;
import de.njsm.stocks.android.util.Logger;

public class LocationViewModel extends ViewModel {

    private static final Logger LOG = new Logger(LocationViewModel.class);

    private LiveData<List<Location>> locations;

    private LiveData<Location> singleLocation;

    private LocationRepository locationRepo;

    @Inject
    public LocationViewModel(LocationRepository locationRepo) {
        this.locationRepo = locationRepo;
    }

    public void init() {
        if (locations == null) {
            LOG.d("Initialising");
            locations = locationRepo.getLocations();
        }
    }

    public void init(int id) {
        if (singleLocation == null) {
            LOG.d("Initialising");
            singleLocation = locationRepo.getLocation(id);
        }
    }

    public LiveData<List<Location>> getLocations() {
        return locations;
    }

    public LiveData<Location> getPreparedLocation() {
        return singleLocation;
    }

    public LiveData<Location> getLocation(int id) {
        return locationRepo.getLocation(id);
    }

    public LiveData<Location> getLocationWithMostItemsOfType(int food) {
        return locationRepo.getLocationWithMostItemsOfType(food);
    }

    LiveData<StatusCode> addLocation(String name) {
        return locationRepo.addLocation(name);
    }

    LiveData<StatusCode> renameLocation(Location item, String newName) {
        return locationRepo.renameLocation(item, newName);
    }

    LiveData<StatusCode> deleteLocation(Location item, boolean cascade) {
        return locationRepo.deleteLocation(item, cascade);
    }

    public LiveData<StatusCode> setLocationDescription(int id, int version, String description) {
        return locationRepo.setDescription(id, version, description);
    }
}
