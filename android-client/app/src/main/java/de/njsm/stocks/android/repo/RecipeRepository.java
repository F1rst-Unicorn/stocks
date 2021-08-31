/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2021  The stocks developers
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
 */

package de.njsm.stocks.android.repo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import de.njsm.stocks.android.db.dao.RecipeDao;
import de.njsm.stocks.android.db.dao.RecipeIngredientDao;
import de.njsm.stocks.android.db.dao.RecipeProductDao;
import de.njsm.stocks.android.db.entities.Recipe;
import de.njsm.stocks.android.network.server.ServerClient;
import de.njsm.stocks.android.network.server.StatusCodeCallback;
import de.njsm.stocks.android.util.Logger;
import de.njsm.stocks.android.util.idling.IdlingResource;
import de.njsm.stocks.common.api.*;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class RecipeRepository {

    private static final Logger LOG = new Logger(RecipeRepository.class);

    private final RecipeDao recipeDao;

    private final RecipeIngredientDao recipeIngredientDao;

    private final RecipeProductDao recipeProductDao;

    private final ServerClient webClient;

    private final Synchroniser synchroniser;

    private final Executor executor;

    private final IdlingResource idlingResource;

    @Inject
    public RecipeRepository(RecipeDao recipeDao,
                            RecipeIngredientDao recipeIngredientDao,
                            RecipeProductDao recipeProductDao,
                            ServerClient webClient,
                            Synchroniser synchroniser,
                            Executor executor, IdlingResource idlingResource) {
        this.recipeDao = recipeDao;
        this.recipeIngredientDao = recipeIngredientDao;
        this.recipeProductDao = recipeProductDao;
        this.webClient = webClient;
        this.synchroniser = synchroniser;
        this.executor = executor;
        this.idlingResource = idlingResource;
    }


    public LiveData<List<Recipe>> getRecipes() {
        return recipeDao.getAll();
    }

    public LiveData<StatusCode> addRecipe(FullRecipeForInsertion recipe) {
        LOG.i("adding recipe " + recipe);
        MediatorLiveData<StatusCode> result = new MediatorLiveData<>();

        webClient.addRecipe(recipe)
                .enqueue(new StatusCodeCallback(result, synchroniser, idlingResource));
        return result;
    }

    public LiveData<StatusCode> deleteRecipe(Recipe recipe) {
        LOG.i("deleting recipe " + recipe);
        MediatorLiveData<StatusCode> result = new MediatorLiveData<>();

        executor.execute(() -> {
            RecipeForDeletion recipeForDeletion = RecipeForDeletion.builder()
                    .id(recipe.getId())
                    .version(recipe.getVersion())
                    .build();

            FullRecipeForDeletion fullRecipeForDeletion = FullRecipeForDeletion.builder()
                    .recipe(recipeForDeletion)
                    .ingredients(recipeIngredientDao.getIngredientsOf(recipe.getId()).stream()
                            .map(ingredient -> RecipeIngredientForDeletion.builder()
                                    .id(ingredient.getId())
                                    .version(ingredient.getVersion())
                                    .build()
                            ).collect(Collectors.toSet())
                    )
                    .products(recipeProductDao.getProductsOf(recipe.getId()).stream()
                            .map(product -> RecipeProductForDeletion.builder()
                                    .id(product.getId())
                                    .version(product.getVersion())
                                    .build()
                            ).collect(Collectors.toSet())
                    )
                    .build();

            webClient.deleteRecipe(fullRecipeForDeletion)
                    .enqueue(new StatusCodeCallback(result, synchroniser, idlingResource));
        });

        return result;
    }

    public LiveData<Recipe> getRecipe(int recipeId) {
        return recipeDao.getRecipe(recipeId);
    }
}
