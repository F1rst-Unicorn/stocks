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

import de.njsm.stocks.client.business.FoodEditService;
import de.njsm.stocks.client.business.entities.FoodForEditing;
import de.njsm.stocks.common.api.FoodForFullEditing;
import de.njsm.stocks.common.api.Response;
import retrofit2.Call;

import javax.inject.Inject;

class FoodEditServiceImpl extends ServiceCommand<FoodForEditing> implements FoodEditService {

    @Inject
    FoodEditServiceImpl(ServerApi api, CallHandler callHandler) {
        super(api, callHandler);
    }

    @Override
    public void edit(FoodForEditing food) {
        perform(food);
    }

    @Override
    Call<Response> buildCall(FoodForEditing input) {
        int days = input.expirationOffset().getDays();
        return api.editFood(FoodForFullEditing.builder()
                .id(input.id())
                .version(input.version())
                .name(input.name())
                .toBuy(input.toBuy())
                .expirationOffset(days > 0 ? days : null)
                .location(input.location().orElse(0))
                .description(input.description())
                .storeUnit(input.storeUnit())
                .build());
    }
}
