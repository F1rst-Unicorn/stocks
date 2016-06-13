package de.njsm.stocks.setup;


import de.njsm.stocks.backend.data.Ticket;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface SentryClient {

    @POST("uac/newuser")
    Call<Ticket> requestCertificate(@Body Ticket ticket);

}
