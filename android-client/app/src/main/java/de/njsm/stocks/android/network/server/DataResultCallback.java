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

package de.njsm.stocks.android.network.server;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;
import de.njsm.stocks.android.frontend.device.ServerTicket;
import de.njsm.stocks.android.network.server.data.DataResponse;
import de.njsm.stocks.android.repo.Synchroniser;
import de.njsm.stocks.android.util.Logger;
import fj.data.Validation;
import retrofit2.Call;
import retrofit2.Callback;

public class DataResultCallback implements Callback<DataResponse<ServerTicket>> {

    private static final Logger LOG = new Logger(DataResultCallback.class);

    private MediatorLiveData<Validation<StatusCode, ServerTicket>> data;

    private Synchroniser synchroniser;

    public DataResultCallback(MediatorLiveData<Validation<StatusCode, ServerTicket>> data,
                              Synchroniser synchroniser) {
        this.data = data;
        this.synchroniser = synchroniser;
    }

    @Override
    public void onResponse(@NonNull Call<DataResponse<ServerTicket>> call,
                           @NonNull retrofit2.Response<DataResponse<ServerTicket>> response) {
        LOG.d("Got result for " + call.request().method() + " to " +
                call.request().url().encodedPath());

        Validation<StatusCode, ServerTicket> result = StatusCodeCallback.returnResponse(response);
        if (result.isSuccess()) {
            data.setValue(result);
            LiveData<StatusCode> syncResult = synchroniser.synchronise();
            LiveData<Validation<StatusCode, ServerTicket>> map = Transformations.map(syncResult, s ->
                    s != StatusCode.SUCCESS ? Validation.fail(s) : result);
            data.addSource(map, v -> data.setValue(v));
        } else {
            data.setValue(result);
        }
    }

    @Override
    public void onFailure(@NonNull Call<DataResponse<ServerTicket>> call,
                          @NonNull Throwable t) {
        LOG.e("Network error", t);
        data.setValue(Validation.fail(StatusCode.GENERAL_ERROR));
    }
}
