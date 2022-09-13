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

package de.njsm.stocks.client.fragment.emptyfood;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.client.business.entities.EmptyFood;
import de.njsm.stocks.client.fragment.view.FoodOutlineViewHolder;
import de.njsm.stocks.client.presenter.UnitAmountRenderStrategy;
import de.njsm.stocks.client.ui.R;

import java.util.List;

import static de.njsm.stocks.client.fragment.util.ListDiffer.byId;

public class FoodAdapter extends RecyclerView.Adapter<FoodOutlineViewHolder> {

    private List<EmptyFood> foods;

    private final View.OnClickListener onClickListener;

    private final View.OnLongClickListener onLongClickListener;

    private final UnitAmountRenderStrategy unitAmountRenderStrategy;

    public FoodAdapter(View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener) {
        this.onClickListener = onClickListener;
        this.onLongClickListener = onLongClickListener;
        unitAmountRenderStrategy = new UnitAmountRenderStrategy();
    }

    public void setData(List<EmptyFood> newList) {
        List<EmptyFood> oldList = foods;
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
        FoodOutlineViewHolder result = new FoodOutlineViewHolder(v);
        result.hideExpirationDate();
        return result;
    }

    @Override
    public void onBindViewHolder(@NonNull FoodOutlineViewHolder holder, int position) {
        EmptyFood item = foods.get(position);
        holder.setName(item.name());
        holder.showToBuy(item.toBuy());
        holder.setAmount(unitAmountRenderStrategy.render(item.storedAmount()));
    }

    @Override
    public int getItemCount() {
        if (foods == null) {
            return 0;
        } else {
            return foods.size();
        }
    }
}
