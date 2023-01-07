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

import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.Recipe;
import de.njsm.stocks.client.fragment.recipelist.RecipeListFragmentDirections;

import javax.inject.Inject;

class RecipeListNavigatorImpl extends BaseNavigator implements RecipeListNavigator {

    @Inject
    RecipeListNavigatorImpl(NavigationArgConsumer navigationArgConsumer) {
        super(navigationArgConsumer);
    }

    @Override
    public void add() {
        var direction = RecipeListFragmentDirections.actionNavFragmentRecipeListToNavFragmentRecipeAdd();
        getNavigationArgConsumer().navigate(direction);
    }

    @Override
    public void show(Id<Recipe> recipeId) {
        var direction = RecipeListFragmentDirections.actionNavFragmentRecipeListToNavFragmentRecipe(recipeId.id());
        getNavigationArgConsumer().navigate(direction);
    }
}
