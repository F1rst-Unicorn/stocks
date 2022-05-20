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

import de.njsm.stocks.client.business.ScaledUnitAddService;
import de.njsm.stocks.client.business.StatusCodeException;
import de.njsm.stocks.client.business.entities.ScaledUnitAddForm;
import de.njsm.stocks.common.api.Response;
import de.njsm.stocks.common.api.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;

import javax.inject.Inject;

import static de.njsm.stocks.client.network.DataMapper.map;

class ScaledUnitAddServiceImpl implements ScaledUnitAddService {

    private static final Logger LOG = LoggerFactory.getLogger(ScaledUnitAddServiceImpl.class);

    private final ServerApi api;

    private final CallHandler callHandler;

    @Inject
    ScaledUnitAddServiceImpl(ServerApi api, CallHandler callHandler) {
        this.api = api;
        this.callHandler = callHandler;
    }

    @Override
    public void add(ScaledUnitAddForm form) {
        LOG.debug(form.toString());
        Call<Response> call = api.addScaledUnit(form.scale().toPlainString(), form.unit());
        StatusCode result = callHandler.executeCommand(call);
        if (result.isFail())
            throw new StatusCodeException(map(result));
    }
}
