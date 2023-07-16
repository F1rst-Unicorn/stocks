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
import com.google.common.collect.Lists;
import de.njsm.stocks.client.business.entities.RecipeCookingFormDataProduct;
import de.njsm.stocks.client.presenter.UnitAmountRenderStrategy;
import de.njsm.stocks.client.ui.R;

import java.util.List;
import java.util.function.Function;

import static de.njsm.stocks.client.fragment.util.ListDiffer.byNestedId;
import static java.util.Collections.emptyList;

class RecipeProductAdapter extends RecyclerView.Adapter<RecipeProductViewHolder> {

    private List<RecipeCookingFormDataProduct> data = emptyList();

    private final UnitAmountRenderStrategy unitAmountRenderStrategy;

    RecipeProductAdapter() {
        unitAmountRenderStrategy = new UnitAmountRenderStrategy();
    }

    public void setData(List<RecipeCookingFormDataProduct> newList) {
        var oldList = data;
        DiffUtil.calculateDiff(byNestedId(oldList, newList, RecipeCookingFormDataProduct::id), true).dispatchUpdatesTo(this);
        this.data = Lists.newArrayList(newList);
    }

    List<RecipeCookingFormDataProduct> getData() {
        return data;
    }

    @NonNull
    @Override
    public RecipeProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe_item, parent, false);
        return new RecipeProductViewHolder(v, this::onAddButtonPressed, this::onRemoveButtonPressed);
    }

    private void onAddButtonPressed(RecipeProductViewHolder viewHolder) {
        onModifyButtonPressed(viewHolder, v -> v.producedAmount().increase());
    }

    private void onRemoveButtonPressed(RecipeProductViewHolder viewHolder) {
        onModifyButtonPressed(viewHolder, v -> v.producedAmount().decrease());
    }

    private void onModifyButtonPressed(RecipeProductViewHolder viewHolder, Function<RecipeCookingFormDataProduct, RecipeCookingFormDataProduct.Amount> modifier) {
        int position = viewHolder.getAbsoluteAdapterPosition();
        var item = data.remove(position);
        var increasedAmount = modifier.apply(item);
        data.add(position, RecipeCookingFormDataProduct.create(item.id(), item.name(), increasedAmount));
        notifyItemChanged(position);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeProductViewHolder holder, int position) {
        var item = data.get(position);
        holder.setFoodName(item.name());
        holder.setRecipeAmounts(unitAmountRenderStrategy.render(item.producedAmount()));
        holder.setAmounts(List.of(item.producedAmount()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
