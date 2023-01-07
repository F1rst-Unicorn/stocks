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

package de.njsm.stocks.client.fragment.emptyfood;

import android.view.View;
import de.njsm.stocks.client.business.entities.EmptyFood;
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.databind.AbstractFoodAdapter;
import de.njsm.stocks.client.fragment.view.FoodOutlineViewHolder;

class EmptyFoodAdapter extends AbstractFoodAdapter<Food, EmptyFood> {

    EmptyFoodAdapter(View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener) {
        super(onClickListener, onLongClickListener);
    }

    @Override
    protected void onBindViewHolder(FoodOutlineViewHolder holder, EmptyFood item) {
        holder.hideExpirationDate();
        holder.setName(item.name());
        holder.showToBuy(item.toBuy());
        holder.setAmount(unitAmountRenderStrategy.render(item.storedAmount()));
    }
}
