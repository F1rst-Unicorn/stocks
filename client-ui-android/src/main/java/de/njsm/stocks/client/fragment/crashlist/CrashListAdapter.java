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

package de.njsm.stocks.client.fragment.crashlist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.client.business.entities.CrashLog;
import de.njsm.stocks.client.presenter.DateRenderStrategy;
import de.njsm.stocks.client.ui.R;

import java.util.List;

class CrashListAdapter extends RecyclerView.Adapter<CrashLogViewHolder> {

    private List<CrashLog> list;

    private final View.OnClickListener onClickListener;

    private final DateRenderStrategy dateRenderStrategy;

    CrashListAdapter(View.OnClickListener onClickListener, DateRenderStrategy dateRenderStrategy) {
        this.onClickListener = onClickListener;
        this.dateRenderStrategy = dateRenderStrategy;
    }

    void setData(List<CrashLog> newList) {
        list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CrashLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_crash_log, parent, false);
        v.setOnClickListener(onClickListener);
        return new CrashLogViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CrashLogViewHolder holder, int position) {
        CrashLog item = list.get(position);
        holder.setName(item.exceptionName());
        holder.setDate(dateRenderStrategy.render(item.timeOccurred()));
    }

    @Override
    public int getItemCount() {
        if (list == null) {
            return 0;
        } else {
            return list.size();
        }
    }
}
