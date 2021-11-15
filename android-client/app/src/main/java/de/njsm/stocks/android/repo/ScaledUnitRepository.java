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
import androidx.lifecycle.MediatorLiveData;
import de.njsm.stocks.android.db.dao.ScaledUnitDao;
import de.njsm.stocks.android.db.entities.ScaledUnit;
import de.njsm.stocks.android.db.views.ScaledUnitView;
import de.njsm.stocks.android.network.server.ServerClient;
import de.njsm.stocks.android.network.server.StatusCodeCallback;
import de.njsm.stocks.android.util.Logger;
import de.njsm.stocks.android.util.idling.IdlingResource;
import de.njsm.stocks.common.api.StatusCode;

import javax.inject.Inject;
import java.util.List;

public class ScaledUnitRepository {

    private static final Logger LOG = new Logger(ScaledUnitRepository.class);

    private final ScaledUnitDao scaledUnitDao;

    private final ServerClient webClient;

    private final Synchroniser synchroniser;

    private final IdlingResource idlingResource;

    @Inject
    public ScaledUnitRepository(ScaledUnitDao scaledUnitDao, ServerClient webClient, Synchroniser synchroniser, IdlingResource idlingResource) {
        this.scaledUnitDao = scaledUnitDao;
        this.webClient = webClient;
        this.synchroniser = synchroniser;
        this.idlingResource = idlingResource;
    }

    public LiveData<List<ScaledUnitView>> getUnits() {
        return scaledUnitDao.getAllView();
    }

    public LiveData<ScaledUnitView> getUnit(int id) {
        LOG.d("getting scaled unit " + id);
        return scaledUnitDao.getScaledUnitView(id);
    }

    public LiveData<StatusCode> delete(ScaledUnit unit) {
        LOG.i("deleting " + unit);
        MediatorLiveData<StatusCode> result = new MediatorLiveData<>();

        webClient.deleteScaledUnit(unit.id, unit.version)
                .enqueue(StatusCodeCallback.synchronise(result, idlingResource, synchroniser));

        return result;
    }

    public LiveData<StatusCode> edit(ScaledUnit item, int unit, String scale) {
        MediatorLiveData<StatusCode> result = new MediatorLiveData<>();
        if (item.getScale().toString().equals(scale) &&
                item.getUnit() == unit) {
            LOG.d("nothing to edit");
            result.setValue(StatusCode.SUCCESS);

        } else {
            webClient.editScaledUnit(item.id, item.version, scale, unit)
                    .enqueue(StatusCodeCallback.synchronise(result, idlingResource, synchroniser));
        }

        return result;
    }

    public LiveData<StatusCode> add(int unit, String scale) {
        LOG.d("adding scaled unit " + unit + ", " + scale);
        MediatorLiveData<StatusCode> data = new MediatorLiveData<>();

        webClient.addScaledUnit(scale, unit)
                .enqueue(StatusCodeCallback.synchronise(data, idlingResource, synchroniser));
        return data;
    }
}
