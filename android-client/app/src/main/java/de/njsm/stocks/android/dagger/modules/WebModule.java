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

package de.njsm.stocks.android.dagger.modules;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import dagger.Module;
import dagger.Provides;
import de.njsm.stocks.android.network.server.ServerClient;
import de.njsm.stocks.android.util.Config;
import de.njsm.stocks.android.util.Logger;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.inject.Singleton;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Locale;


@Module
public class WebModule {

    private static final Logger LOG = new Logger(WebModule.class);

    @Provides
    SharedPreferences provideStocksPreferences(Application ctx) {
        return ctx.getSharedPreferences(Config.PREFERENCES_FILE, Context.MODE_PRIVATE);
    }

    @Provides
    InputStream provideKeystoreStream(Application ctx) {
        try {
            return ctx.openFileInput(Config.KEYSTORE_FILE);
        } catch (FileNotFoundException e) {
            LOG.e("keystore doesn't exist", e);
            throw new RuntimeException(e);
        }
    }

    @Provides
    public static OkHttpClient getClient(InputStream keystoreStream) {

        try {
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(keystoreStream, Config.PASSWORD.toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ks);

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, Config.PASSWORD.toCharArray());

            SSLContext context = SSLContext.getInstance("TLSv1.2");
            context.init(kmf.getKeyManagers(),
                    tmf.getTrustManagers(),
                    new SecureRandom());

            return new OkHttpClient.Builder()
                    .sslSocketFactory(context.getSocketFactory(), (X509TrustManager) tmf.getTrustManagers()[0])
                    .hostnameVerifier((s, sslSession) -> true)
                    .build();
        } catch (Exception e) {
            LOG.e("Error creating http client", e);
            throw new RuntimeException(e);
        }
    }


    @Provides
    @Singleton
    static ServerClient provideServerClient(SharedPreferences prefs, OkHttpClient httpClient) {
        try {
            String url = String.format(Locale.US, "https://%s:%d/",
                    prefs.getString(Config.SERVER_NAME_CONFIG, ""),
                    prefs.getInt(Config.SERVER_PORT_CONFIG, 0));

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .client(httpClient)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();

            return retrofit.create(ServerClient.class);
        } catch (Exception e) {
            LOG.e("Could not create server client", e);
            throw new RuntimeException(e);
        }
    }


}
