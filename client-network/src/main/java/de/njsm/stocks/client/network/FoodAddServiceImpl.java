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

import de.njsm.stocks.client.business.FoodAddService;
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.FoodAddForm;
import de.njsm.stocks.client.business.entities.IdImpl;
import de.njsm.stocks.common.api.DataResponse;
import de.njsm.stocks.common.api.FoodForInsertion;
import retrofit2.Call;

import javax.inject.Inject;

class FoodAddServiceImpl extends ServiceQuery<FoodAddForm, Food> implements FoodAddService {

    @Inject
    FoodAddServiceImpl(ServerApi api, CallHandler callHandler) {
        super(api, callHandler);
    }

    @Override
    public IdImpl<Food> add(FoodAddForm form) {
        return retrieve(form);
    }

    @Override
    Call<? extends DataResponse<Integer>> buildCall(FoodAddForm input) {
        int days = input.expirationOffset().getDays();
        FoodForInsertion networkData = FoodForInsertion.builder()
                .name(input.name())
                .toBuy(input.toBuy())
                .expirationOffset(days > 0 ? days : null)
                .location(input.location().orElse(null))
                .storeUnit(input.storeUnit())
                .description(input.description())
                .build();

        return api.addFood(networkData);
    }
}
