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

package de.njsm.stocks.client.fragment.fooditemlist;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.client.ui.R;

class FoodItemViewHolder extends RecyclerView.ViewHolder {

    private final TextView eatByField;

    private final TextView locationField;

    private final TextView buyerField;

    private final TextView registererField;

    private final TextView amountField;

    private final ImageView eatByIcon;

    FoodItemViewHolder(View root) {
        super(root);
        eatByField = root.findViewById(R.id.item_food_item_date);
        locationField = root.findViewById(R.id.item_food_item_location);
        buyerField = root.findViewById(R.id.item_food_item_user);
        registererField = root.findViewById(R.id.item_food_item_device);
        amountField = root.findViewById(R.id.item_food_item_amount);
        eatByIcon = root.findViewById(R.id.item_food_item_icon);
        itemView.setTag(this);
    }

    public void setEatBy(CharSequence date) {
        eatByField.setText(date);
    }

    public void setLocation(String location) {
        locationField.setText(location);
    }

    public void setBuyer(String buyer) {
        buyerField.setText(buyer);
    }

    public void setRegisterer(String registerer) {
        registererField.setText(registerer);
    }

    public void setAmount(String amount) {
        amountField.setText(amount);
    }

    public void setWarningLevel(Drawable level) {
        eatByIcon.setImageDrawable(level);
    }
}
