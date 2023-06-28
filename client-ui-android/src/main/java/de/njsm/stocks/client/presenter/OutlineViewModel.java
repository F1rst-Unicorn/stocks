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
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;
import de.njsm.stocks.client.business.EanNumberLookupInteractor;
import de.njsm.stocks.client.business.Localiser;
import de.njsm.stocks.client.business.Synchroniser;
import de.njsm.stocks.client.business.entities.EanNumberForLookup;
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.event.ActivityEvent;
import de.njsm.stocks.client.business.event.EventInteractor;
import de.njsm.stocks.client.databind.event.EventPagingSource;
import io.reactivex.rxjava3.core.Maybe;

import javax.inject.Inject;

public class OutlineViewModel extends ViewModel {

    private final Synchroniser synchroniser;

    private final EanNumberLookupInteractor lookupInteractor;

    private final LiveData<PagingData<ActivityEvent>> pagingDataLiveData;

    @Inject
    OutlineViewModel(Synchroniser synchroniser, Localiser localiser, EventInteractor interactor, EanNumberLookupInteractor lookupInteractor) {
        this.synchroniser = synchroniser;
        this.lookupInteractor = lookupInteractor;
        var eventPagingSource = new EventPagingSource(interactor, localiser);
        var pager = new Pager<>(
                new PagingConfig(20),
                () -> eventPagingSource);
        pagingDataLiveData = PagingLiveData.cachedIn(PagingLiveData.getLiveData(pager), this);
    }

    public void synchronise() {
        synchroniser.synchronise();
    }

    public LiveData<PagingData<ActivityEvent>> getActivityFeed() {
        return pagingDataLiveData;
    }

    public Maybe<Id<Food>> searchFood(String eanNumber) {
        return lookupInteractor.lookup(EanNumberForLookup.create(eanNumber));
    }
}
