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

package de.njsm.stocks.client.fragment.recipecook;

import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.client.business.entities.RecipeCookingFormDataIngredient;
import de.njsm.stocks.client.business.entities.RecipeCookingFormDataProduct;
import de.njsm.stocks.client.ui.R;

import java.util.List;

public class RecipeCookForm {

    private final RecipeIngredientAdapter ingredientAdapter;

    private final RecipeProductAdapter productAdapter;

    public RecipeCookForm(View root) {
        RecyclerView ingredients = root.findViewById(R.id.fragment_recipe_cook_ingredients);
        ingredientAdapter = new RecipeIngredientAdapter();
        ingredients.setLayoutManager(new LinearLayoutManager(root.getContext()));
        ingredients.setAdapter(ingredientAdapter);

        RecyclerView products = root.findViewById(R.id.fragment_recipe_cook_products);
        productAdapter = new RecipeProductAdapter();
        products.setLayoutManager(new LinearLayoutManager(root.getContext()));
        products.setAdapter(productAdapter);

    }

    public void setIngredients(List<RecipeCookingFormDataIngredient> ingredients) {
        ingredientAdapter.setData(ingredients);
    }

    public void setProducts(List<RecipeCookingFormDataProduct> products) {
        productAdapter.setData(products);
    }
}
