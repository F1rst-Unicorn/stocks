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

package de.njsm.stocks.client.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.client.business.entities.LocationForListing;
import de.njsm.stocks.client.ui.R;

import java.util.List;

import static de.njsm.stocks.client.view.ListDiffer.byId;

public class LocationAdapter extends RecyclerView.Adapter<ViewHolder> {

    private List<LocationForListing> locations;

    public void setData(List<LocationForListing> newList) {
        List<LocationForListing> oldList = locations;
        locations = newList;
        DiffUtil.calculateDiff(byId(oldList, newList), true).dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_text_with_prefix_icon, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocationForListing item = locations.get(position);
        holder.setText(item.name());
    }

    @Override
    public int getItemCount() {
        if (locations == null) {
            return 0;
        } else {
            return locations.size();
        }
    }
}
