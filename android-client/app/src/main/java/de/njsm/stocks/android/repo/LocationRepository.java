package de.njsm.stocks.android.repo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import de.njsm.stocks.android.db.dao.LocationDao;
import de.njsm.stocks.android.db.entities.Location;
import de.njsm.stocks.android.network.server.ServerClient;
import de.njsm.stocks.android.network.server.StatusCode;
import de.njsm.stocks.android.network.server.StatusCodeCallback;
import de.njsm.stocks.android.util.Logger;

import javax.inject.Inject;
import java.util.List;

public class LocationRepository {

    private static final Logger LOG = new Logger(LocationRepository.class);

    private LocationDao locationDao;

    private ServerClient webClient;

    private Synchroniser synchroniser;

    @Inject
    public LocationRepository(LocationDao locationDao,
                              ServerClient webClient,
                              Synchroniser synchroniser) {
        this.locationDao = locationDao;
        this.webClient = webClient;
        this.synchroniser = synchroniser;
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
                .enqueue(new StatusCodeCallback(data, synchroniser));
        return data;
    }

    public LiveData<StatusCode> renameLocation(Location entity, String newName) {
        LOG.d("renaming location " + entity + " to " + newName);
        MediatorLiveData<StatusCode> data = new MediatorLiveData<>();
        webClient.renameLocation(entity.id, entity.version, newName)
                .enqueue(new StatusCodeCallback(data, synchroniser));
        return data;
    }

    public LiveData<StatusCode> deleteLocation(Location entity, boolean cascade) {
        LOG.d("deleting location " + entity);
        MediatorLiveData<StatusCode> data = new MediatorLiveData<>();
        webClient.deleteLocation(entity.id, entity.version, cascade ? 1 : 0)
                .enqueue(new StatusCodeCallback(data, synchroniser));
        return data;
    }
}
