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

package de.njsm.stocks.android.frontend.interactor;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.R;
import de.njsm.stocks.android.network.server.StatusCode;

import java.util.List;

public interface Editor<T> {

    default void initiateEditing(View view,
                                LiveData<List<T>> data,
                                int messageId) {
        RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) view.getTag();
        int position = holder.getAdapterPosition();
        List<T> list = data.getValue();
        if (list != null) {
            T item = list.get(position);
            View editLayout = getEditLayout(item, getLayoutInflater());
            new AlertDialog.Builder(getContext())
                    .setTitle(getString(messageId))
                    .setView(editLayout)
                    .setPositiveButton(getString(R.string.dialog_ok), (dialog, which) -> edit(item, dialog, editLayout)
                            .observe(getViewLifecycleOwner(), c -> this.treatStatusCode(item, c)))
                    .show();
        }
    }

    LiveData<StatusCode> edit(T item, DialogInterface dialog, View view);

    View getEditLayout(T item, LayoutInflater layoutInflater);

    void treatStatusCode(T item, StatusCode statusCode);



    LayoutInflater getLayoutInflater();

    String getString(int resourceId);

    Context getContext();

    LifecycleOwner getViewLifecycleOwner();
}
