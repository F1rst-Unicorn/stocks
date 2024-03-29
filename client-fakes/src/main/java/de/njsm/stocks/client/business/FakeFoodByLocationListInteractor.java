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

import de.njsm.stocks.client.business.entities.FoodForListing;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.Location;
import de.njsm.stocks.client.business.entities.LocationName;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import javax.inject.Inject;
import java.util.List;

public class FakeFoodByLocationListInteractor implements FoodByLocationListInteractor {

    private final BehaviorSubject<List<FoodForListing>> data;

    @Inject
    FakeFoodByLocationListInteractor() {
        this.data = BehaviorSubject.create();
    }

    public void setData(List<FoodForListing> data) {
        this.data.onNext(data);
    }

    @Override
    public Observable<List<FoodForListing>> getFoodBy(Id<Location> location) {
        return data;
    }

    @Override
    public Observable<LocationName> getLocation(Id<Location> location) {
        return Observable.just(LocationName.create("Fridge"));
    }
}
