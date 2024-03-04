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

package de.njsm.stocks.client.fragment.recipeedit;

import androidx.annotation.NonNull;
import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.databind.RecipeFoodAdapter;
import de.njsm.stocks.client.databind.RecipeFoodViewHolder;

public class RecipeProductEditFoodAdapter extends RecipeFoodAdapter<RecipeProductEditFormData> {

    public RecipeProductEditFoodAdapter(RecipeEditFormData data) {
        super(data.availableFood(), data.availableUnits());
        add(data.products());
    }

    public void add() {
        list.add(RecipeProductEditFormData.create(-1,
                1,
                0,
                unitsForSelection.get(0),
                0,
                foodForSelection.get(0)));
        notifyItemInserted(list.size() - 1);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeFoodViewHolder holder, int position) {
        RecipeProductEditFormData data = list.get(position);
        holder.setAmount(data.amount());
        holder.setSelectedFood(data.productListItemPosition());
        holder.setSelectedUnit(data.unitListItemPosition());
        holder.setCallback(this::onItemEdit);
    }

    public void onItemEdit(int position, int amount, int foodPosition, int unitPosition) {
        RecipeProductEditFormData current = list.get(position);
        Id<Food> food = foodForSelection.get(foodPosition);
        Id<ScaledUnit> unit = unitsForSelection.get(unitPosition);
        if (amount != current.amount() || food.id() != current.product().id() ||
                unit.id() != current.unit().id()) {
            RecipeProductEditFormData newData = RecipeProductEditFormData.create(
                    current.id(),
                    amount,
                    unitPosition,
                    unit,
                    foodPosition,
                    food);
            list.set(position, newData);
        }
    }
}
