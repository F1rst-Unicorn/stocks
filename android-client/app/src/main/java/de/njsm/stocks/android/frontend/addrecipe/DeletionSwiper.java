package de.njsm.stocks.android.frontend.addrecipe;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.android.frontend.util.BackgroundDrawingSwipeCallback;

import java.util.function.Consumer;

public class DeletionSwiper extends BackgroundDrawingSwipeCallback {

    private final Consumer<Integer> deleteCallback;

    public DeletionSwiper(Drawable icon, ColorDrawable background, Consumer<Integer> deleteCallback) {
        super(icon, background);
        this.deleteCallback = deleteCallback;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (direction == ItemTouchHelper.END)
            deleteCallback.accept(viewHolder.getAbsoluteAdapterPosition());
    }
}
