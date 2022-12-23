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

package de.njsm.stocks.client.database;

import de.njsm.stocks.client.business.event.*;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

import javax.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.util.Collections.emptyList;

class EventRepositoryImpl implements EventRepository {

    private final EventDao eventDao;

    @Inject
    EventRepositoryImpl(EventDao eventDao) {
        this.eventDao = eventDao;
    }

    @Override
    public Single<List<LocationEventFeedItem>> getLocationFeed(Instant day) {
        return eventDao.getLocationEvents(day, day.plus(1, ChronoUnit.DAYS))
                .first(emptyList());
    }

    @Override
    public Single<List<UnitEventFeedItem>> getUnitFeed(Instant day) {
        return eventDao.getUnitEvents(day, day.plus(1, ChronoUnit.DAYS))
                .first(emptyList());
    }

    @Override
    public Single<List<UserEventFeedItem>> getUserFeed(Instant day) {
        return eventDao.getUserEvents(day, day.plus(1, ChronoUnit.DAYS))
                .first(emptyList());
    }

    @Override
    public Single<List<UserDeviceEventFeedItem>> getUserDeviceFeed(Instant day) {
        return eventDao.getUserDeviceEvents(day, day.plus(1, ChronoUnit.DAYS))
                .first(emptyList());
    }

    @Override
    public Single<List<ScaledUnitEventFeedItem>> getScaledUnitFeed(Instant day) {
        return eventDao.getScaledUnitEvents(day, day.plus(1, ChronoUnit.DAYS))
                .first(emptyList());
    }

    @Override
    public Single<List<FoodEventFeedItem>> getFoodFeed(Instant day) {
        return eventDao.getFoodEvents(day, day.plus(1, ChronoUnit.DAYS))
                .first(emptyList());
    }

    @Override
    public Observable<Instant> getNewEventNotifier() {
        return eventDao.getLatestUpdateTimestamp()
                .distinctUntilChanged()
                .skip(1);
    }

    @Override
    public Single<Instant> getOldestEventTime() {
        return eventDao.getOldestEventTime()
                .first(Instant.MAX);
    }
}
