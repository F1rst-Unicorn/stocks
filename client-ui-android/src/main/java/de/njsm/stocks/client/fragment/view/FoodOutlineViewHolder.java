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

package de.njsm.stocks.client.fragment.view;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.client.ui.R;

public class FoodOutlineViewHolder extends RecyclerView.ViewHolder {

    private final TextView name;

    private final ImageView shoppingCart;

    private final TextView amount;

    private final TextView nextEatByDate;

    private final ImageView nextEatByIcon;

    public FoodOutlineViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.item_food_outline_name);
        shoppingCart = itemView.findViewById(R.id.item_food_outline_shopping_flag);
        amount = itemView.findViewById(R.id.item_food_outline_count);
        nextEatByDate = itemView.findViewById(R.id.item_food_outline_date);
        nextEatByIcon = itemView.findViewById(R.id.item_food_outline_icon);
        itemView.setTag(this);
    }

    public void setName(CharSequence name) {
        this.name.setText(name);
    }

    public void showToBuy(boolean show) {
        shoppingCart.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void setAmount(String amount) {
        this.amount.setText(amount);
    }

    public void hideExpirationDate() {
        nextEatByIcon.setVisibility(View.GONE);
        nextEatByDate.setVisibility(View.GONE);
    }

    public void setExpirationDate(CharSequence date) {
        nextEatByDate.setText(date);
    }

    public void setExpirationWarningLevel(Drawable level) {
        nextEatByIcon.setImageDrawable(level);
    }
}
