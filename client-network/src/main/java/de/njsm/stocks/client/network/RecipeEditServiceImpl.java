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

import de.njsm.stocks.client.business.RecipeEditService;
import de.njsm.stocks.client.business.entities.RecipeEditNetworkData;
import de.njsm.stocks.common.api.*;
import retrofit2.Call;

import javax.inject.Inject;
import java.util.stream.Collectors;

class RecipeEditServiceImpl extends ServiceBase<RecipeEditNetworkData> implements RecipeEditService {

    @Inject
    RecipeEditServiceImpl(ServerApi api, CallHandler callHandler) {
        super(api, callHandler);
    }

    @Override
    public void edit(RecipeEditNetworkData recipe) {
        perform(recipe);
    }

    @Override
    Call<? extends Response> buildCall(RecipeEditNetworkData input) {
        return api.editRecipe(FullRecipeForEditing.builder()
                        .recipe(RecipeForEditing.builder()
                                .id(input.recipe().id())
                                .version(input.recipe().version())
                                .name(input.recipe().name())
                                .instructions(input.recipe().instructions())
                                .duration(input.recipe().duration())
                                .build())
                        .ingredients(input.ingredients().stream()
                                .map(v -> RecipeIngredientForEditing.builder()
                                        .id(v.id())
                                        .version(v.version())
                                        .recipe(input.recipe().id())
                                        .amount(v.amount())
                                        .unit(v.unit().id())
                                        .ingredient(v.ingredient().id())
                                        .build())
                                .collect(Collectors.toSet()))
                        .ingredientsToInsert(input.ingredientsToAdd().stream()
                                .map(v -> RecipeIngredientForInsertion.builder()
                                        .amount(v.amount())
                                        .unit(v.unit().id())
                                        .ingredient(v.ingredient().id())
                                        .build())
                                .collect(Collectors.toSet()))
                        .ingredientsToDelete(input.ingredientsToDelete().stream()
                                .map(v -> RecipeIngredientForDeletion.builder()
                                        .id(v.id())
                                        .version(v.version())
                                        .build())
                                .collect(Collectors.toSet()))
                        .products(input.products().stream()
                                .map(v -> RecipeProductForEditing.builder()
                                        .id(v.id())
                                        .version(v.version())
                                        .recipe(input.recipe().id())
                                        .amount(v.amount())
                                        .unit(v.unit().id())
                                        .product(v.product().id())
                                        .build())
                                .collect(Collectors.toSet()))
                        .productsToInsert(input.productsToAdd().stream()
                                .map(v -> RecipeProductForInsertion.builder()
                                        .amount(v.amount())
                                        .unit(v.unit().id())
                                        .product(v.product().id())
                                        .build())
                                .collect(Collectors.toSet()))
                        .productsToDelete(input.productsToDelete().stream()
                                .map(v -> RecipeProductForDeletion.builder()
                                        .id(v.id())
                                        .version(v.version())
                                        .build())
                                .collect(Collectors.toSet()))
                .build());
    }
}
