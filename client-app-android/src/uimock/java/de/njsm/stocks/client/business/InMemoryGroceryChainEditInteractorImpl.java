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

import de.njsm.stocks.client.business.entities.GroceryChainForEditing;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.GroceryChain;
import de.njsm.stocks.client.business.entities.GroceryChainForListing;
import de.njsm.stocks.client.testdata.GroceryChainsForListing;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import javax.inject.Inject;
import java.util.List;

class InMemoryGroceryChainEditInteractorImpl implements GroceryChainEditInteractor {

    private final BehaviorSubject<List<GroceryChainForListing>> data;

    @Inject
    InMemoryGroceryChainEditInteractorImpl(GroceryChainsForListing groceryChainsForListing) {
        this.data = groceryChainsForListing.getData();
    }

    @Override
    public Observable<GroceryChainForEditing> get(Id<GroceryChain> id) {
        return data.firstElement().map(list -> {
            GroceryChainForListing item = list.stream().filter(v -> v.id() == id.id()).findAny().get();
            return GroceryChainForEditing.builder()
                    .id(item.id())
                    .version(0)
                    .name(item.name())
                    .build();
        }).toObservable();
    }

    @Override
    public void edit(GroceryChainForEditing formData) {

    }
}
