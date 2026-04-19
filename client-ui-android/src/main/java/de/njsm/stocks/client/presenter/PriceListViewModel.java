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
import de.njsm.stocks.client.business.PriceListInteractor;
import de.njsm.stocks.client.business.Synchroniser;
import de.njsm.stocks.client.business.entities.*;

import javax.inject.Inject;
import java.util.List;

public class PriceListViewModel extends ViewModel {

    private final PriceListInteractor interactor;

    private final EntityDeleter<Price> deleter;

    private final Synchroniser synchroniser;

    private final ObservableListCache<PriceForListing> data;

    @Inject
    PriceListViewModel(PriceListInteractor interactor, EntityDeleter<Price> deleter, Synchroniser synchroniser, ObservableListCache<PriceForListing> data) {
        this.interactor = interactor;
        this.deleter = deleter;
        this.synchroniser = synchroniser;
        this.data = data;
    }

    public LiveData<List<PriceForListing>> getPrices(IdImpl<Food> id) {
        return data.getLiveData(() -> interactor.getPrices(id));
    }

    public void delete(int listItemIndex) {
        data.performOnListItem(listItemIndex, item -> deleter.delete(item.id()));
    }

    public void synchronise() {
        synchroniser.synchronise();
    }

    @Override
    protected void onCleared() {
        data.clear();
    }
}
