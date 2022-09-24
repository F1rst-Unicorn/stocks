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

package de.njsm.stocks.client.databind;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.client.business.entities.Entity;
import de.njsm.stocks.client.business.entities.Identifiable;
import de.njsm.stocks.client.fragment.view.FoodOutlineViewHolder;
import de.njsm.stocks.client.presenter.UnitAmountRenderStrategy;
import de.njsm.stocks.client.ui.R;

import java.util.List;

import static de.njsm.stocks.client.fragment.util.ListDiffer.byId;

public abstract class AbstractFoodAdapter<E extends Entity<E>, T extends Identifiable<E>> extends RecyclerView.Adapter<FoodOutlineViewHolder> {

    private List<T> foods;

    private final View.OnClickListener onClickListener;

    private final View.OnLongClickListener onLongClickListener;

    protected final UnitAmountRenderStrategy unitAmountRenderStrategy;

    protected AbstractFoodAdapter(View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener) {
        this.onClickListener = onClickListener;
        this.onLongClickListener = onLongClickListener;
        unitAmountRenderStrategy = new UnitAmountRenderStrategy();
    }

    public void setData(List<T> newList) {
        List<T> oldList = foods;
        foods = newList;
        DiffUtil.calculateDiff(byId(oldList, newList), true).dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public FoodOutlineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food_outline, parent, false);
        v.setOnClickListener(onClickListener);
        v.setOnLongClickListener(onLongClickListener);
        return new FoodOutlineViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodOutlineViewHolder holder, int position) {
        T item = foods.get(position);
        onBindViewHolder(holder, item);
    }

    protected abstract void onBindViewHolder(FoodOutlineViewHolder holder, T item);

    @Override
    public int getItemCount() {
        if (foods == null) {
            return 0;
        } else {
            return foods.size();
        }
    }
}
