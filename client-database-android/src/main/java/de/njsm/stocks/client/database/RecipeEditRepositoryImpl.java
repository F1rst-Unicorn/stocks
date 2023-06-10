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

import de.njsm.stocks.client.business.RecipeEditRepository;
import de.njsm.stocks.client.business.ScaledUnitRepository;
import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.business.entities.VersionedId;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

class RecipeEditRepositoryImpl implements RecipeEditRepository {

    private final RecipeDao recipeDao;

    private final RecipeIngredientDao recipeIngredientDao;

    private final RecipeProductDao recipeProductDao;

    private final FoodDao foodDao;

    private final ScaledUnitRepository scaledUnitRepository;

    @Inject
    RecipeEditRepositoryImpl(RecipeDao recipeDao, RecipeIngredientDao recipeIngredientDao, RecipeProductDao recipeProductDao, FoodDao foodDao, ScaledUnitRepository scaledUnitRepository) {
        this.recipeDao = recipeDao;
        this.recipeIngredientDao = recipeIngredientDao;
        this.recipeProductDao = recipeProductDao;
        this.foodDao = foodDao;
        this.scaledUnitRepository = scaledUnitRepository;
    }

    @Override
    public Maybe<RecipeEditBaseData> getRecipe(Id<Recipe> recipeId) {
        return recipeDao.getRecipeBy(recipeId.id())
                .map(v -> RecipeEditBaseData.create(
                        v.id(),
                        v.name(),
                        v.instructions(),
                        v.duration()
                ))
                .firstElement();
    }

    @Override
    public Observable<List<FoodForSelection>> getFood() {
        return foodDao.getForSelection();
    }

    @Override
    public Observable<List<ScaledUnitForSelection>> getUnits() {
        return scaledUnitRepository.getScaledUnitsForSelection();
    }

    @Override
    public Maybe<List<RecipeIngredientEditData>> getIngredients(Id<Recipe> recipeId) {
        return recipeIngredientDao.getIngredientsOf(recipeId.id())
                .map(l -> l.stream().map(v -> RecipeIngredientEditData.create(
                        v.id(),
                        v.amount(),
                        IdImpl.create(v.unit()),
                        IdImpl.create(v.ingredient())
                        ))
                        .collect(Collectors.toList())
                );
    }

    @Override
    public Maybe<List<RecipeProductEditData>> getProducts(Id<Recipe> recipeId) {
        return recipeProductDao.getProductsOf(recipeId.id())
                .map(l -> l.stream().map(v -> RecipeProductEditData.create(
                                        v.id(),
                                        v.amount(),
                                        IdImpl.create(v.unit()),
                                        IdImpl.create(v.product())
                                ))
                                .collect(Collectors.toList())
                );
    }

    @Override
    public Versionable<Recipe> getRecipeVersion(Id<Recipe> recipe) {
        var version = recipeDao.getRecipeVersion(recipe.id());
        return VersionedId.create(version.id, version.version);
    }

    @Override
    public Versionable<RecipeIngredient> getRecipeIngredientVersion(Id<RecipeIngredient> v) {
        var version = recipeIngredientDao.getIngredientVersion(v.id());
        return VersionedId.create(version.id, version.version);
    }

    @Override
    public Versionable<RecipeProduct> getRecipeProductVersion(Id<RecipeProduct> v) {
        var version = recipeProductDao.getProductVersion(v.id());
        return VersionedId.create(version.id, version.version);
    }
}
