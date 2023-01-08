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

package de.njsm.stocks.client.databind.event;

import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxPagingSource;
import de.njsm.stocks.client.business.event.EventInteractor;
import de.njsm.stocks.client.business.Localiser;
import de.njsm.stocks.client.business.entities.event.ActivityEvent;
import io.reactivex.rxjava3.core.Single;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;

public class EventPagingSource extends RxPagingSource<LocalDate, ActivityEvent> {

    private final EventInteractor interactor;

    private final Localiser localiser;

    public EventPagingSource(EventInteractor interactor, Localiser localiser) {
        this.interactor = interactor;
        this.localiser = localiser;

        var disposable = interactor.getNewEventNotifier()
                .subscribe(v -> this.invalidate());
        this.registerInvalidatedCallback(() -> {
            disposable.dispose();
            return null;
        });
    }

    @NotNull
    @Override
    public Single<LoadResult<LocalDate, ActivityEvent>> loadSingle(@NotNull LoadParams<LocalDate> loadParams) {
        LocalDate day = resolveKey(loadParams);

        return interactor.getEventsOf(day)
                .map(v -> new LoadResult.Page<>(
                        v.events(),
                        v.nextPageKey().orElse(null),
                        v.previousPageKey().orElse(null)));
    }

    private LocalDate resolveKey(@NotNull LoadParams<LocalDate> loadParams) {
        LocalDate day = loadParams.getKey();
        if (day == null) {
            day = localiser.today();
        }
        return day;
    }

    @Nullable
    @Override
    public LocalDate getRefreshKey(@NotNull PagingState<LocalDate, ActivityEvent> state) {
        Integer anchorPosition = state.getAnchorPosition();
        if (anchorPosition == null) {
            return null;
        }

        LoadResult.Page<LocalDate, ActivityEvent> anchorPage = state.closestPageToPosition(anchorPosition);
        if (anchorPage == null) {
            return null;
        }

        LocalDate prevKey = anchorPage.getPrevKey();
        if (prevKey != null) {
            return prevKey.minusDays(1);
        }

        LocalDate nextKey = anchorPage.getNextKey();
        if (nextKey != null) {
            return nextKey.plusDays(1);
        }

        return null;
    }
}
