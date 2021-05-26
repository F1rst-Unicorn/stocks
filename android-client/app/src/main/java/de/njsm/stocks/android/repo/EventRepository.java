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

package de.njsm.stocks.android.repo;

import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import de.njsm.stocks.android.business.data.activity.EntityEvent;
import de.njsm.stocks.android.db.dao.EventDao;
import de.njsm.stocks.android.util.Logger;
import de.njsm.stocks.android.util.paging.MergingDataSourceFactory;

import javax.inject.Inject;

public class EventRepository {

    private static final Logger LOG = new Logger(EventRepository.class);

    private final EventDao eventDao;

    @Inject
    public EventRepository(EventDao eventDao) {
        this.eventDao = eventDao;
    }

    public LiveData<PagedList<EntityEvent<?>>> getEvents() {
        LOG.d("getting history");
        return new LivePagedListBuilder<>(
                new MergingDataSourceFactory(
                        eventDao.getFoodItemHistory(),
                        eventDao.getEanHistory(),
                        eventDao.getFoodHistory(),
                        eventDao.getUserHistory(),
                        eventDao.getUserDeviceHistory(),
                        eventDao.getLocationHistory(),
                        eventDao.getUnitHistory(),
                        eventDao.getScaledUnitHistory())
                , 7).build();
    }

    public LiveData<PagedList<EntityEvent<?>>> getFoodEvents(int id) {
        LOG.d("getting history");
        return new LivePagedListBuilder<>(
                new MergingDataSourceFactory(
                        eventDao.getFoodHistoryOfSingleFood(id),
                        eventDao.getEanNumberHistoryOfSingleFood(id),
                        eventDao.getFoodItemHistoryOfSingleFood(id))
                , 7).build();
    }

    public LiveData<PagedList<EntityEvent<?>>> getLocationEvents(int id) {
        LOG.d("getting history");
        return new LivePagedListBuilder<>(
                new MergingDataSourceFactory(
                        eventDao.getLocationHistoryOfSingleLocation(id),
                        eventDao.getFoodItemHistoryOfSingleLocation(id))
                , 7).build();
    }
}
