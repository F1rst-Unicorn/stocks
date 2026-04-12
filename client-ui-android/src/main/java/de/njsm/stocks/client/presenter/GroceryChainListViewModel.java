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
import de.njsm.stocks.client.business.EntityDeleter;
import de.njsm.stocks.client.business.GroceryChainListInteractor;
import de.njsm.stocks.client.business.Synchroniser;
import de.njsm.stocks.client.business.entities.GroceryChain;
import de.njsm.stocks.client.business.entities.GroceryChainForListing;

import javax.inject.Inject;
import java.util.List;
import java.util.function.Consumer;

public class GroceryChainListViewModel extends ViewModel {

    private final GroceryChainListInteractor groceryChainListInteractor;

    private final EntityDeleter<GroceryChain> groceryChainDeleter;

    private final Synchroniser synchroniser;

    private final ObservableListCache<GroceryChainForListing> data;

    @Inject
    public GroceryChainListViewModel(GroceryChainListInteractor groceryChainListInteractor, EntityDeleter<GroceryChain> groceryChainDeleter, Synchroniser synchroniser, ObservableListCache<GroceryChainForListing> data) {
        this.groceryChainListInteractor = groceryChainListInteractor;
        this.groceryChainDeleter = groceryChainDeleter;
        this.synchroniser = synchroniser;
        this.data = data;
    }

    public LiveData<List<GroceryChainForListing>> getGroceryChains() {
        return data.getLiveData(groceryChainListInteractor::getGroceryChains);
    }

    public void deleteGroceryChain(int listItemIndex) {
        data.performOnListItem(listItemIndex, groceryChainDeleter::delete);
    }

    public void resolveGroceryChainId(int listItemIndex, Consumer<Integer> callback) {
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
