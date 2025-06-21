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
import de.njsm.stocks.client.business.entities.Update;
import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.common.api.*;
import de.njsm.stocks.common.api.serialisers.InstantSerialiser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;

import javax.inject.Inject;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class UpdateServiceImpl implements UpdateService {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateServiceImpl.class);

    private final ServerApi api;

    private final CallHandler callHandler;

    @Inject
    UpdateServiceImpl(ServerApi api, CallHandler callHandler) {
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
                .collect(toList());
    }

    @Override
    public List<LocationForSynchronisation> getLocations(Instant startingFrom, Instant upUntil) {
        LOG.debug("getting locations from " + startingFrom);
        Call<ListResponse<BitemporalLocation>> call = api.getLocations(InstantSerialiser.serialize(startingFrom), InstantSerialiser.serialize(upUntil));
        return callHandler.executeForResult(call)
                .stream().map(DataMapper::map).collect(toList());
    }

    @Override
    public List<UserForSynchronisation> getUsers(Instant startingFrom, Instant upUntil) {
        LOG.debug("getting users from " + startingFrom);
        Call<ListResponse<BitemporalUser>> call = api.getUsers(InstantSerialiser.serialize(startingFrom), InstantSerialiser.serialize(upUntil));
        return callHandler.executeForResult(call)
                .stream().map(DataMapper::map).collect(toList());
    }

    @Override
    public List<UserDeviceForSynchronisation> getUserDevices(Instant startingFrom, Instant upUntil) {
        LOG.debug("getting user devices from " + startingFrom);
        Call<ListResponse<BitemporalUserDevice>> call = api.getUserDevices(InstantSerialiser.serialize(startingFrom), InstantSerialiser.serialize(upUntil));
        return callHandler.executeForResult(call)
                .stream().map(DataMapper::map).collect(toList());
    }

    @Override
    public List<FoodForSynchronisation> getFood(Instant startingFrom, Instant upUntil) {
        LOG.debug("getting food from " + startingFrom);
        Call<ListResponse<BitemporalFood>> call = api.getFood(InstantSerialiser.serialize(startingFrom), InstantSerialiser.serialize(upUntil));
        return callHandler.executeForResult(call)
                .stream().map(DataMapper::map).collect(toList());
    }

    @Override
    public List<EanNumberForSynchronisation> getEanNumbers(Instant startingFrom, Instant upUntil) {
        LOG.debug("getting ean numbers from " + startingFrom);
        Call<ListResponse<BitemporalEanNumber>> call = api.getEanNumbers(InstantSerialiser.serialize(startingFrom), InstantSerialiser.serialize(upUntil));
        return callHandler.executeForResult(call)
                .stream().map(DataMapper::map).collect(toList());
    }

    @Override
    public List<FoodItemForSynchronisation> getFoodItems(Instant startingFrom, Instant upUntil) {
        LOG.debug("getting food items from " + startingFrom);
        Call<ListResponse<BitemporalFoodItem>> call = api.getFoodItems(InstantSerialiser.serialize(startingFrom), InstantSerialiser.serialize(upUntil));
        return callHandler.executeForResult(call)
                .stream().map(DataMapper::map).collect(toList());
    }

    @Override
    public List<UnitForSynchronisation> getUnits(Instant startingFrom, Instant upUntil) {
        LOG.debug("getting units from " + startingFrom);
        Call<ListResponse<BitemporalUnit>> call = api.getUnits(InstantSerialiser.serialize(startingFrom), InstantSerialiser.serialize(upUntil));
        return callHandler.executeForResult(call)
                .stream().map(DataMapper::map).collect(toList());
    }

    @Override
    public List<ScaledUnitForSynchronisation> getScaledUnits(Instant startingFrom, Instant upUntil) {
        LOG.debug("getting scaled units from " + startingFrom);
        Call<ListResponse<BitemporalScaledUnit>> call = api.getScaledUnits(InstantSerialiser.serialize(startingFrom), InstantSerialiser.serialize(upUntil));
        return callHandler.executeForResult(call)
                .stream().map(DataMapper::map).collect(toList());
    }

    @Override
    public List<RecipeForSynchronisation> getRecipes(Instant startingFrom, Instant upUntil) {
        LOG.debug("getting recipes from " + startingFrom);
        Call<ListResponse<BitemporalRecipe>> call = api.getRecipes(InstantSerialiser.serialize(startingFrom), InstantSerialiser.serialize(upUntil));
        return callHandler.executeForResult(call)
                .stream().map(DataMapper::map).collect(toList());
    }

    @Override
    public List<RecipeIngredientForSynchronisation> getRecipeIngredients(Instant startingFrom, Instant upUntil) {
        LOG.debug("getting recipe ingredients from " + startingFrom);
        Call<ListResponse<BitemporalRecipeIngredient>> call = api.getRecipeIngredients(InstantSerialiser.serialize(startingFrom), InstantSerialiser.serialize(upUntil));
        return callHandler.executeForResult(call)
                .stream().map(DataMapper::map).collect(toList());
    }

    @Override
    public List<RecipeProductForSynchronisation> getRecipeProducts(Instant startingFrom, Instant upUntil) {
        LOG.debug("getting recipe products from " + startingFrom);
        Call<ListResponse<BitemporalRecipeProduct>> call = api.getRecipeProducts(InstantSerialiser.serialize(startingFrom), InstantSerialiser.serialize(upUntil));
        return callHandler.executeForResult(call)
                .stream().map(DataMapper::map).collect(toList());
    }
}
