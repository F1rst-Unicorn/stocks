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
import de.njsm.stocks.android.db.dao.EanNumberDao;
import de.njsm.stocks.android.db.entities.EanNumber;
import de.njsm.stocks.android.network.server.ServerClient;
import de.njsm.stocks.android.network.server.StatusCodeCallback;
import de.njsm.stocks.android.util.Logger;
import de.njsm.stocks.android.util.idling.IdlingResource;
import de.njsm.stocks.common.api.StatusCode;

import javax.inject.Inject;
import java.util.List;

public class EanNumberRepository {

    private static final Logger LOG = new Logger(EanNumberRepository.class);

    private final EanNumberDao eanNumberDao;

    private final ServerClient webClient;

    private final Synchroniser synchroniser;

    private final IdlingResource idlingResource;

    @Inject
    public EanNumberRepository(EanNumberDao eanNumberDao,
                               ServerClient webClient,
                               Synchroniser synchroniser,
                               IdlingResource idlingResource) {
        this.eanNumberDao = eanNumberDao;
        this.webClient = webClient;
        this.synchroniser = synchroniser;
        this.idlingResource = idlingResource;
    }

    public LiveData<List<EanNumber>> getEanCodesOf(int id) {
        LOG.d("getting numbers of id " + id);
        return eanNumberDao.getEanNumbersOf(id);
    }

    public LiveData<StatusCode> addEanCode(String code, int identifies) {
        LOG.d("adding code " + code + " for food " + identifies);
        MediatorLiveData<StatusCode> data = new MediatorLiveData<>();

        webClient.addEanNumber(code, identifies)
                .enqueue(new StatusCodeCallback(data, synchroniser, idlingResource));
        return data;
    }

    public LiveData<StatusCode> deleteEanCode(EanNumber number) {
        LOG.d("deleting number " + number);
        MediatorLiveData<StatusCode> data = new MediatorLiveData<>();
        webClient.deleteEanNumber(number.id, number.version)
                .enqueue(new StatusCodeCallback(data, synchroniser, idlingResource));
        return data;
    }
}
