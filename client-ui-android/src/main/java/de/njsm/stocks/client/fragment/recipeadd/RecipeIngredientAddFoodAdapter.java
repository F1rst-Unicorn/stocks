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
import de.njsm.stocks.client.databind.RecipeFoodAdapter;
import de.njsm.stocks.client.databind.RecipeFoodViewHolder;

import static de.njsm.stocks.client.business.ListSearcher.findFirstBy;

public class RecipeIngredientAddFoodAdapter extends RecipeFoodAdapter<RecipeIngredientToAdd> {

    public RecipeIngredientAddFoodAdapter(RecipeAddData data) {
        super(data.availableFood(), data.availableUnits());
    }

    public void add() {
        list.add(RecipeIngredientToAdd.create(1,
                foodForSelection.get(0),
                unitsForSelection.get(0)));
        notifyItemInserted(list.size() - 1);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeFoodViewHolder holder, int position) {
        RecipeIngredientToAdd data = list.get(position);
        holder.setAmount(data.amount());
        holder.setSelectedFood(findFirstBy(foodForSelection, data.ingredient(), f -> f));
        holder.setSelectedUnit(findFirstBy(unitsForSelection, data.unit(), u -> u));
        holder.setCallback(this::onItemEdit);
    }

    public void onItemEdit(int position, int amount, int foodPosition, int unitPosition) {
        RecipeIngredientToAdd current = list.get(position);
        Id<Food> food = foodForSelection.get(foodPosition);
        Id<ScaledUnit> unit = unitsForSelection.get(unitPosition);
        if (amount != current.amount() || food.id() != current.ingredient().id() ||
                unit.id() != current.unit().id()) {
            RecipeIngredientToAdd newData = RecipeIngredientToAdd.create(amount, food, unit);
            list.set(position, newData);
        }
    }
}
