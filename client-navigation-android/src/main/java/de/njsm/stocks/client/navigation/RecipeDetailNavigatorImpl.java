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

package de.njsm.stocks.client.navigation;

import android.os.Bundle;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.Recipe;
import de.njsm.stocks.client.fragment.recipedetail.RecipeDetailFragmentArgs;
import de.njsm.stocks.client.fragment.recipedetail.RecipeDetailFragmentDirections;

import javax.inject.Inject;

class RecipeDetailNavigatorImpl extends BaseNavigator implements RecipeDetailNavigator {

    @Inject
    RecipeDetailNavigatorImpl(NavigationArgConsumer navigationArgConsumer) {
        super(navigationArgConsumer);
    }

    @Override
    public Id<Recipe> getRecipe(Bundle arguments) {
        return RecipeDetailFragmentArgs.fromBundle(arguments)::getId;
    }

    @Override
    public void edit(Id<Recipe> recipeId) {
        getNavigationArgConsumer().navigate(RecipeDetailFragmentDirections.actionNavFragmentRecipeDetailToNavFragmentRecipeEdit(recipeId.id()));
    }

    @Override
    public void prepare(Id<Recipe> recipeId) {
        getNavigationArgConsumer().navigate(RecipeDetailFragmentDirections.actionNavFragmentRecipeDetailToNavFragmentRecipeCook(recipeId.id()));
    }
}
