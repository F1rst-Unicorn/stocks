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

package de.njsm.stocks.android.frontend.util;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.android.db.entities.Positionable;

import java.util.List;

public class SwipeCallback<T extends Positionable> extends BackgroundDrawingSwipeCallback {

    @Nullable
    private List<T> data;

    private final Consumer<T> deleteCallback;

    private Consumer<T> leftCallback;

    public SwipeCallback(@Nullable List<T> data,
                         Drawable icon,
                         ColorDrawable background,
                         Consumer<T> deleteCallback) {
        super(icon, background);
        this.data = data;
        this.deleteCallback = deleteCallback;
    }

    public SwipeCallback(@Nullable List<T> data,
                         Drawable icon,
                         Drawable leftIcon,
                         ColorDrawable background,
                         Consumer<T> deleteCallback,
                         Consumer<T> leftCallback) {
        super(icon, leftIcon, background);
        this.data = data;
        this.deleteCallback = deleteCallback;
        this.leftCallback = leftCallback;
    }

    public void setData(@Nullable List<T> data) {
        this.data = data;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        if (data != null) {
            T item = data.get(position);
            item.setPosition(position);
            if (direction == ItemTouchHelper.END)
                deleteCallback.accept(item);
            else if (direction == ItemTouchHelper.START)
                leftCallback.accept(item);
        }
    }

}
