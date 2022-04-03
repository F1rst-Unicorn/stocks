package de.njsm.stocks.client.fragment.listswipe;

import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BackgroundDrawingSwipeCallback extends ItemTouchHelper.SimpleCallback {

    private final Drawable icon;

    private Drawable leftIcon;

    private final ColorDrawable background;

    public BackgroundDrawingSwipeCallback(Drawable icon,
                                          ColorDrawable background) {
        super(0, ItemTouchHelper.END);
        this.icon = icon;
        this.background = background;
    }

    public BackgroundDrawingSwipeCallback(Drawable icon,
                                          Drawable leftIcon,
                                          ColorDrawable background) {
        super(0, ItemTouchHelper.END | ItemTouchHelper.START);
        this.icon = icon;
        this.leftIcon = leftIcon;
        this.background = background;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c,
                            @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY,
                            int actionState,
                            boolean isCurrentlyActive) {
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
