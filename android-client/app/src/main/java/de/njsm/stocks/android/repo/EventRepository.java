package de.njsm.stocks.android.repo;

import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import javax.inject.Inject;

import de.njsm.stocks.android.business.data.activity.EntityEvent;
import de.njsm.stocks.android.db.dao.EventDao;
import de.njsm.stocks.android.util.Logger;

public class EventRepository {

    private static final Logger LOG = new Logger(EventRepository.class);

    private EventDao eventDao;

    @Inject
    public EventRepository(EventDao eventDao) {
        this.eventDao = eventDao;
    }

    public LiveData<PagedList<EntityEvent<?>>> getLocationEvents() {
        LOG.d("getting history");
        return new LivePagedListBuilder<>(eventDao.getEventHistory(), 3).build();
    }
}
