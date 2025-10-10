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
import de.njsm.stocks.client.databind.RecipeFoodDataChanged;
import de.njsm.stocks.client.databind.RecipeFoodViewHolder;

import static de.njsm.stocks.client.business.ListSearcher.searchFirst;

public class RecipeProductFoodAddAdapter
        extends RecipeFoodAdapter<RecipeProductToAdd>
        implements RecipeFoodDataChanged {

    public RecipeProductFoodAddAdapter(RecipeAddData data) {
        super(data.availableFood(), data.availableUnits());
    }

    public void add() {
        list.add(RecipeProductToAdd.create(1,
                foodForSelection.get(0),
                unitsForSelection.get(0)));
        notifyItemInserted(list.size() - 1);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeFoodViewHolder holder, int position) {
        RecipeProductToAdd data = list.get(position);
        holder.setAmount(data.amount());
        searchFirst(this.foodForSelection, data.product()).ifPresent(holder::setSelectedFood);
        searchFirst(this.unitsForSelection, data.unit()).ifPresent(holder::setSelectedUnit);
        holder.setCallback(this);
    }

    @Override
    public void onAmountChanged(int position, int amount) {
        RecipeProductToAdd current = list.get(position);
        if (amount != current.amount()) {
            RecipeProductToAdd newData = RecipeProductToAdd.create(
                    amount,
                    current.product(),
                    current.unit());
            list.set(position, newData);
        }
    }

    @Override
    public void onFoodChanged(int position, Id<Food> food) {
        RecipeProductToAdd current = list.get(position);
        if (food.id() != current.product().id()) {
            RecipeProductToAdd newData = RecipeProductToAdd.create(
                    current.amount(),
                    food,
                    current.unit());
            list.set(position, newData);
        }
    }

    @Override
    public void onUnitChanged(int position, Id<ScaledUnit> scaledUnit, int itemPosition) {
        RecipeProductToAdd current = list.get(position);
        if (scaledUnit.id() != current.unit().id()) {
            RecipeProductToAdd newData = RecipeProductToAdd.create(
                    current.amount(),
                    current.product(),
                    scaledUnit);
            list.set(position, newData);
        }
    }
}
