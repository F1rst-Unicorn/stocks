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

package de.njsm.stocks.client.fragment.pricelist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.client.business.Localiser;
import de.njsm.stocks.client.business.entities.PriceForListing;
import de.njsm.stocks.client.presenter.DateRenderStrategy;
import de.njsm.stocks.client.presenter.UnitAmountRenderStrategy;
import de.njsm.stocks.client.ui.R;

import java.util.List;

import static de.njsm.stocks.client.fragment.util.ListDiffer.byId;

public class PriceAdapter extends RecyclerView.Adapter<PriceViewHolder> {

    private List<PriceForListing> data;

    private final DateRenderStrategy dateRenderStrategy;

    private final UnitAmountRenderStrategy unitAmountRenderStrategy;

    public PriceAdapter(Localiser localiser) {
        this.dateRenderStrategy = new DateRenderStrategy(localiser);
        this.unitAmountRenderStrategy = new UnitAmountRenderStrategy();
    }

    public void setData(List<PriceForListing> data) {
        List<PriceForListing> oldList = this.data;
        this.data = data;
        DiffUtil.calculateDiff(byId(oldList, data, v -> v.id().longId()), true).dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public PriceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_price, parent, false);
        return new PriceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PriceViewHolder holder, int position) {
        PriceForListing item = data.get(position);
        holder.setDate(dateRenderStrategy.render(item.date()));
        holder.setGroceryStore(item.groceryStoreAndChainName());
        holder.setPrice(unitAmountRenderStrategy.render(item.normalisedPrice()));
        holder.setQuantity(unitAmountRenderStrategy.render(item.normalisedQuantity()));
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
