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

package de.njsm.stocks.client.business.entities;

import com.google.auto.value.AutoValue;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@AutoValue
public abstract class RecipeCookingFormData implements Serializable {

    private static final long serialVersionUID = 1;

    public abstract IdImpl<Recipe> id();

    public abstract String name();

    public abstract List<RecipeCookingFormDataIngredient> ingredients();

    public abstract List<RecipeCookingFormDataProduct> products();

    public static RecipeCookingFormData create(IdImpl<Recipe> id, String name, List<RecipeCookingFormDataIngredient> ingredients, List<RecipeCookingFormDataProduct> products) {
        return new AutoValue_RecipeCookingFormData(id, name, ingredients, products);
    }

    /**
     * Merge the selected counts from the parameters into the bounding present
     * counts of this.
     */
    public RecipeCookingFormData mergeFrom(List<RecipeCookingFormDataIngredient> ingredients, List<RecipeCookingFormDataProduct> products) {
        var ingredientsByFood = ingredients.stream()
                .collect(toMap(RecipeCookingFormDataIngredient::id, v -> v));
        var productsByFood = products.stream()
                .collect(toMap(RecipeCookingFormDataProduct::id, v -> v));

        return create(id(), name(),
                ingredients().stream().map(v -> v.mergeFrom(ingredientsByFood.get(v.id()))).collect(Collectors.toList()),
                products().stream().map(v -> v.mergeFrom(productsByFood.get(v.id()))).collect(Collectors.toList()));
    }
}
