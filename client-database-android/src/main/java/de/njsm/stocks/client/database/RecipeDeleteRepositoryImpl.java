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

import de.njsm.stocks.client.business.RecipeDeleteRepository;
import de.njsm.stocks.client.business.entities.VersionedId;
import de.njsm.stocks.client.business.entities.*;

import javax.inject.Inject;
import java.util.stream.Collectors;

class RecipeDeleteRepositoryImpl implements RecipeDeleteRepository {

    private final RecipeDao recipeDao;

    private final RecipeIngredientDao recipeIngredientDao;

    private final RecipeProductDao recipeProductDao;

    @Inject
    RecipeDeleteRepositoryImpl(RecipeDao recipeDao, RecipeIngredientDao recipeIngredientDao, RecipeProductDao recipeProductDao) {
        this.recipeDao = recipeDao;
        this.recipeIngredientDao = recipeIngredientDao;
        this.recipeProductDao = recipeProductDao;
    }

    @Override
    public RecipeDeleteData getData(Id<Recipe> id) {
        var recipeForDeletion = recipeDao.getRecipeVersion(id.id());
        var ingredients = recipeIngredientDao.getIngredientsForDeletionOf(id.id());
        var products = recipeProductDao.getProductsForDeletionOf(id.id());
        return RecipeDeleteData.create(
                VersionedId.create(recipeForDeletion.id, recipeForDeletion.version),
                ingredients.stream()
                        .map(v -> VersionedId.<RecipeIngredient>create(v.id, v.version))
                        .collect(Collectors.toList()),
                products.stream()
                        .map(v -> VersionedId.<RecipeProduct>create(v.id, v.version))
                        .collect(Collectors.toList()));
    }
}
