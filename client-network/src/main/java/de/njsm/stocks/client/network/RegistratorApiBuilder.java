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
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.inject.Inject;
import java.util.Locale;

import static de.njsm.stocks.client.network.NetworkModule.getClient;

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
}
