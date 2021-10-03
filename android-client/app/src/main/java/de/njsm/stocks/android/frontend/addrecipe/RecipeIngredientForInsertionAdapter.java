/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2021  The stocks developers
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
 */

package de.njsm.stocks.android.frontend.addrecipe;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.db.views.ScaledUnitView;
import de.njsm.stocks.common.api.RecipeIngredientForInsertion;

import java.util.List;

public class RecipeIngredientForInsertionAdapter extends ScaledFoodAdapter<RecipeIngredientForInsertion, RecipeIngredientForInsertion.Builder, RecipeIngredientForInsertion, RecipeIngredientForInsertion.Builder> {

    public RecipeIngredientForInsertionAdapter(LiveData<List<ScaledUnitView>> units, LiveData<List<Food>> food) {
        super(units, food);
    }

    private class ViewHolder extends ScaledFoodAdapter<RecipeIngredientForInsertion, RecipeIngredientForInsertion.Builder, RecipeIngredientForInsertion, RecipeIngredientForInsertion.Builder>.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        void updateAmount(int position, int amount) {
            entities.get(position).amount(amount);
        }

        @Override
        void updateUnit(int position, int unit) {
            entities.get(position).unit(unit);
        }

        @Override
        void updateFood(int position, int food) {
            entities.get(position).ingredient(food);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ScaledFoodAdapter<RecipeIngredientForInsertion, RecipeIngredientForInsertion.Builder, RecipeIngredientForInsertion, RecipeIngredientForInsertion.Builder>.ViewHolder holder,
                                 int position) {
        RecipeIngredientForInsertion currentState = entities.get(position).build();
        holder.setAmount(currentState.amount());
        holder.setUnit(currentState.unit());
        holder.setFood(currentState.ingredient());
    }

    @Override
    ScaledFoodAdapter<RecipeIngredientForInsertion, RecipeIngredientForInsertion.Builder, RecipeIngredientForInsertion, RecipeIngredientForInsertion.Builder>.ViewHolder
    newViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    RecipeIngredientForInsertion.Builder newBuilder(List<ScaledUnitView> units, List<Food> food) {
        return RecipeIngredientForInsertion.builder()
                .amount(1)
                .unit(units.get(0).getId())
                .ingredient(food.get(0).getId());
    }
}
