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
import de.njsm.stocks.client.business.GroceryStoreListInteractor;
import de.njsm.stocks.client.business.Synchroniser;
import de.njsm.stocks.client.business.entities.GroceryStore;
import de.njsm.stocks.client.business.entities.GroceryStoresForListing;
import de.njsm.stocks.client.business.entities.Versionable;

import javax.inject.Inject;
import java.util.function.Consumer;

public class GroceryStoreListViewModel extends ViewModel {

    private final GroceryStoreListInteractor groceryStoreListInteractor;

    private final EntityDeleter<GroceryStore> groceryStoreDeleter;

    private final Synchroniser synchroniser;

    private final ObservableDataCache<GroceryStoresForListing> data;

    @Inject
    GroceryStoreListViewModel(GroceryStoreListInteractor groceryStoreListInteractor, EntityDeleter<GroceryStore> groceryStoreDeleter, Synchroniser synchroniser, ObservableDataCache<GroceryStoresForListing> data) {
        this.groceryStoreListInteractor = groceryStoreListInteractor;
        this.groceryStoreDeleter = groceryStoreDeleter;
        this.synchroniser = synchroniser;
        this.data = data;
    }

    public LiveData<GroceryStoresForListing> getGroceryStores() {
        return data.getLiveData(groceryStoreListInteractor::getGroceryStores);
    }

    public void deleteGroceryStore(int listItemIndex) {
        data.performOnNestedList(listItemIndex, GroceryStoresForListing::groceryStores, groceryStoreDeleter::delete);
    }

    public void resolveGroceryStoreId(int listItemIndex, Consumer<Versionable<GroceryStore>> callback) {
        data.performOnNestedList(listItemIndex, GroceryStoresForListing::groceryStores, callback::accept);
    }

    public void synchronise() {
        synchroniser.synchronise();
    }

    @Override
    protected void onCleared() {
        data.clear();
    }
}
