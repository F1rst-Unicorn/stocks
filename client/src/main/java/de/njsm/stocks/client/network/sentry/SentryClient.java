package de.njsm.stocks.client.network.sentry;


import de.njsm.stocks.client.data.Ticket;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.POST;


public interface SentryClient {

    @POST("uac/newuser")
    Call<Ticket> requestCertificate(@Body Ticket ticket);

}
