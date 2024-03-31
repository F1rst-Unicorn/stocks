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

import de.njsm.stocks.client.business.RecipeAddService;
import de.njsm.stocks.client.business.entities.IdImpl;
import de.njsm.stocks.client.business.entities.Recipe;
import de.njsm.stocks.client.business.entities.RecipeAddForm;
import de.njsm.stocks.common.api.*;
import retrofit2.Call;

import javax.inject.Inject;

import static java.util.stream.Collectors.toList;

class RecipeAddServiceImpl extends ServiceQuery<RecipeAddForm, Recipe> implements RecipeAddService {

    @Inject
    RecipeAddServiceImpl(ServerApi api, CallHandler callHandler) {
        super(api, callHandler);
    }

    @Override
    public IdImpl<Recipe> add(RecipeAddForm form) {
        return retrieve(form);
    }

    @Override
    Call<? extends DataResponse<Integer>> buildCall(RecipeAddForm input) {
        FullRecipeForInsertion networkData = FullRecipeForInsertion.builder()
                .recipe(RecipeForInsertion.builder()
                        .name(input.name())
                        .instructions(input.instructions())
                        .duration(input.duration())
                        .build())
                .ingredients(input.ingredients().stream().map(v ->
                        RecipeIngredientForInsertion.builder()
                                .amount(v.amount())
                                .ingredient(v.ingredient().id())
                                .unit(v.unit().id())
                                .build())
                        .collect(toList()))
                .products(input.products().stream().map(v ->
                        RecipeProductForInsertion.builder()
                                .amount(v.amount())
                                .product(v.product().id())
                                .unit(v.unit().id())
                                .build())
                        .collect(toList()))
                .build();
        return api.addRecipe(networkData);
    }
}
