/* stocks is client-server program to manage a household's food stock
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
 */

package de.njsm.stocks.android.frontend;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public abstract class BaseAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private LiveData<List<T>> data;

    private final Consumer<View> onClickListener;

    private Consumer<View> onLongClickListener;

    public BaseAdapter(LiveData<List<T>> data,
                       Consumer<View> onClickListener) {
        this.data = data;
        this.onClickListener = onClickListener;
    }

    public BaseAdapter(LiveData<List<T>> data, Consumer<View> onClickListener, Consumer<View> onLongClickListener) {
        this.data = data;
        this.onClickListener = onClickListener;
        this.onLongClickListener = onLongClickListener;
    }

    public void setData(LiveData<List<T>> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    protected LiveData<List<T>> getData() {
        return data;
    }

    protected void onClick(View v) {
        onClickListener.accept(v);
    }

    protected boolean onLongClick(View v) {
        if (onLongClickListener != null) {
            onLongClickListener.accept(v);
            return true;
        } else
            return false;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        List<T> list = data.getValue();
        if (list != null) {
            T item = list.get(position);
            bindConcrete(holder, item);
        } else {
            bindVoid(holder);
        }
    }

    protected abstract void bindConcrete(VH holder, T data);

    protected abstract void bindVoid(VH holder);

    @Override
    public int getItemCount() {
        List<T> list = data.getValue();
        if (list != null)
            return list.size();
        else
            return 0;
    }
}
