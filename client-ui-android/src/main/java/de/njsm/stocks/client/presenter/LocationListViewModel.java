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

package de.njsm.stocks.client.presenter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import de.njsm.stocks.client.business.EntityDeleteInteractor;
import de.njsm.stocks.client.business.LocationListInteractor;
import de.njsm.stocks.client.business.Synchroniser;
import de.njsm.stocks.client.business.entities.LocationForDeletion;
import de.njsm.stocks.client.business.entities.LocationForListing;

import javax.inject.Inject;
import java.util.List;
import java.util.function.Consumer;

public class LocationListViewModel extends ViewModel {

    private final LocationListInteractor locationListInteractor;

    private final EntityDeleteInteractor<LocationForDeletion> locationDeleter;

    private final Synchroniser synchroniser;

    private final ObservableListCache<LocationForListing> data;

    @Inject
    public LocationListViewModel(LocationListInteractor locationListInteractor, EntityDeleteInteractor<LocationForDeletion> locationDeleter, Synchroniser synchroniser, ObservableListCache<LocationForListing> data) {
        this.locationListInteractor = locationListInteractor;
        this.locationDeleter = locationDeleter;
        this.synchroniser = synchroniser;
        this.data = data;
    }

    public LiveData<List<LocationForListing>> getLocations() {
        return data.getLiveData(locationListInteractor::getLocations);
    }

    public void deleteLocation(int listItemIndex) {
        data.performOnListItem(listItemIndex, v -> locationDeleter.delete(v.toDeletion()));
    }

    public void resolveLocationId(int listItemIndex, Consumer<Integer> callback) {
        data.performOnListItem(listItemIndex, v -> callback.accept(v.id()));
    }

    public void synchronise() {
        synchroniser.synchronise();
    }

    @Override
    protected void onCleared() {
        data.clear();
    }
}
