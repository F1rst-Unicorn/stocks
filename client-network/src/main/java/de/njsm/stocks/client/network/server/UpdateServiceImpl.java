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

package de.njsm.stocks.client.network.server;

import de.njsm.stocks.client.business.UpdateService;
import de.njsm.stocks.client.business.entities.LocationForSynchronisation;
import de.njsm.stocks.client.business.entities.Update;
import de.njsm.stocks.common.api.BitemporalLocation;
import de.njsm.stocks.common.api.ListResponse;
import de.njsm.stocks.common.api.serialisers.InstantSerialiser;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Call;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public class UpdateServiceImpl implements UpdateService {

    private final Api api;

    private final CallHandler callHandler;

    public UpdateServiceImpl(Api api, CallHandler callHandler) {
        this.api = api;
        this.callHandler = callHandler;
    }

    @Override
    public Single<List<Update>> getUpdates() {
        Call<ListResponse<de.njsm.stocks.common.api.Update>> call = api.getUpdates();
        return Single.fromCallable(() -> callHandler.executeForResult(call))
                .map(l -> l.stream().map(DataMapper::map).collect(Collectors.toList()));
    }

    @Override
    public Single<List<LocationForSynchronisation>> getLocations(Instant startingFrom) {
        Call<ListResponse<BitemporalLocation>> call = api.getLocations(1, InstantSerialiser.serialize(startingFrom));
        return Single.fromCallable(() -> callHandler.executeForResult(call))
                .map(l -> l.stream().map(DataMapper::map).collect(Collectors.toList()));
    }

}
