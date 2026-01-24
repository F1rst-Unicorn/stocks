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
import de.njsm.stocks.client.business.ListSearcher;
import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.databind.RecipeFoodAdapter;
import de.njsm.stocks.client.databind.RecipeFoodDataChanged;
import de.njsm.stocks.client.databind.RecipeFoodViewHolder;

import java.util.List;

import static de.njsm.stocks.client.business.RecipeFoodEditMatcher.FRESHLY_CREATED_ENTITY_ID;

public class RecipeProductEditFoodAdapter
        extends RecipeFoodAdapter<RecipeProductEditFormData>
        implements RecipeFoodDataChanged {

    public RecipeProductEditFoodAdapter(RecipeEditFormData data) {
        super(data.availableFood(), data.availableUnits());
        add(data.products());
    }

    public RecipeProductEditFoodAdapter(RecipeEditFormData data, List<RecipeProductEditFormData> list) {
        super(data.availableFood(), data.availableUnits());
        add(list);
    }

    public void add() {
        list.add(RecipeProductEditFormData.create(FRESHLY_CREATED_ENTITY_ID,
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
        holder.setCallback(this);
    }

    @Override
    public void onAmountChanged(int position, int amount) {
        RecipeProductEditFormData current = list.get(position);
        if (amount != current.amount()) {
            RecipeProductEditFormData newData = RecipeProductEditFormData.create(
                    current.id(),
                    amount,
                    current.unitListItemPosition(),
                    current.unit(),
                    current.productListItemPosition(),
                    current.product());
            list.set(position, newData);
        }
    }

    @Override
    public void onFoodChanged(int position, Id<Food> food) {
        RecipeProductEditFormData current = list.get(position);
        if (food.id() != current.product().id()) {
            int newFoodPosition = ListSearcher.findFirst(
                    foodForSelection,
                    food
            );
            RecipeProductEditFormData newData = RecipeProductEditFormData.create(
                    current.id(),
                    current.amount(),
                    current.unitListItemPosition(),
                    current.unit(),
                    newFoodPosition,
                    food);
            list.set(position, newData);
        }
    }

    @Override
    public void onUnitChanged(int position, Id<ScaledUnit> scaledUnit, int itemPosition) {
        RecipeProductEditFormData current = list.get(position);
        if (scaledUnit.id() != current.unit().id()) {
            RecipeProductEditFormData newData = RecipeProductEditFormData.create(
                    current.id(),
                    current.amount(),
                    itemPosition,
                    scaledUnit,
                    current.productListItemPosition(),
                    current.product());
            list.set(position, newData);
        }
    }
}
