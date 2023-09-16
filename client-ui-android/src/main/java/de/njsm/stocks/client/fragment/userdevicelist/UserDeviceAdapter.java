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

package de.njsm.stocks.client.fragment.userdevicelist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.client.business.entities.UserDeviceForListing;
import de.njsm.stocks.client.fragment.view.TextWithPrefixIconViewHolder;
import de.njsm.stocks.client.ui.R;

import java.util.List;

import static de.njsm.stocks.client.fragment.util.ListDiffer.byId;

public class UserDeviceAdapter extends RecyclerView.Adapter<TextWithPrefixIconViewHolder> {

    private List<UserDeviceForListing> userDevices;

    private final View.OnClickListener onClickListener;

    private final View.OnLongClickListener onLongClickListener;

    public UserDeviceAdapter(View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener) {
        this.onClickListener = onClickListener;
        this.onLongClickListener = onLongClickListener;
    }

    public void setData(List<UserDeviceForListing> newList) {
        List<UserDeviceForListing> oldList = userDevices;
        userDevices = newList;
        DiffUtil.calculateDiff(byId(oldList, newList), true).dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public TextWithPrefixIconViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_text_with_prefix_icon, parent, false);
        v.setOnLongClickListener(onLongClickListener);
        return new TextWithPrefixIconViewHolder(v, R.drawable.baseline_phone_android_black_24);
    }

    @Override
    public void onBindViewHolder(@NonNull TextWithPrefixIconViewHolder holder, int position) {
        UserDeviceForListing item = userDevices.get(position);
        holder.setText(item.name());

        if (item.ticketPresent()) {
            holder.itemView.setOnClickListener(onClickListener);
            holder.setIconAtEnd(R.drawable.baseline_qr_code_black_24);
        } else {
            holder.itemView.setOnClickListener(null);
            holder.removeIconAtEnd();
        }
    }

    @Override
    public int getItemCount() {
        if (userDevices == null) {
            return 0;
        } else {
            return userDevices.size();
        }
    }
}
