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

import de.njsm.stocks.client.business.UpdateService;
import de.njsm.stocks.client.business.entities.LocationForSynchronisation;
import de.njsm.stocks.client.business.entities.Update;
import de.njsm.stocks.client.business.entities.UserDeviceForSynchronisation;
import de.njsm.stocks.client.business.entities.UserForSynchronisation;
import de.njsm.stocks.common.api.BitemporalLocation;
import de.njsm.stocks.common.api.BitemporalUser;
import de.njsm.stocks.common.api.BitemporalUserDevice;
import de.njsm.stocks.common.api.ListResponse;
import de.njsm.stocks.common.api.serialisers.InstantSerialiser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;

import javax.inject.Inject;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UpdateServiceImpl implements UpdateService {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateServiceImpl.class);

    private final ServerApi api;

    private final CallHandler callHandler;

    @Inject
    public UpdateServiceImpl(ServerApi api, CallHandler callHandler) {
        this.api = api;
        this.callHandler = callHandler;
    }

    @Override
    public List<Update> getUpdates() {
        LOG.debug("getting updates");
        Call<ListResponse<de.njsm.stocks.common.api.Update>> call = api.getUpdates();
        return callHandler.executeForResult(call)
                .stream()
                .map(DataMapper::map)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<LocationForSynchronisation> getLocations(Instant startingFrom) {
        LOG.debug("getting locations from " + startingFrom);
        Call<ListResponse<BitemporalLocation>> call = api.getLocations(1, InstantSerialiser.serialize(startingFrom));
        return callHandler.executeForResult(call)
                .stream().map(DataMapper::map).collect(Collectors.toList());
    }

    @Override
    public List<UserForSynchronisation> getUsers(Instant startingFrom) {
        LOG.debug("getting users from " + startingFrom);
        Call<ListResponse<BitemporalUser>> call = api.getUsers(1, InstantSerialiser.serialize(startingFrom));
        return callHandler.executeForResult(call)
                .stream().map(DataMapper::map).collect(Collectors.toList());
    }

    @Override
    public List<UserDeviceForSynchronisation> getUserDevices(Instant startingFrom) {
        LOG.debug("getting user devices from " + startingFrom);
        Call<ListResponse<BitemporalUserDevice>> call = api.getUserDevices(1, InstantSerialiser.serialize(startingFrom));
        return callHandler.executeForResult(call)
                .stream().map(DataMapper::map).collect(Collectors.toList());
    }
}
