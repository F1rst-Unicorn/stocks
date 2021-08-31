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
import de.njsm.stocks.android.db.entities.Recipe;
import de.njsm.stocks.android.network.server.ServerClient;
import de.njsm.stocks.android.network.server.StatusCodeCallback;
import de.njsm.stocks.android.util.Logger;
import de.njsm.stocks.android.util.idling.IdlingResource;
import de.njsm.stocks.common.api.FullRecipeForInsertion;
import de.njsm.stocks.common.api.StatusCode;

import javax.inject.Inject;
import java.util.List;

public class RecipeRepository {

    private static final Logger LOG = new Logger(RecipeRepository.class);

    private final RecipeDao recipeDao;

    private final ServerClient webClient;

    private final Synchroniser synchroniser;

    private final IdlingResource idlingResource;

    @Inject
    public RecipeRepository(RecipeDao recipeDao,
                            ServerClient webClient,
                            Synchroniser synchroniser,
                            IdlingResource idlingResource) {
        this.recipeDao = recipeDao;
        this.webClient = webClient;
        this.synchroniser = synchroniser;
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
}
