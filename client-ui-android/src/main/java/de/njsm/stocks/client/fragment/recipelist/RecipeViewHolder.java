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

package de.njsm.stocks.client.fragment.recipelist;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.client.ui.R;

public class RecipeViewHolder extends RecyclerView.ViewHolder {

    private final TextView name;

    private final TextView necessaryIngredientIndex;

    private final TextView sufficientIngredientIndex;

    public RecipeViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.item_recipe_name);
        necessaryIngredientIndex = itemView.findViewById(R.id.item_recipe_necessary_ingredient_index);
        sufficientIngredientIndex = itemView.findViewById(R.id.item_recipe_sufficient_ingredient_index);
        itemView.setTag(this);
    }

    public void setName(CharSequence name) {
        this.name.setText(name);
    }

    public void setNecessaryIngredientIndex(CharSequence name) {
        this.necessaryIngredientIndex.setText(name);
    }

    public void setSufficientIngredientIndex(CharSequence name) {
        this.sufficientIngredientIndex.setText(name);
    }
}
