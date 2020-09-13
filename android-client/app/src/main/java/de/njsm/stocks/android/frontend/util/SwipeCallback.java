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

import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.njsm.stocks.android.db.entities.Positionable;

public class SwipeCallback<T extends Positionable> extends ItemTouchHelper.SimpleCallback {

    @Nullable
    private List<T> data;

    private Consumer<T> deleteCallback;

    private Consumer<T> leftCallback;

    private Drawable icon;

    private Drawable leftIcon;

    private ColorDrawable background;

    public SwipeCallback(@Nullable List<T> data,
                         Drawable icon,
                         ColorDrawable background,
                         Consumer<T> deleteCallback) {
        super(0, ItemTouchHelper.END);
        this.data = data;
        this.deleteCallback = deleteCallback;
        this.icon = icon;
        this.background = background;
    }

    public SwipeCallback(@Nullable List<T> data,
                         Drawable icon,
                         Drawable leftIcon,
                         ColorDrawable background,
                         Consumer<T> deleteCallback,
                         Consumer<T> leftCallback) {
        super(0, ItemTouchHelper.END | ItemTouchHelper.START);
        this.data = data;
        this.deleteCallback = deleteCallback;
        this.leftCallback = leftCallback;
        this.icon = icon;
        this.leftIcon = leftIcon;
        this.background = background;
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

    @Override
    public void onChildDraw(@NonNull Canvas c,
                            @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20;

        if (dX > 0) {
            int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconBottom = iconTop + icon.getIntrinsicHeight();
            int iconLeft = itemView.getLeft() + iconMargin;
            int iconRight = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getLeft(),
                    itemView.getTop(),
                    itemView.getLeft() + ((int) dX) + backgroundCornerOffset,
                    itemView.getBottom());
            background.draw(c);
            icon.draw(c);
        } else if (dX < 0) {
            int iconMargin = (itemView.getHeight() - leftIcon.getIntrinsicHeight()) / 2;
            int iconTop = itemView.getTop() + (itemView.getHeight() - leftIcon.getIntrinsicHeight()) / 2;
            int iconBottom = iconTop + leftIcon.getIntrinsicHeight();
            int iconLeft = itemView.getRight() - iconMargin - leftIcon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            leftIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                    itemView.getTop(),
                    itemView.getRight(),
                    itemView.getBottom());
            background.draw(c);
            leftIcon.draw(c);

        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
