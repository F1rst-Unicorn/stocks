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
import de.njsm.stocks.android.error.StatusCodeException;
import de.njsm.stocks.android.repo.Synchroniser;
import de.njsm.stocks.android.util.Logger;
import de.njsm.stocks.android.util.idling.IdlingResource;
import de.njsm.stocks.common.api.DataResponse;
import de.njsm.stocks.common.api.Response;
import de.njsm.stocks.common.api.StatusCode;
import fj.data.Validation;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

public class StatusCodeCallback implements Callback<Response> {

    private static final Logger LOG = new Logger(StatusCodeCallback.class);

    private final MediatorLiveData<StatusCode> data;

    private final Synchroniser synchroniser;

    private final IdlingResource idlingResource;

    public StatusCodeCallback(MediatorLiveData<StatusCode> data,
                              Synchroniser synchroniser,
                              IdlingResource idlingResource) {
        this.data = data;
        this.synchroniser = synchroniser;
        this.idlingResource = idlingResource;

        idlingResource.increment();
    }

    @Override
    public void onResponse(@NonNull Call<Response> call,
                           @NonNull retrofit2.Response<Response> response) {
        StatusCode result = handleResponse(response);

        LOG.d("Got result for " + call.request().method() + " to " +
                call.request().url().encodedPath() + ": " + result);

        data.setValue(result);
        if (result == StatusCode.SUCCESS ||
                result == StatusCode.INVALID_DATA_VERSION ||
                result == StatusCode.NOT_FOUND ||
                result == StatusCode.FOREIGN_KEY_CONSTRAINT_VIOLATION) {
            LiveData<StatusCode> syncResult = synchroniser.synchronise();
            data.addSource(syncResult, data::setValue);
        }
        idlingResource.decrement();
    }

    @Override
    public void onFailure(@NonNull Call<Response> call,
                          @NonNull Throwable t) {
        LOG.e("Network error", t);
        data.setValue(StatusCode.GENERAL_ERROR);
        idlingResource.decrement();
    }

    static <T extends Response> StatusCode handleResponse(retrofit2.Response<T> response) {
        if (!response.isSuccessful()
                || response.body() == null
                || response.body().getStatus() != StatusCode.SUCCESS)
            return error(response);
        else
            return StatusCode.SUCCESS;
    }

    static <T> Validation<StatusCode, T> returnResponse(retrofit2.Response<DataResponse<T>> response) {
        if (!response.isSuccessful()
                || response.body() == null
                || response.body().getStatus() != StatusCode.SUCCESS)
            return Validation.fail(error(response));
        else
            return Validation.success(response.body().data);
    }

    /**
     * @throws StatusCodeException if retrofit raises error or server returns
     *                             erroneous StatusCode.
     */
    public static <D> D executeCall(retrofit2.Call<? extends DataResponse<D>> call) throws StatusCodeException {
        retrofit2.Response<? extends DataResponse<D>> response;
        try {
            response = call.execute();
        } catch (IOException e) {
            LOG.e("Calling " + call.request().method() + " to " + call.request().url().encodedPath() + " failed", e);
            throw new StatusCodeException(StatusCode.GENERAL_ERROR, e);
        }

        if (!response.isSuccessful()
                || response.body() == null
                || response.body().getStatus() != StatusCode.SUCCESS)
            throw new StatusCodeException(error(response));
        else
            return response.body().data;
    }

    private static StatusCode error(retrofit2.Response<? extends Response> r) {
        Response response = r.body();

        if (response != null)
            return response.getStatus();
        else if (r.errorBody() != null)
            return unmarshalResponseOurselves(r);
        else {
            logResponse(r);
            return StatusCode.GENERAL_ERROR;
        }
    }

    private static StatusCode unmarshalResponseOurselves(retrofit2.Response<? extends Response> r) {
        Response response;

        ResponseBody body = r.errorBody();
        if (body == null) {
            logResponse(r);
            return StatusCode.GENERAL_ERROR;
        }

        try {
            JacksonConverterFactory factory = JacksonConverterFactory.create();
            response = (Response) factory.responseBodyConverter(Response.class, null, null).convert(body);
        } catch (IOException | ClassCastException e) {
            logResponse(r);
            return StatusCode.GENERAL_ERROR;
        }

        if (response != null)
            return response.getStatus();
        else {
            logResponse(r);
            return StatusCode.GENERAL_ERROR;
        }
    }

    private static <T> void logResponse(retrofit2.Response<T> r) {
        if (r.body() != null)
            LOG.e("Response was an error:\n" + r.body().toString());
        else
            LOG.e("Response was an error without body");

        if (r.errorBody() != null)
            try {
                LOG.e("Response was an error:\n" +
                        r.errorBody().string());
            } catch (IOException e) {
                LOG.e("Response was an error and the body returned an exception");
            }
        else
            LOG.e("Response was an error without error body");
    }

}
