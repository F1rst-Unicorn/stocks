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
import de.njsm.stocks.android.db.dao.UnitDao;
import de.njsm.stocks.android.db.entities.Unit;
import de.njsm.stocks.android.network.server.ServerClient;
import de.njsm.stocks.android.network.server.StatusCode;
import de.njsm.stocks.android.network.server.StatusCodeCallback;
import de.njsm.stocks.android.util.Logger;
import de.njsm.stocks.android.util.idling.IdlingResource;

import javax.inject.Inject;
import java.util.List;

public class UnitRepository {

    private static final Logger LOG = new Logger(UnitRepository.class);

    private final UnitDao unitDao;

    private final ServerClient webClient;

    private final Synchroniser synchroniser;

    private final IdlingResource idlingResource;

    @Inject
    public UnitRepository(UnitDao unitDao, ServerClient webClient, Synchroniser synchroniser, IdlingResource idlingResource) {
        this.unitDao = unitDao;
        this.webClient = webClient;
        this.synchroniser = synchroniser;
        this.idlingResource = idlingResource;
    }

    public LiveData<List<Unit>> getUnits() {
        return unitDao.getAll();
    }

    public LiveData<StatusCode> delete(Unit unit) {
        LOG.i("deleting " + unit);
        MediatorLiveData<StatusCode> result = new MediatorLiveData<>();

        webClient.deleteUnit(unit.id, unit.version)
                .enqueue(new StatusCodeCallback(result, synchroniser, idlingResource));

        return result;
    }

    public LiveData<StatusCode> edit(Unit item, String newName, String newAbbreviation) {
        MediatorLiveData<StatusCode> result = new MediatorLiveData<>();
        if (item.getName().equals(newName) &&
                item.getAbbreviation().equals(newAbbreviation)) {
            LOG.d("nothing to edit");
            result.setValue(StatusCode.SUCCESS);

        } else {
            webClient.editUnit(item.id, item.version, newName, newAbbreviation)
                    .enqueue(new StatusCodeCallback(result, synchroniser, idlingResource));
        }

        return result;
    }
}
