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

package de.njsm.stocks.client.fragment.recipeadd;

import androidx.annotation.NonNull;
import de.njsm.stocks.client.business.entities.*;

import static de.njsm.stocks.client.business.ListSearcher.findFirst;

public class RecipeProductFoodAdapter extends RecipeFoodAdapter<RecipeProductToAdd> {

    public RecipeProductFoodAdapter(RecipeAddData data) {
        super(data);
    }

    public void add() {
        list.add(RecipeProductToAdd.create(1,
                data.availableFood().get(0),
                data.availableUnits().get(0)));
        notifyItemInserted(list.size() - 1);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeFoodViewHolder holder, int position) {
        RecipeProductToAdd data = list.get(position);
        holder.setAmount(data.amount());
        holder.setSelectedFood(findFirst(this.data.availableFood(), data.product()));
        holder.setSelectedUnit(findFirst(this.data.availableUnits(), data.unit()));
        holder.setCallback(this::onItemEdit);
    }

    public void onItemEdit(int position, int amount, int foodPosition, int unitPosition) {
        RecipeProductToAdd current = list.get(position);
        Id<Food> food = data.availableFood().get(foodPosition);
        Id<ScaledUnit> unit = data.availableUnits().get(unitPosition);
        if (amount != current.amount() || food.id() != current.product().id() ||
                unit.id() != current.unit().id()) {
            RecipeProductToAdd newData = RecipeProductToAdd.create(amount, food, unit);
            list.set(position, newData);
        }
    }
}
