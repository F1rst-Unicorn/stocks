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

import de.njsm.stocks.client.business.FoodItemAddService;
import de.njsm.stocks.client.business.entities.FoodItem;
import de.njsm.stocks.client.business.entities.FoodItemToAdd;
import de.njsm.stocks.client.business.entities.IdImpl;
import de.njsm.stocks.common.api.DataResponse;
import de.njsm.stocks.common.api.serialisers.InstantSerialiser;
import retrofit2.Call;

import javax.inject.Inject;

class FoodItemAddServiceImpl extends ServiceQuery<FoodItemToAdd, FoodItem> implements FoodItemAddService {

    @Inject
    FoodItemAddServiceImpl(ServerApi api, CallHandler callHandler) {
        super(api, callHandler);
    }

    @Override
    public IdImpl<FoodItem> add(FoodItemToAdd item) {
        return retrieve(item);
    }

    @Override
    Call<? extends DataResponse<Integer>> buildCall(FoodItemToAdd input) {
        return api.addFoodItem(InstantSerialiser.serialize(input.eatBy()),
                input.storedIn(),
                input.ofType(),
                input.unit());
    }
}
