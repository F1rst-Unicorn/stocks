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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import de.njsm.stocks.client.business.*;
import de.njsm.stocks.client.business.entities.*;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.inject.Singleton;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Locale;

@Module
public interface NetworkModule {

    @Binds
    CertificateFetcher certificateFetcherBuilder(CertificateFetcherImpl impl);

    @Binds
    Registrator registratorBuilder(RegistratorImpl impl);

    @Binds
    UpdateService updateService(UpdateServiceImpl impl);

    @Binds
    LocationAddService locationAddService(LocationAddServiceImpl impl);

    @Binds
    EntityDeleteService<Location> locationDeleteService(LocationDeleteServiceImpl impl);

    @Binds
    LocationEditService locationEditService(LocationEditServiceImpl impl);

    @Binds
    UnitAddService unitAddService(UnitAddServiceImpl impl);

    @Binds
    EntityDeleteService<Unit> unitDeleteService(UnitDeleteServiceImpl impl);

    @Binds
    UnitEditService unitEditService(UnitEditServiceImpl impl);

    @Binds
    ScaledUnitAddService ScaledUnitAddService(ScaledUnitAddServiceImpl impl);

    @Binds
    ScaledUnitEditService ScaledUnitEditService(ScaledUnitEditServiceImpl impl);

    @Binds
    EntityDeleteService<ScaledUnit> ScaledUnitDeleteServiceImpl(ScaledUnitDeleteServiceImpl impl);

    @Binds
    FoodAddService FoodAddService(FoodAddServiceImpl impl);

    @Binds
    EntityDeleteService<Food> foodDeleteService(FoodDeleteServiceImpl impl);

    @Binds
    FoodEditService FoodEditService(FoodEditServiceImpl impl);

    @Binds
    FoodItemAddService FoodItemAddService(FoodItemAddServiceImpl impl);

    @Binds
    EntityDeleteService<FoodItem> foodItemDeleteService(FoodItemDeleteServiceImpl impl);


    @Provides
    @Singleton
    static ServerApi serverApi(ServerEndpoint serverEndpoint) {
        String url = String.format(Locale.US, "https://%s:%d/", serverEndpoint.hostname(), serverEndpoint.port());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());

        return new Retrofit.Builder()
                .baseUrl(url)
                .client(getClient(serverEndpoint.trustManagerFactory(), serverEndpoint.keyManagerFactory()))
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build()
                .create(ServerApi.class);
    }

    static OkHttpClient getClient(TrustManagerFactory trustManagerFactory, KeyManagerFactory keyManagerFactory) {
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
