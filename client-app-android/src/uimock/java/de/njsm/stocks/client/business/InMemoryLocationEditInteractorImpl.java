/*
 * stocks is client-server program to manage a household's food stock
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
 *
 */

package de.njsm.stocks.client.business;

import de.njsm.stocks.client.business.entities.Identifiable;
import de.njsm.stocks.client.business.entities.Location;
import de.njsm.stocks.client.business.entities.LocationForListing;
import de.njsm.stocks.client.business.entities.LocationToEdit;
import de.njsm.stocks.client.testdata.LocationsForListing;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import javax.inject.Inject;
import java.util.List;

class InMemoryLocationEditInteractorImpl implements LocationEditInteractor {

    private final BehaviorSubject<List<LocationForListing>> data;

    @Inject
    InMemoryLocationEditInteractorImpl(LocationsForListing locationsForListing) {
        this.data = locationsForListing.getData();
    }

    @Override
    public Observable<LocationToEdit> getLocation(Identifiable<Location> id) {
        return data.firstElement().map(list -> {
            LocationForListing item = list.stream().filter(v -> v.id() == id.id()).findAny().get();
            return LocationToEdit.builder()
                    .id(item.id())
                    .name(item.name())
                    .description("Lorem ipsum")
                    .build();
        }).toObservable();
    }

    @Override
    public void edit(LocationToEdit formData) {

    }
}
