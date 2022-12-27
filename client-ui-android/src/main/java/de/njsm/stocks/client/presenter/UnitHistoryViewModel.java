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
import androidx.lifecycle.ViewModelKt;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;
import de.njsm.stocks.client.business.Localiser;
import de.njsm.stocks.client.business.entities.event.ActivityEvent;
import de.njsm.stocks.client.business.event.EventInteractor;
import de.njsm.stocks.client.databind.event.EventPagingSource;
import kotlinx.coroutines.CoroutineScope;

import javax.inject.Inject;
import java.time.LocalDate;

public class UnitHistoryViewModel extends ViewModel {

    private final Localiser localiser;

    private final EventInteractor interactor;

    @Inject
    UnitHistoryViewModel(Localiser localiser, EventInteractor interactor) {
        this.localiser = localiser;
        this.interactor = interactor;
    }

    public LiveData<PagingData<ActivityEvent>> getActivityFeed() {
        CoroutineScope viewModelScope = ViewModelKt.getViewModelScope(this);
        Pager<LocalDate, ActivityEvent> pager = new Pager<>(
                new PagingConfig(20),
                () -> new EventPagingSource(interactor, localiser));
        return PagingLiveData.cachedIn(PagingLiveData.getLiveData(pager), viewModelScope);
    }
}
