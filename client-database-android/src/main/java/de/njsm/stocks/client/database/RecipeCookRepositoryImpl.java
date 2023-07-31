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

package de.njsm.stocks.client.database;

import de.njsm.stocks.client.business.RecipeCookRepository;
import de.njsm.stocks.client.business.entities.*;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import static de.njsm.stocks.client.business.entities.IdImpl.create;
import static java.util.stream.Collectors.toList;

class RecipeCookRepositoryImpl implements RecipeCookRepository {

    private final RecipeDao recipeDao;

    private final RecipeIngredientDao recipeIngredientDao;

    private final RecipeProductDao recipeProductDao;

    private final FoodItemDao foodItemDao;

    @Inject
    RecipeCookRepositoryImpl(RecipeDao recipeDao, RecipeIngredientDao recipeIngredientDao, RecipeProductDao recipeProductDao, FoodItemDao foodItemDao) {
        this.recipeDao = recipeDao;
        this.recipeIngredientDao = recipeIngredientDao;
        this.recipeProductDao = recipeProductDao;
        this.foodItemDao = foodItemDao;
    }

    @Override
    public Observable<RecipeForCooking> getRecipe(IdImpl<Recipe> recipeId) {
        return recipeDao.getRecipeBy(recipeId.id())
                .map(v -> RecipeForCooking.create(recipeId, v.name()));
    }

    @Override
    public Observable<List<RecipeIngredientForCooking>> getRequiredIngredients(IdImpl<Recipe> recipeId) {
        return recipeIngredientDao.getCookingIngredientsOf(recipeId.id())
                .map(l -> l.stream().map(v -> RecipeIngredientForCooking.create(
                        create(v.food),
                        v.foodName,
                        v.toBuy,
                        create(v.unit),
                        v.abbreviation,
                        v.scale.multiply(new BigDecimal(v.amount))
                )).collect(toList()));
    }

    @Override
    public Observable<List<FoodItemForCooking>> getPresentIngredients(IdImpl<Recipe> recipeId) {
        return recipeIngredientDao.getPresentCookingIngredientsOf(recipeId.id())
                .map(l -> l.stream().map(v -> FoodItemForCooking.create(
                        create(v.food),
                        create(v.unit),
                        v.abbreviation,
                        create(v.scaledUnit),
                        v.scale,
                        v.presentCount
                )).collect(toList()));
    }

    @Override
    public Observable<List<RecipeCookingFormDataProduct>> getProducts(IdImpl<Recipe> recipeId) {
        return recipeProductDao.getProductsBy(recipeId.id())
                .map(l -> l.stream().map(v -> RecipeCookingFormDataProduct.create(
                        create(v.product),
                        v.name,
                        RecipeCookingFormDataProduct.Amount.create(
                                create(v.unit),
                                v.scale,
                                v.abbreviation,
                                v.amount
                        )
                )).collect(toList()));
    }

    @Override
    public List<FoodItemForDeletion> getFoodItemsForCooking(List<RecipeCookingIngredientToConsume> ingredients) {
        return ingredients.stream()
                .map(v -> foodItemDao.getItemsMatching(v.food().id(), v.scaledUnit().id(), v.count()))
                .flatMap(Collection::stream)
                .map(v -> FoodItemForDeletion.create(v.id(), v.version()))
                .collect(toList());
    }
}
