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

package de.njsm.stocks.client.business;

import de.njsm.stocks.client.business.entities.RecipeForListing;
import de.njsm.stocks.client.business.entities.RecipeIngredientAmount;
import de.njsm.stocks.client.business.entities.RecipesForListing;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

class RecipeListInteractorImpl implements RecipeListInteractor {

    private final RecipeListRepository repository;

    @Inject
    RecipeListInteractorImpl(RecipeListRepository repository) {
        this.repository = repository;
    }

    @Override
    public Observable<RecipesForListing> get() {
        var recipes = repository.get();
        var recipeIngredients = repository.getIngredients();

        return Observable.zip(recipes, recipeIngredients, (r, i) -> {
            var ingredientsByRecipe = i.stream()
                    .collect(groupingBy(RecipeIngredientAmount::recipe));

            List<RecipeForListing> recipesByName = r.stream().map(recipe -> {
                        var ingredients = ingredientsByRecipe.getOrDefault(recipe.id(), emptyList());
                        // optional ingredients with no required amount are considered too
                        var necessaryIngredients = normalise(ingredients.stream().filter(RecipeIngredientAmount::isNecessaryAmountPresent).count(), ingredients.size());
                        var sufficientIngredients = normalise(ingredients.stream().filter(RecipeIngredientAmount::isSufficientAmountPresent).count(), ingredients.size());

                        return RecipeForListing.create(
                                recipe.id(),
                                recipe.name(),
                                necessaryIngredients,
                                sufficientIngredients
                        );
                    })
                    .collect(toList());

            List<RecipeForListing> recipesByCookability = new ArrayList<>(recipesByName);
            recipesByCookability.sort(
                    comparing(RecipeForListing::necessaryIngredientIndex)
                            .thenComparing(RecipeForListing::sufficientIngredientIndex).reversed()
            );

            return RecipesForListing.create(recipesByName, recipesByCookability);
        });
    }

    private int normalise(long count, long total) {
        if (total == 0)
            return 7;
        return (int) Math.floor((7.0 * count) / total);
    }
}
