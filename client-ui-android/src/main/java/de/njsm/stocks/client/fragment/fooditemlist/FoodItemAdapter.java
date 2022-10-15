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

package de.njsm.stocks.client.fragment.fooditemlist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.client.business.Clock;
import de.njsm.stocks.client.business.entities.FoodItemForListing;
import de.njsm.stocks.client.databind.ExpirationIconProvider;
import de.njsm.stocks.client.presenter.DateRenderStrategy;
import de.njsm.stocks.client.presenter.UnitAmountRenderStrategy;
import de.njsm.stocks.client.ui.R;

import java.time.ZoneOffset;
import java.util.List;

import static de.njsm.stocks.client.fragment.util.ListDiffer.byId;

class FoodItemAdapter extends RecyclerView.Adapter<FoodItemViewHolder> {

    private final DateRenderStrategy dateRenderStrategy;

    private final Clock clock;

    private final ExpirationIconProvider expirationIconProvider;

    private List<FoodItemForListing> data;

    private final View.OnClickListener onClickListener;

    private final UnitAmountRenderStrategy unitAmountRenderStrategy;

    FoodItemAdapter(View.OnClickListener onClickListener, ExpirationIconProvider expirationIconProvider, Clock clock) {
        this.onClickListener = onClickListener;
        unitAmountRenderStrategy = new UnitAmountRenderStrategy();
        this.dateRenderStrategy = new DateRenderStrategy();
        this.clock = clock;
        this.expirationIconProvider = expirationIconProvider;
    }

    void setData(List<FoodItemForListing> newList) {
        List<FoodItemForListing> oldList = data;
        data = newList;
        DiffUtil.calculateDiff(byId(oldList, newList), true).dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public FoodItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food_item, parent, false);
        v.setOnClickListener(onClickListener);
        return new FoodItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodItemViewHolder holder, int position) {
        FoodItemForListing item = data.get(position);
        holder.setBuyer(item.buyer());
        holder.setRegisterer(item.registerer());
        holder.setLocation(item.location());
        holder.setAmount(unitAmountRenderStrategy.render(item.amount()));
        holder.setEatBy(dateRenderStrategy.render(item.eatBy()));
        holder.setWarningLevel(expirationIconProvider.computeIcon(item.eatBy().atStartOfDay(ZoneOffset.systemDefault()).toInstant(), clock.get()));
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
