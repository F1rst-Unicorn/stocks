package de.njsm.stocks.client.network.sentry;


import de.njsm.stocks.client.business.StatusCode;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface SentryClient {

    @POST("v2/auth/newuser")
    Call<Result> requestCertificate(@Body int deviceId,
                                    @Body String token,
                                    @Body String csr);


    public class Result {
        public StatusCode status;
        public String pemCertificate;
    }

}
