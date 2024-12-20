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

package de.njsm.stocks.client.network;

import de.njsm.stocks.client.business.StatusCodeException;
import de.njsm.stocks.common.api.DataResponse;
import de.njsm.stocks.common.api.Response;
import de.njsm.stocks.common.api.StatusCode;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.inject.Inject;
import java.io.IOException;

import static de.njsm.stocks.client.network.DataMapper.map;

class CallHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CallHandler.class);

    @Inject
    CallHandler() {
    }

    StatusCode executeCommand(Call<? extends Response> call) {
        try {
            retrofit2.Response<? extends Response> response = call.execute();
            StatusCode result = handleResponse(response);
            LOG.debug("Got result for " + call.request().method() + " to " +
                    call.request().url().encodedPath() + ": " + result);
            return result;
        } catch (IOException e) {
            LOG.error("Network error", e);
            return StatusCode.GENERAL_ERROR;
        }
    }

    <D> D executeRawForResult(Call<D> call) throws StatusCodeException {
        retrofit2.Response<D> response = executeCall(call);

        if (!response.isSuccessful()
                || response.body() == null) {
            logResponse(response);
            throw new StatusCodeException(map(StatusCode.GENERAL_ERROR));
        } else
            return response.body();

    }

    <D> D executeForResult(retrofit2.Call<? extends DataResponse<D>> call) throws StatusCodeException {
        retrofit2.Response<? extends DataResponse<D>> response = executeCall(call);

        if (!response.isSuccessful()
                || response.body() == null
                || response.body().getStatus() != StatusCode.SUCCESS)
            throw new StatusCodeException(map(error(response)));
        else
            return response.body().getData();
    }

    private <D> retrofit2.Response<D> executeCall(Call<D> call) {
        retrofit2.Response<D> response;
        try {
            response = call.execute();
        } catch (IOException e) {
            // Append exception message because android suppresses UnknownHostException stack traces
            LOG.error("Calling " + call.request().method() + " to " + call.request().url().encodedPath() + " failed: " + e.getMessage(), e);
            throw new StatusCodeException(map(StatusCode.GENERAL_ERROR), e);
        }
        return response;
    }

    private static <T extends Response> StatusCode handleResponse(retrofit2.Response<T> response) {
        if (!response.isSuccessful()
                || response.body() == null
                || response.body().getStatus() != StatusCode.SUCCESS)
            return error(response);
        else
            return StatusCode.SUCCESS;
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
            LOG.error("Response was an " + r.code() + " error:\n" + r.body());
        else
            LOG.error("Response was an " + r.code() + " error without body");

        try (var errorBody = r.errorBody()) {
            if (errorBody != null && !errorBody.string().isEmpty())
                LOG.error("Response was an " + r.code() + " error:\n" +
                        errorBody.string());
            else
                LOG.error("Response was an " + r.code() + " error without error body");
        } catch (IOException e) {
            LOG.error("Response was an " + r.code() + " error and the body returned an exception");
        }
    }
}
