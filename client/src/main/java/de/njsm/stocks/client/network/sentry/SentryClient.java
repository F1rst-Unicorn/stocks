package de.njsm.stocks.client.network.sentry;


import de.njsm.stocks.common.data.Ticket;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface SentryClient {

    @POST("uac/newuser")
    Call<Ticket> requestCertificate(@Body Ticket ticket);

}
