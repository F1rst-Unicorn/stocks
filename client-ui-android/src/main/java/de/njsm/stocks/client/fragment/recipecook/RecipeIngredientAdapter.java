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

package de.njsm.stocks.client.fragment.recipecook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.client.business.entities.RecipeCookingFormDataIngredient;
import de.njsm.stocks.client.presenter.UnitAmountRenderStrategy;
import de.njsm.stocks.client.ui.R;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.google.common.collect.Lists.newArrayList;
import static de.njsm.stocks.client.fragment.util.ListDiffer.byNestedId;
import static java.util.Collections.emptyList;

class RecipeIngredientAdapter extends RecyclerView.Adapter<RecipeIngredientViewHolder> {

    private List<RecipeCookingFormDataIngredient> data = emptyList();

    private final UnitAmountRenderStrategy unitAmountRenderStrategy;

    RecipeIngredientAdapter() {
        unitAmountRenderStrategy = new UnitAmountRenderStrategy();
    }

    public void setData(List<RecipeCookingFormDataIngredient> newList) {
        var oldList = data;
        DiffUtil.calculateDiff(byNestedId(oldList, newList, RecipeCookingFormDataIngredient::id), true).dispatchUpdatesTo(this);
        this.data = new ArrayList<>(newList);
    }

    List<RecipeCookingFormDataIngredient> getData() {
        return data;
    }

    @NonNull
    @Override
    public RecipeIngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe_item, parent, false);
        return new RecipeIngredientViewHolder(v, this::onAddButtonPressed, this::onRemoveButtonPressed);
    }

    private void onAddButtonPressed(RecipeIngredientViewHolder viewHolder, ItemIngredientAmountIncrementorViewHolder amountViewHolder) {
        onModifyButtonPressed(viewHolder, amountViewHolder, RecipeCookingFormDataIngredient.PresentAmount::increase);
    }

    private void onRemoveButtonPressed(RecipeIngredientViewHolder viewHolder, ItemIngredientAmountIncrementorViewHolder amountViewHolder) {
        onModifyButtonPressed(viewHolder, amountViewHolder, RecipeCookingFormDataIngredient.PresentAmount::decrease);
    }

    private void onModifyButtonPressed(RecipeIngredientViewHolder viewHolder,
                                       ItemIngredientAmountIncrementorViewHolder amountViewHolder,
                                       Function<RecipeCookingFormDataIngredient.PresentAmount, RecipeCookingFormDataIngredient.PresentAmount> modifier) {
        int position = viewHolder.getAbsoluteAdapterPosition();
        int amountPosition = amountViewHolder.getAbsoluteAdapterPosition();
        var item = data.remove(position);
        var newAmounts = newArrayList(item.presentAmount());
        var increasedAmount = modifier.apply(newAmounts.remove(amountPosition));
        newAmounts.add(amountPosition, increasedAmount);
        data.add(position, RecipeCookingFormDataIngredient.create(item.id(), item.name(), item.toBuy(), item.requiredAmount(), newAmounts));
        notifyItemChanged(position);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeIngredientViewHolder holder, int position) {
        var item = data.get(position);
        holder.setFoodName(item.name());
        holder.setRecipeAmounts(unitAmountRenderStrategy.render(item.requiredAmount()));
        holder.setToBuy(item.toBuy());
        holder.setAmounts(item.presentAmount());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
