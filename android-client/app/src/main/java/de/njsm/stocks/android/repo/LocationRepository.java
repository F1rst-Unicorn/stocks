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

package de.njsm.stocks.android.repo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import de.njsm.stocks.android.db.dao.LocationDao;
import de.njsm.stocks.android.db.entities.Location;
import de.njsm.stocks.android.network.server.ServerClient;
import de.njsm.stocks.android.network.server.StatusCodeCallback;
import de.njsm.stocks.android.util.Logger;
import de.njsm.stocks.android.util.idling.IdlingResource;
import de.njsm.stocks.common.api.StatusCode;

import javax.inject.Inject;
import java.util.List;

public class LocationRepository {

    private static final Logger LOG = new Logger(LocationRepository.class);

    private final LocationDao locationDao;

    private final ServerClient webClient;

    private final Synchroniser synchroniser;

    private final IdlingResource idlingResource;

    @Inject
    public LocationRepository(LocationDao locationDao,
                              ServerClient webClient,
                              Synchroniser synchroniser,
                              IdlingResource idlingResource) {
        this.locationDao = locationDao;
        this.webClient = webClient;
        this.synchroniser = synchroniser;
        this.idlingResource = idlingResource;
    }

    public LiveData<List<Location>> getLocations() {
        LOG.d("getting locations");
        return locationDao.getAll();
    }

    public LiveData<Location> getLocation(int locationId) {
        LOG.d("Getting location for id " + locationId);
        return locationDao.getLocation(locationId);
    }

    public LiveData<StatusCode> addLocation(String name) {
        LOG.d("adding location " + name);
        MediatorLiveData<StatusCode> data = new MediatorLiveData<>();

        webClient.addLocation(name)
                .enqueue(new StatusCodeCallback(data, synchroniser, idlingResource));
        return data;
    }

    public LiveData<StatusCode> renameLocation(Location entity, String newName) {
        LOG.d("renaming location " + entity + " to " + newName);
        MediatorLiveData<StatusCode> data = new MediatorLiveData<>();
        webClient.renameLocation(entity.id, entity.version, newName)
                .enqueue(new StatusCodeCallback(data, synchroniser, idlingResource));
        return data;
    }

    public LiveData<StatusCode> setDescription(int id, int version, String description) {
        LOG.d("editing description of location " + id);
        MediatorLiveData<StatusCode> data = new MediatorLiveData<>();
        webClient.setLocationDescription(id, version, description)
                .enqueue(new StatusCodeCallback(data, synchroniser, idlingResource));
        return data;
    }

    public LiveData<StatusCode> deleteLocation(Location entity, boolean cascade) {
        LOG.d("deleting location " + entity);
        MediatorLiveData<StatusCode> data = new MediatorLiveData<>();
        webClient.deleteLocation(entity.id, entity.version, cascade ? 1 : 0)
                .enqueue(new StatusCodeCallback(data, synchroniser, idlingResource));
        return data;
    }

    public LiveData<Location> getLocationWithMostItemsOfType(int food) {
        LOG.d("Getting location with max items of " + food);
        return locationDao.getLocationWithMostItemsOfType(food);
    }
}
