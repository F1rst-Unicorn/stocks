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

package de.njsm.stocks.client.presenter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import de.njsm.stocks.client.business.GroceryStoreEditInteractor;
import de.njsm.stocks.client.business.entities.GroceryStore;
import de.njsm.stocks.client.business.entities.GroceryStoreEditData;
import de.njsm.stocks.client.business.entities.GroceryStoreForEditing;
import de.njsm.stocks.client.business.entities.Id;

import javax.inject.Inject;

public class GroceryStoreEditViewModel extends ViewModel {

    private final GroceryStoreEditInteractor groceryStoreEditInteractor;

    private final ObservableDataCache<GroceryStoreEditData> data;

    @Inject
    GroceryStoreEditViewModel(GroceryStoreEditInteractor groceryStoreEditInteractor, ObservableDataCache<GroceryStoreEditData> data) {
        this.groceryStoreEditInteractor = groceryStoreEditInteractor;
        this.data = data;
    }

    public LiveData<GroceryStoreEditData> get(Id<GroceryStore> id) {
        return data.getLiveData(() -> groceryStoreEditInteractor.get(id));
    }

    public void edit(GroceryStoreForEditing data) {
        groceryStoreEditInteractor.edit(data);
    }

    @Override
    protected void onCleared() {
        data.clear();
    }
}
