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

package de.njsm.stocks.client.fragment.searchedfood;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.client.ui.R;

public class FoodAmountViewHolder extends RecyclerView.ViewHolder {

    private final TextView name;

    private final ImageView toBuy;

    private final TextView amount;

    public FoodAmountViewHolder(@NonNull View view) {
        super(view);
        name = view.findViewById(R.id.item_food_amount_name);
        toBuy = view.findViewById(R.id.item_food_amount_shopping_flag);
        amount = view.findViewById(R.id.item_food_amount_amout);
        view.setTag(this);
    }

    public void setName(String name) {
        this.name.setText(name);
    }

    public void setToBuy(boolean toBuy) {
        if (toBuy)
            this.toBuy.setVisibility(View.VISIBLE);
        else
            this.toBuy.setVisibility(View.GONE);
    }

    public void setAmount(String amount) {
        this.amount.setText(amount);
    }
}

