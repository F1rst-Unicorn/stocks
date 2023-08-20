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

package de.njsm.stocks.client.testdata;


import de.njsm.stocks.client.business.entities.RecipeForListing;
import de.njsm.stocks.client.business.entities.RecipesForListing;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.comparing;

public class RecipeTestData {

    private final BehaviorSubject<RecipesForListing> data;

    public RecipeTestData(RecipesForListing data) {
        this.data = BehaviorSubject.createDefault(data);
    }

    public static RecipesForListing generate() {
        var recipesByName = List.of(
                RecipeForListing.create(4, "Burger", 5, 4),
                RecipeForListing.create(2, "Pasta", 6, 6),
                RecipeForListing.create(1, "Pizza", 7, 0)
        );
        List<RecipeForListing> recipesByCookability = new ArrayList<>(recipesByName);
        recipesByCookability.sort(
                comparing(RecipeForListing::necessaryIngredientIndex)
                        .thenComparing(RecipeForListing::sufficientIngredientIndex)
        );

        return RecipesForListing.create(recipesByName, recipesByCookability);
    }

    public BehaviorSubject<RecipesForListing> getData() {
        return data;
    }
}
