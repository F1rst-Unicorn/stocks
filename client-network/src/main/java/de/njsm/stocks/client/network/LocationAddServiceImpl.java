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

import de.njsm.stocks.client.business.LocationAddService;
import de.njsm.stocks.client.business.entities.LocationAddForm;
import de.njsm.stocks.common.api.DataResponse;
import de.njsm.stocks.common.api.LocationForInsertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;

import javax.inject.Inject;

public class LocationAddServiceImpl implements LocationAddService {

    private static final Logger LOG = LoggerFactory.getLogger(LocationAddServiceImpl.class);

    private final ServerApi api;

    private final CallHandler callHandler;

    @Inject
    public LocationAddServiceImpl(ServerApi api, CallHandler callHandler) {
        this.api = api;
        this.callHandler = callHandler;
    }

    @Override
    public void add(LocationAddForm locationAddForm) {
        LOG.debug(locationAddForm.toString());
        LocationForInsertion networkData = LocationForInsertion.builder()
                .name(locationAddForm.name())
                .description(locationAddForm.description())
                .build();

        Call<DataResponse<Integer>> call = api.addLocation(networkData);
        callHandler.executeForResult(call);
    }
}
