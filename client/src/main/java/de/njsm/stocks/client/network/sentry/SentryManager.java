package de.njsm.stocks.client.network.sentry;

import com.squareup.okhttp.OkHttpClient;
import de.njsm.stocks.common.data.Ticket;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.network.TcpHost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit.*;

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

    public String requestCertificate(Ticket requestTicket) throws NetworkException {
        LOG.info("Requesting ticket");
        Response<Ticket> response = executeCall(requestTicket);
        return handleResponse(response);
    }

    private Response<Ticket> executeCall(Ticket ticket) throws NetworkException {
        try {
            Call<Ticket> callback = backend.requestCertificate(ticket);
            return callback.execute();
        } catch (IOException e) {
            LOG.error("Failed to execute", e);
            throw new NetworkException("Connection to sentry failed", e);
        }
    }

    private String handleResponse(Response<Ticket> response) throws NetworkException {
        if (response.isSuccess()) {
            return handleSuccess(response);
        } else {
            logError(response);
            throw new NetworkException("Sentry returned error");
        }
    }

    private String handleSuccess(Response<Ticket> response) throws NetworkException {
        Ticket responseTicket = response.body();
        if (responseTicket.pemFile == null || responseTicket.pemFile.isEmpty()) {
            LOG.error("Sentry returned empty file");
            throw new NetworkException("Sentry rejected ticket!");
        }
        LOG.info("Sentry request was successful");
        return responseTicket.pemFile;
    }

    private void logError(Response<Ticket> response) {
        LOG.error("Request was not successful: HTTP Code " + response.code());
        try {
            LOG.error(response.errorBody().string());
        } catch (IOException e) {
            LOG.error("Could not read error response", e);
        }
    }

}
