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

package de.njsm.stocks.android.frontend.recipe;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import de.njsm.stocks.android.db.entities.Recipe;
import de.njsm.stocks.android.repo.RecipeRepository;
import de.njsm.stocks.common.api.FullRecipeForEditing;
import de.njsm.stocks.common.api.FullRecipeForInsertion;
import de.njsm.stocks.common.api.StatusCode;

import javax.inject.Inject;
import java.util.List;

public class RecipeViewModel extends ViewModel {

    private final RecipeRepository repository;

    private LiveData<List<Recipe>> currentRecipes;

    private LiveData<Recipe> currentRecipe;

    @Inject
    public RecipeViewModel(RecipeRepository repository) {
        this.repository = repository;
    }

    public void init() {
        if (currentRecipes == null) {
            currentRecipes = repository.getRecipes();
        }
    }

    public void init(int recipeId) {
        if (currentRecipe == null) {
            currentRecipe = repository.getRecipe(recipeId);
        }
    }

    public LiveData<List<Recipe>> getRecipes() {
        return currentRecipes;
    }

    public LiveData<StatusCode> addRecipe(FullRecipeForInsertion recipe) {
        return repository.addRecipe(recipe);
    }

    public LiveData<StatusCode> deleteRecipe(Recipe recipe) {
        return repository.deleteRecipe(recipe);
    }

    public LiveData<StatusCode> editRecipe(FullRecipeForEditing recipe) {
        return repository.editRecipe(recipe);
    }

    public LiveData<Recipe> getRecipe() {
        return currentRecipe;
    }
}