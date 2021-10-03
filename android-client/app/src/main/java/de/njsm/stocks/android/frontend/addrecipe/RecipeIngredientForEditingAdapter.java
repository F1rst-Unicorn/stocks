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
import de.njsm.stocks.common.api.RecipeIngredientForDeletion;
import de.njsm.stocks.common.api.RecipeIngredientForEditing;
import de.njsm.stocks.common.api.RecipeIngredientForInsertion;
import de.njsm.stocks.common.api.SelfValidating;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RecipeIngredientForEditingAdapter extends ScaledFoodAdapter<RecipeIngredientForEditing, RecipeIngredientForEditing.Builder, RecipeIngredientForInsertion, RecipeIngredientForInsertion.Builder> {

    private final List<RecipeIngredientForEditing.Builder> ingredientsToEdit;

    private final List<RecipeIngredientForDeletion> ingredientsToDelete;

    private final int recipeId;

    public RecipeIngredientForEditingAdapter(LiveData<List<ScaledUnitView>> units, LiveData<List<Food>> food, List<RecipeIngredientForEditing.Builder> ingredientsToEdit, int recipeId) {
        super(units, food);
        this.ingredientsToEdit = ingredientsToEdit;
        this.recipeId = recipeId;
        this.ingredientsToDelete = new ArrayList<>();
    }

    private class ViewHolder extends ScaledFoodAdapter<RecipeIngredientForEditing, RecipeIngredientForEditing.Builder, RecipeIngredientForInsertion, RecipeIngredientForInsertion.Builder>.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        void updateAmount(int position, int amount) {
            if (isEditedItem(position))
                ingredientsToEdit.get(position).amount(amount);
            else
                entities.get(transformIndexForInsertedItem(position)).amount(amount);
        }

        @Override
        void updateUnit(int position, int unit) {
            if (isEditedItem(position))
                ingredientsToEdit.get(position).unit(unit);
            else
                entities.get(transformIndexForInsertedItem(position)).unit(unit);
        }

        @Override
        void updateFood(int position, int food) {
            if (isEditedItem(position))
                ingredientsToEdit.get(position).ingredient(food);
            else
                entities.get(transformIndexForInsertedItem(position)).ingredient(food);
        }
    }

    @Override
    public void removeItem(int position) {
        if (isEditedItem(position)) {
            RecipeIngredientForEditing deletedItem = ingredientsToEdit.get(position).build();
            ingredientsToEdit.remove(position);

            ingredientsToDelete.add(RecipeIngredientForDeletion.builder()
                    .id(deletedItem.id())
                    .version(deletedItem.version())
                    .build());
        } else {
            entities.remove(transformIndexForInsertedItem(position));
        }
        notifyItemRemoved(position);
    }

    @Override
    ScaledFoodAdapter<RecipeIngredientForEditing, RecipeIngredientForEditing.Builder, RecipeIngredientForInsertion, RecipeIngredientForInsertion.Builder>.ViewHolder
    newViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    RecipeIngredientForInsertion.Builder
    newBuilder(List<ScaledUnitView> units, List<Food> food) {
        return RecipeIngredientForInsertion.builder()
                .amount(1)
                .unit(units.get(0).getId())
                .ingredient(food.get(0).getId());
    }

    @Override
    public void onBindViewHolder(@NonNull ScaledFoodAdapter<RecipeIngredientForEditing, RecipeIngredientForEditing.Builder, RecipeIngredientForInsertion, RecipeIngredientForInsertion.Builder>.ViewHolder holder,
                                 int position) {
        if (isEditedItem(position)) {
            bindEditingItem(holder, position);
        } else {
            bindInsertedItem(holder, transformIndexForInsertedItem(position));
        }
    }

    private void bindInsertedItem(ScaledFoodAdapter<RecipeIngredientForEditing, RecipeIngredientForEditing.Builder, RecipeIngredientForInsertion, RecipeIngredientForInsertion.Builder>.ViewHolder holder,
                                  int position) {
        RecipeIngredientForInsertion currentState = entities.get(position).build();
        holder.setAmount(currentState.amount());
        holder.setUnit(currentState.unit());
        holder.setFood(currentState.ingredient());
    }

    private void bindEditingItem(ScaledFoodAdapter<RecipeIngredientForEditing, RecipeIngredientForEditing.Builder, RecipeIngredientForInsertion, RecipeIngredientForInsertion.Builder>.ViewHolder holder,
                                 int position) {
        RecipeIngredientForEditing currentState = ingredientsToEdit.get(position).build();
        holder.setAmount(currentState.amount());
        holder.setUnit(currentState.unit());
        holder.setFood(currentState.ingredient());
    }

    @Override
    public int getItemCount() {
        return entities.size() + ingredientsToEdit.size();
    }

    public Set<RecipeIngredientForInsertion> getIngredientsToInsert() {
        minimiseEntities();
        return new HashSet<>(getScaledFood());
    }

    public Set<RecipeIngredientForEditing> getIngredientsToEdit() {
        minimiseEntities();
        return ingredientsToEdit.stream().map(SelfValidating.Builder::build).collect(Collectors.toSet());
    }

    public Set<RecipeIngredientForDeletion> getIngredientsToDelete() {
        minimiseEntities();
        return new HashSet<>(ingredientsToDelete);
    }

    private int transformIndexForInsertedItem(int position) {
        return position - ingredientsToEdit.size();
    }

    private boolean isEditedItem(int position) {
        return position < ingredientsToEdit.size();
    }

    private void minimiseEntities() {
        while (!(entities.isEmpty() || ingredientsToDelete.isEmpty())) {
            RecipeIngredientForInsertion inserted = entities.get(entities.size() - 1).build();
            entities.remove(entities.size() - 1);
            RecipeIngredientForDeletion deleted = ingredientsToDelete.get(ingredientsToDelete.size() - 1);
            ingredientsToDelete.remove(ingredientsToDelete.size() - 1);

            ingredientsToEdit.add(RecipeIngredientForEditing.builder()
                    .id(deleted.id())
                    .version(deleted.version())
                    .recipe(recipeId)
                    .amount(inserted.amount())
                    .ingredient(inserted.ingredient())
                    .unit(inserted.unit()));
        }
    }
}
