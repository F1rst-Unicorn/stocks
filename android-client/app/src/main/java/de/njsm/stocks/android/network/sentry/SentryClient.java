package de.njsm.stocks.android.network.sentry;


import de.njsm.stocks.android.network.server.StatusCode;
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


    class Result {
        public StatusCode status;
        public String data;
    }

}
