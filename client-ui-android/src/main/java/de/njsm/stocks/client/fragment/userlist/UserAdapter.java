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

package de.njsm.stocks.client.fragment.userlist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.client.business.entities.UserForListing;
import de.njsm.stocks.client.fragment.view.TextWithPrefixIconViewHolder;
import de.njsm.stocks.client.ui.R;

import java.util.List;

import static de.njsm.stocks.client.fragment.util.ListDiffer.byId;

public class UserAdapter extends RecyclerView.Adapter<TextWithPrefixIconViewHolder> {

    private List<UserForListing> users;

    private final View.OnClickListener onClickListener;

    public UserAdapter(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setData(List<UserForListing> newList) {
        List<UserForListing> oldList = users;
        users = newList;
        DiffUtil.calculateDiff(byId(oldList, newList), true).dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public TextWithPrefixIconViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_text_with_prefix_icon, parent, false);
        v.setOnClickListener(onClickListener);
        return new TextWithPrefixIconViewHolder(v, R.drawable.ic_person_black_24dp);
    }

    @Override
    public void onBindViewHolder(@NonNull TextWithPrefixIconViewHolder holder, int position) {
        UserForListing item = users.get(position);
        holder.setText(item.name());
    }

    @Override
    public int getItemCount() {
        if (users == null) {
            return 0;
        } else {
            return users.size();
        }
    }
}
