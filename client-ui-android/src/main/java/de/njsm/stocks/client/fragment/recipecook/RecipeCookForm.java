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
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.IdImpl;
import de.njsm.stocks.client.business.entities.RecipeCookingFormDataIngredient;
import de.njsm.stocks.client.business.entities.RecipeCookingFormDataProduct;
import de.njsm.stocks.client.ui.R;

import java.util.List;
import java.util.function.Consumer;

class RecipeCookForm {

    private final RecipeIngredientAdapter ingredientAdapter;

    private final RecipeProductAdapter productAdapter;

    private final TextView ingredientHeadline;

    private final TextView productsHeadline;

    RecipeCookForm(View root, Consumer<IdImpl<Food>> toBuyCallback,
                   Consumer<IdImpl<Food>> showFoodCallback) {
        ingredientHeadline = root.findViewById(R.id.fragment_recipe_cook_label_ingredients);
        productsHeadline = root.findViewById(R.id.fragment_recipe_cook_label_products);
        RecyclerView ingredients = root.findViewById(R.id.fragment_recipe_cook_ingredients);
        ingredientAdapter = new RecipeIngredientAdapter(toBuyCallback, showFoodCallback);
        ingredients.setLayoutManager(new LinearLayoutManager(root.getContext()));
        ingredients.setAdapter(ingredientAdapter);

        RecyclerView products = root.findViewById(R.id.fragment_recipe_cook_products);
        productAdapter = new RecipeProductAdapter(showFoodCallback);
        products.setLayoutManager(new LinearLayoutManager(root.getContext()));
        products.setAdapter(productAdapter);

    }

    void setIngredients(List<RecipeCookingFormDataIngredient> ingredients) {
        ingredientAdapter.setData(ingredients);
        if (ingredients.isEmpty()) {
            ingredientHeadline.setVisibility(View.GONE);
        } else {
            ingredientHeadline.setVisibility(View.VISIBLE);
        }
    }

    void setProducts(List<RecipeCookingFormDataProduct> products) {
        productAdapter.setData(products);
        if (products.isEmpty()) {
            productsHeadline.setVisibility(View.GONE);
        } else {
            productsHeadline.setVisibility(View.VISIBLE);
        }
    }

    List<RecipeCookingFormDataIngredient> getCurrentIngredients() {
        return ingredientAdapter.getData();
    }

    List<RecipeCookingFormDataProduct> getCurrentProducts() {
        return productAdapter.getData();
    }
}
