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
 */

package de.njsm.stocks.client.fragment.listswipe;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SwipeCallback extends BackgroundDrawingSwipeCallback {

    private final Consumer<Integer> deleteCallback;

    private Consumer<Integer> leftCallback;

    public SwipeCallback(Drawable icon,
                         ColorDrawable background,
                         Consumer<Integer> deleteCallback) {
        super(icon, background);
        this.deleteCallback = deleteCallback;
    }

    public SwipeCallback(Drawable icon,
                         Drawable leftIcon,
                         ColorDrawable background,
                         Consumer<Integer> deleteCallback,
                         Consumer<Integer> leftCallback) {
        super(icon, leftIcon, background);
        this.deleteCallback = deleteCallback;
        this.leftCallback = leftCallback;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getBindingAdapterPosition();
        if (direction == ItemTouchHelper.END)
            deleteCallback.accept(position);
        else if (direction == ItemTouchHelper.START)
            leftCallback.accept(position);
    }

}
