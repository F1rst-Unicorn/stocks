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

import java.util.List;

import static de.njsm.stocks.client.fragment.util.ListDiffer.byNestedId;

public class PresentAmountAdapter extends RecyclerView.Adapter<ItemIngredientAmountIncrementorViewHolder> {

    private List<RecipeCookingFormDataIngredient.PresentAmount> data;

    private final ItemIngredientAmountIncrementorViewHolder.ButtonCallback addCallback;

    private final ItemIngredientAmountIncrementorViewHolder.ButtonCallback removeCallback;

    private final UnitAmountRenderStrategy unitAmountRenderStrategy;

    public PresentAmountAdapter(ItemIngredientAmountIncrementorViewHolder.ButtonCallback addCallback,
                                ItemIngredientAmountIncrementorViewHolder.ButtonCallback removeCallback) {
        this.addCallback = addCallback;
        this.removeCallback = removeCallback;
        unitAmountRenderStrategy = new UnitAmountRenderStrategy();
    }

    public void setData(List<RecipeCookingFormDataIngredient.PresentAmount> newList) {
        var oldList = data;
        DiffUtil.calculateDiff(byNestedId(oldList, newList, RecipeCookingFormDataIngredient.PresentAmount::scaledUnit), true).dispatchUpdatesTo(this);
        this.data = newList;
    }

    @NonNull
    @Override
    public ItemIngredientAmountIncrementorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_amount_incrementor, parent, false);
        return new ItemIngredientAmountIncrementorViewHolder(v, addCallback, removeCallback);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemIngredientAmountIncrementorViewHolder holder, int position) {
        var item = data.get(position);
        holder.setMaxAmount(unitAmountRenderStrategy.render(item.scalePresent()));
        holder.setCurrentAmount(unitAmountRenderStrategy.render(item.scaleSelected()));
        holder.setUnitAbbreviation(unitAmountRenderStrategy.renderUnitSymbol(item.amount()));
    }

    @Override
    public int getItemCount() {
        if (data == null) {
            return 0;
        } else {
            return data.size();
        }
    }
}
