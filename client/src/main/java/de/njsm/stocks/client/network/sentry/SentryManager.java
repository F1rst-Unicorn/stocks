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

package de.njsm.stocks.client.network.sentry;

import de.njsm.stocks.client.business.data.ClientTicket;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.network.TcpHost;
import okhttp3.OkHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

public class SentryManager {

    private static final Logger LOG = LogManager.getLogger(SentryManager.class);


    private SentryClient backend;

    public SentryManager(OkHttpClient httpClient, TcpHost sentryHost) {
        String url = String.format("https://%s/", sentryHost.toString());

        backend = new Retrofit.Builder()
                .baseUrl(url)
                .client(httpClient)
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(SentryClient.class);
        LOG.info("New ticket backend to " + url);
    }

    public String requestCertificate(ClientTicket requestTicket) throws NetworkException {
        LOG.info("Requesting ticket");
        Response<SentryClient.Result> response = executeCall(requestTicket);
        return handleResponse(response);
    }

    private Response<SentryClient.Result> executeCall(ClientTicket ticket) throws NetworkException {
        try {
            Call<SentryClient.Result> callback = backend.requestCertificate(ticket.deviceId, ticket.ticket, ticket.pemFile);
            return callback.execute();
        } catch (IOException e) {
            LOG.error("Failed to execute", e);
            throw new NetworkException("Connection to sentry failed", e);
        }
    }

    private String handleResponse(Response<SentryClient.Result> response) throws NetworkException {
        if (response.isSuccessful()) {
            return handleHttpSuccess(response);
        } else {
            logError(response);
            throw new NetworkException("Sentry returned error");
        }
    }

    private String handleHttpSuccess(Response<SentryClient.Result> response) throws NetworkException {
        SentryClient.Result responseTicket = response.body();
        if (responseTicket == null) {
            LOG.error("Sentry returned empty response");
            throw new NetworkException("Sentry returned empty response");
        }

        switch (responseTicket.status) {
            case SUCCESS:
                LOG.info("Sentry request was successful");
                return responseTicket.data;

            case ACCESS_DENIED:
                LOG.error("Sentry denied access");
                throw new NetworkException("Server denied access");

            default:
                LOG.error("Sentry returned error code " + responseTicket.status.name());
                throw new NetworkException("Sentry returned error");
        }
    }

    private void logError(Response<SentryClient.Result> response) {
        LOG.error("Request was not successful: HTTP Code " + response.code());
        try {
            LOG.error(response.errorBody() != null ? response.errorBody().string() : "");
        } catch (IOException e) {
            LOG.error("Could not read error response", e);
        }
    }

}
