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
import de.njsm.stocks.client.business.Localiser;
import de.njsm.stocks.client.business.Synchroniser;
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.Location;
import de.njsm.stocks.client.business.entities.event.ActivityEvent;
import de.njsm.stocks.client.business.event.EventInteractor;
import de.njsm.stocks.client.business.event.EventInteractorFactory;
import de.njsm.stocks.client.databind.event.EventPagingSource;

import javax.inject.Inject;
import java.time.LocalDate;

public class HistoryViewModel extends ViewModel {

    private final EventInteractor interactor;

    private final EventInteractorFactory factory;

    private final Synchroniser synchroniser;

    private final LiveData<PagingData<ActivityEvent>> pagingDataLiveData;

    @Inject
    HistoryViewModel(Localiser localiser, EventInteractor interactor, EventInteractorFactory factory, Synchroniser synchroniser) {
        this.interactor = interactor;
        this.factory = factory;
        this.synchroniser = synchroniser;
        Pager<LocalDate, ActivityEvent> pager = new Pager<>(
                new PagingConfig(20),
                () -> new EventPagingSource(interactor, localiser));
        pagingDataLiveData =  PagingLiveData.cachedIn(PagingLiveData.getLiveData(pager), this);
    }

    public LiveData<PagingData<ActivityEvent>> getActivityFeed() {
        return getPagingDataLiveData(interactor);
    }

    public void synchronise() {
        synchroniser.synchronise();
    }

    private LiveData<PagingData<ActivityEvent>> getPagingDataLiveData(EventInteractor interactor) {
        return pagingDataLiveData;
    }

    public LiveData<PagingData<ActivityEvent>> getActivityFeedForLocation(Id<Location> location) {
        var interactor = factory.forLocation(location);
        return getPagingDataLiveData(interactor);
    }

    public LiveData<PagingData<ActivityEvent>> getActivityFeedForFood(Id<Food> food) {
        var interactor = factory.forFood(food);
        return getPagingDataLiveData(interactor);
    }
}
