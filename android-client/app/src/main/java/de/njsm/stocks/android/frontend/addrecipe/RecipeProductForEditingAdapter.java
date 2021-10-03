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
import de.njsm.stocks.common.api.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RecipeProductForEditingAdapter extends ScaledFoodAdapter<RecipeProductForEditing, RecipeProductForEditing.Builder, RecipeProductForInsertion, RecipeProductForInsertion.Builder> {

    private final List<RecipeProductForEditing.Builder> productsToEdit;

    private final List<RecipeProductForDeletion> productsToDelete;

    private final int recipeId;

    public RecipeProductForEditingAdapter(LiveData<List<ScaledUnitView>> units, LiveData<List<Food>> food, List<RecipeProductForEditing.Builder> productsToEdit, int recipeId) {
        super(units, food);
        this.productsToEdit = productsToEdit;
        this.recipeId = recipeId;
        this.productsToDelete = new ArrayList<>();
    }

    private class ViewHolder extends ScaledFoodAdapter<RecipeProductForEditing, RecipeProductForEditing.Builder, RecipeProductForInsertion, RecipeProductForInsertion.Builder>.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        void updateAmount(int position, int amount) {
            if (isEditedItem(position))
                productsToEdit.get(position).amount(amount);
            else
                entities.get(transformIndexForInsertedItem(position)).amount(amount);
        }

        @Override
        void updateUnit(int position, int unit) {
            if (isEditedItem(position))
                productsToEdit.get(position).unit(unit);
            else
                entities.get(transformIndexForInsertedItem(position)).unit(unit);
        }

        @Override
        void updateFood(int position, int food) {
            if (isEditedItem(position))
                productsToEdit.get(position).product(food);
            else
                entities.get(transformIndexForInsertedItem(position)).product(food);
        }
    }

    @Override
    public void removeItem(int position) {
        if (isEditedItem(position)) {
            RecipeProductForEditing deletedItem = productsToEdit.get(position).build();
            productsToEdit.remove(position);

            productsToDelete.add(RecipeProductForDeletion.builder()
                    .id(deletedItem.id())
                    .version(deletedItem.version())
                    .build());
        } else {
            entities.remove(transformIndexForInsertedItem(position));
        }
        notifyItemRemoved(position);
    }

    @Override
    ScaledFoodAdapter<RecipeProductForEditing, RecipeProductForEditing.Builder, RecipeProductForInsertion, RecipeProductForInsertion.Builder>.ViewHolder
    newViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    RecipeProductForInsertion.Builder
    newBuilder(List<ScaledUnitView> units, List<Food> food) {
        return RecipeProductForInsertion.builder()
                .amount(1)
                .unit(units.get(0).getId())
                .product(food.get(0).getId());
    }

    @Override
    public void onBindViewHolder(@NonNull ScaledFoodAdapter<RecipeProductForEditing, RecipeProductForEditing.Builder, RecipeProductForInsertion, RecipeProductForInsertion.Builder>.ViewHolder holder,
                                 int position) {
        if (isEditedItem(position)) {
            bindEditingItem(holder, position);
        } else {
            bindInsertedItem(holder, transformIndexForInsertedItem(position));
        }
    }

    private void bindInsertedItem(ScaledFoodAdapter<RecipeProductForEditing, RecipeProductForEditing.Builder, RecipeProductForInsertion, RecipeProductForInsertion.Builder>.ViewHolder holder,
                                  int position) {
        RecipeProductForInsertion currentState = entities.get(position).build();
        holder.setAmount(currentState.amount());
        holder.setUnit(currentState.unit());
        holder.setFood(currentState.product());
    }

    private void bindEditingItem(ScaledFoodAdapter<RecipeProductForEditing, RecipeProductForEditing.Builder, RecipeProductForInsertion, RecipeProductForInsertion.Builder>.ViewHolder holder,
                                 int position) {
        RecipeProductForEditing currentState = productsToEdit.get(position).build();
        holder.setAmount(currentState.amount());
        holder.setUnit(currentState.unit());
        holder.setFood(currentState.product());
    }

    @Override
    public int getItemCount() {
        return entities.size() + productsToEdit.size();
    }

    public Set<RecipeProductForInsertion> getProductsToInsert() {
        minimiseEntities();
        return new HashSet<>(getScaledFood());
    }

    public Set<RecipeProductForEditing> getProductsToEdit() {
        minimiseEntities();
        return productsToEdit.stream().map(SelfValidating.Builder::build).collect(Collectors.toSet());
    }

    public Set<RecipeProductForDeletion> getProductsToDelete() {
        minimiseEntities();
        return new HashSet<>(productsToDelete);
    }

    private int transformIndexForInsertedItem(int position) {
        return position - productsToEdit.size();
    }

    private boolean isEditedItem(int position) {
        return position < productsToEdit.size();
    }

    private void minimiseEntities() {
        while (!(entities.isEmpty() || productsToDelete.isEmpty())) {
            RecipeProductForInsertion inserted = entities.get(entities.size() - 1).build();
            entities.remove(entities.size() - 1);
            RecipeProductForDeletion deleted = productsToDelete.get(productsToDelete.size() - 1);
            productsToDelete.remove(productsToDelete.size() - 1);

            productsToEdit.add(RecipeProductForEditing.builder()
                    .id(deleted.id())
                    .version(deleted.version())
                    .recipe(recipeId)
                    .amount(inserted.amount())
                    .product(inserted.product())
                    .unit(inserted.unit()));
        }
    }
}
