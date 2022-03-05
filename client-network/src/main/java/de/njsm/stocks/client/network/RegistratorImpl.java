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

import de.njsm.stocks.client.business.Registrator;
import de.njsm.stocks.client.business.entities.RegistrationCsr;
import de.njsm.stocks.client.business.entities.RegistrationEndpoint;
import de.njsm.stocks.common.api.DataResponse;
import retrofit2.Call;

import javax.inject.Inject;

class RegistratorImpl implements Registrator {

    private final RegistratorApiBuilder registratorApiBuilder;

    @Inject
    RegistratorImpl(RegistratorApiBuilder registratorApiBuilder) {
        this.registratorApiBuilder = registratorApiBuilder;
    }

    @Override
    public String getOwnCertificate(RegistrationEndpoint registrationEndpoint, RegistrationCsr request) {
        Call<DataResponse<String>> call = getApi(registrationEndpoint).requestCertificate(request.deviceId(), request.ticket(), request.csr());
        return new CallHandler().executeForResult(call);
    }

    private SentryApi getApi(RegistrationEndpoint registrationEndpoint) {
        return registratorApiBuilder.build(registrationEndpoint);
    }
}
