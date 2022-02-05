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
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.ViewModel;
import de.njsm.stocks.client.business.LocationDeleter;
import de.njsm.stocks.client.business.LocationListInteractor;
import de.njsm.stocks.client.business.Synchroniser;
import de.njsm.stocks.client.business.entities.LocationForListing;
import io.reactivex.rxjava3.core.BackpressureStrategy;

import javax.inject.Inject;
import java.util.List;
import java.util.function.Consumer;

public class LocationViewModel extends ViewModel {

    private final LocationListInteractor locationListInteractor;

    private final LocationDeleter locationDeleter;

    private final Synchroniser synchroniser;

    @Inject
    public LocationViewModel(LocationListInteractor locationListInteractor, LocationDeleter locationDeleter, Synchroniser synchroniser) {
        this.locationListInteractor = locationListInteractor;
        this.locationDeleter = locationDeleter;
        this.synchroniser = synchroniser;
    }

    public LiveData<List<LocationForListing>> getLocations() {
        return LiveDataReactiveStreams.fromPublisher(
                locationListInteractor.getLocations().toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public void deleteLocation(int listItemIndex) {
        performOnCurrentLocations(list -> locationDeleter.deleteLocation(list.get(listItemIndex)));
    }

    public void resolveLocationId(int listItemIndex, Consumer<Integer> callback) {
        performOnCurrentLocations(list -> callback.accept(list.get(listItemIndex).id()));
    }

    private void performOnCurrentLocations(Consumer<List<LocationForListing>> runnable) {
        locationListInteractor.getLocations().firstElement().subscribe(runnable::accept);
    }

    public void synchronise() {
        synchroniser.synchronise();
    }
}
