/*
 * stocks is client-server program to manage a household's food stock
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
 *
 */

package de.njsm.stocks.client.network;

import de.njsm.stocks.client.business.entities.RegistrationEndpoint;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.inject.Inject;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Locale;

class RegistratorApiBuilder {

    @Inject
    RegistratorApiBuilder() {
    }

    SentryApi build(RegistrationEndpoint registrationEndpoint) {
        String url = String.format(Locale.US, "https://%s:%d/", registrationEndpoint.hostname(), registrationEndpoint.port());
        return new Retrofit.Builder()
                .baseUrl(url)
                .client(getClient(registrationEndpoint.trustManagerFactory(), registrationEndpoint.keyManagerFactory()))
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(SentryApi.class);
    }

    private OkHttpClient getClient(TrustManagerFactory trustManagerFactory, KeyManagerFactory keyManagerFactory) {

        try {
            SSLContext context = SSLContext.getInstance("TLSv1.2");
            context.init(keyManagerFactory.getKeyManagers(),
                    trustManagerFactory.getTrustManagers(),
                    new SecureRandom());

            return new OkHttpClient.Builder()
                    .sslSocketFactory(context.getSocketFactory(), (X509TrustManager) trustManagerFactory.getTrustManagers()[0])
                    .hostnameVerifier((s, sslSession) -> true)
                    .build();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new NetworkException("setting up http client", e);
        }
    }
}
