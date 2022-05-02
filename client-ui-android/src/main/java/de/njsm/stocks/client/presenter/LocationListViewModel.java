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
import de.njsm.stocks.client.business.EntityDeleter;
import de.njsm.stocks.client.business.LocationListInteractor;
import de.njsm.stocks.client.business.Synchroniser;
import de.njsm.stocks.client.business.entities.Location;
import de.njsm.stocks.client.business.entities.LocationForListing;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;
import java.util.function.Consumer;

public class LocationListViewModel extends ViewModel {

    private final LocationListInteractor locationListInteractor;

    private final EntityDeleter<Location> locationDeleter;

    private final Synchroniser synchroniser;

    private Observable<List<LocationForListing>> data;

    @Inject
    public LocationListViewModel(LocationListInteractor locationListInteractor, EntityDeleter<Location> locationDeleter, Synchroniser synchroniser) {
        this.locationListInteractor = locationListInteractor;
        this.locationDeleter = locationDeleter;
        this.synchroniser = synchroniser;
    }

    public LiveData<List<LocationForListing>> getLocations() {
        return LiveDataReactiveStreams.fromPublisher(
                getData().toFlowable(BackpressureStrategy.LATEST)
        );
    }

    public void deleteLocation(int listItemIndex) {
        performOnCurrentLocations(list -> locationDeleter.delete(list.get(listItemIndex)));
    }

    public void resolveLocationId(int listItemIndex, Consumer<Integer> callback) {
        performOnCurrentLocations(list -> callback.accept(list.get(listItemIndex).id()));
    }

    private void performOnCurrentLocations(Consumer<List<LocationForListing>> runnable) {
        getData()
                .firstElement()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(runnable::accept);
    }

    public void synchronise() {
        synchroniser.synchronise();
    }

    private Observable<List<LocationForListing>> getData() {
        if (data == null)
            data = locationListInteractor.getLocations();
        return data;
    }
}
