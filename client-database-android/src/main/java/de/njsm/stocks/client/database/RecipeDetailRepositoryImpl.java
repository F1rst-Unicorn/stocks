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

import de.njsm.stocks.client.business.RecipeDetailRepository;
import de.njsm.stocks.client.business.entities.*;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

class RecipeDetailRepositoryImpl implements RecipeDetailRepository {

    private final RecipeDao recipeDao;

    @Inject
    RecipeDetailRepositoryImpl(RecipeDao recipeDao) {
        this.recipeDao = recipeDao;
    }

    @Override
    public Observable<RecipeForDetailsBaseData> get(Id<Recipe> recipeId) {
        return recipeDao.getRecipe(recipeId.id());
    }

    @Override
    public Observable<List<PresentRecipeFoodForDetailsBaseData>> getIngredientsPresentAmountsOf(Id<Recipe> recipeId) {
        return recipeDao.getIngredientsPresentAmountsOf(recipeId.id());
    }

    @Override
    public Observable<List<RecipeFoodForDetailsBaseData>> getIngredientsRequiredAmountOf(Id<Recipe> recipeId) {
        return recipeDao.getIngredientsRequiredAmountOf(recipeId.id());
    }

    @Override
    public Observable<List<PresentRecipeFoodForDetailsBaseData>> getProductsPresentAmountsOf(Id<Recipe> recipeId) {
        return recipeDao.getProductsPresentAmountsOf(recipeId.id());
    }

    @Override
    public Observable<List<RecipeFoodForDetailsBaseData>> getProductsProducedAmountOf(Id<Recipe> recipeId) {
        return recipeDao.getProductsProducedAmountOf(recipeId.id());
    }
}
