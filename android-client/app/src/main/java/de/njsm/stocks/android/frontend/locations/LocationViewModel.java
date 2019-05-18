package de.njsm.stocks.android.frontend.locations;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import de.njsm.stocks.android.db.entities.Location;
import de.njsm.stocks.android.network.server.StatusCode;
import de.njsm.stocks.android.repo.LocationRepository;
import de.njsm.stocks.android.util.Logger;

import javax.inject.Inject;
import java.util.List;

public class LocationViewModel extends ViewModel {

    private static final Logger LOG = new Logger(LocationViewModel.class);

    private LiveData<List<Location>> locations;

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

    LiveData<List<Location>> getLocations() {
        return locations;
    }

    public LiveData<Location> getLocation(int id) {
        return locationRepo.getLocation(id);
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
}
