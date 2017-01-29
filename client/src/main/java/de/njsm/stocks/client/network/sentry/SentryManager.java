package de.njsm.stocks.client.network.sentry;

import com.squareup.okhttp.OkHttpClient;
import de.njsm.stocks.client.data.Ticket;
import de.njsm.stocks.client.exceptions.NetworkException;
import de.njsm.stocks.client.network.TcpHost;
import retrofit.*;

import java.io.IOException;

public class SentryManager {

    protected SentryClient backend;

    public SentryManager(OkHttpClient httpClient, TcpHost sentryHost) {
        String url = String.format("https://%s/", sentryHost.toString());

        backend = new Retrofit.Builder()
                .baseUrl(url)
                .client(httpClient)
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(SentryClient.class);

    }

    public String requestCertificate(Ticket requestTicket) throws NetworkException {
        Response<Ticket> response = executeCall(requestTicket);
        return extractResponse(response);
    }

    private Response<Ticket> executeCall(Ticket ticket) throws NetworkException {
        try {
            Call<Ticket> callback = backend.requestCertificate(ticket);
            return callback.execute();
        } catch (IOException e) {
            // TODO Log
            throw new NetworkException("Connection to sentry failed", e);
        }
    }

    private String extractResponse(Response<Ticket> response) throws NetworkException {
        if (response.isSuccess()) {
            Ticket responseTicket = response.body();
            if (responseTicket.pemFile == null) {
                // TODO Log
                throw new NetworkException("Server rejected ticket!");
            }
            return responseTicket.pemFile;
        } else {
            // TODO Log
            throw new NetworkException("Sentry returned error");
        }
    }

}
