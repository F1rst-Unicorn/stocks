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
import de.njsm.stocks.client.business.EanNumberListInteractor;
import de.njsm.stocks.client.business.EntityDeleter;
import de.njsm.stocks.client.business.Synchroniser;
import de.njsm.stocks.client.business.entities.*;

import java.util.List;

public class EanNumberListViewModel extends ViewModel {

    private final EanNumberListInteractor interactor;

    private final EntityDeleter<EanNumber> deleter;

    private final Synchroniser synchroniser;

    private final ObservableListCache<EanNumberForListing> data;

    public EanNumberListViewModel(EanNumberListInteractor interactor, EntityDeleter<EanNumber> deleter, Synchroniser synchroniser, ObservableListCache<EanNumberForListing> data) {
        this.interactor = interactor;
        this.deleter = deleter;
        this.synchroniser = synchroniser;
        this.data = data;
    }

    public LiveData<List<EanNumberForListing>> get(Id<Food> food) {
        return data.getLiveData(() -> interactor.get(food));
    }

    public void synchronise() {
        synchroniser.synchronise();
    }

    public void delete(int listItemIndex) {
        data.performOnListItem(listItemIndex, deleter::delete);
    }

    public void add(Id<Food> food, String eanCode) {
        interactor.add(EanNumberAddForm.create(food, eanCode));
    }
}
