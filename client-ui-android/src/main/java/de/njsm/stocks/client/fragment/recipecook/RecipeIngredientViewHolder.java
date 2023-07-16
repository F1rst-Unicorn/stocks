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

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.client.business.entities.RecipeCookingFormDataIngredient;
import de.njsm.stocks.client.ui.R;
import org.jetbrains.annotations.NotNull;

import java.util.List;

class RecipeIngredientViewHolder extends RecyclerView.ViewHolder {

    private final TextView foodName;

    private final TextView recipeAmounts;

    private final ImageView toBuy;

    private final PresentAmountAdapter adapter;

    RecipeIngredientViewHolder(@NonNull @NotNull View itemView, ButtonCallback addCallback, ButtonCallback removeCallback) {
        super(itemView);
        foodName = itemView.findViewById(R.id.item_recipe_item_food_name);
        recipeAmounts = itemView.findViewById(R.id.item_recipe_item_scaled_unit);
        toBuy = itemView.findViewById(R.id.item_recipe_item_shopping_cart);
        RecyclerView amounts = itemView.findViewById(R.id.item_recipe_item_amounts);

        adapter = new PresentAmountAdapter(v -> addCallback.onClicked(this, v), v -> removeCallback.onClicked(this, v));
        amounts.setAdapter(adapter);
        amounts.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
    }

    void setFoodName(String foodName) {
        this.foodName.setText(foodName);
    }

    void setRecipeAmounts(String amounts) {
        this.recipeAmounts.setText(amounts);
    }

    void setAmounts(List<RecipeCookingFormDataIngredient.PresentAmount> amounts) {
        this.adapter.setData(amounts);
    }

    void setToBuy(boolean toBuy) {
        Drawable drawable;
        if (toBuy) {
            drawable = ContextCompat.getDrawable(this.toBuy.getContext(), R.drawable.baseline_remove_shopping_cart_black_24);
        } else {
            drawable = ContextCompat.getDrawable(this.toBuy.getContext(), R.drawable.baseline_add_shopping_cart_black_24);
        }
        this.toBuy.setImageDrawable(drawable);
    }

    public void setToBuyCallback(View.OnClickListener callback) {
        this.toBuy.setOnClickListener(callback);
    }

    interface ButtonCallback {
        void onClicked(RecipeIngredientViewHolder viewHolder, ItemIngredientAmountIncrementorViewHolder amountViewHolder);
    }
}
