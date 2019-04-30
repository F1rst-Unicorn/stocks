package de.njsm.stocks.client.network.sentry;


import de.njsm.stocks.client.business.StatusCode;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public interface SentryClient {

    @FormUrlEncoded
    @POST("v2/auth/newuser")
    Call<Result> requestCertificate(@Field("device") int deviceId,
                                    @Field("token") String token,
                                    @Field("csr") String csr);


    public class Result {
        public StatusCode status;
        public String data;
    }

}
