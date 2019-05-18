package de.njsm.stocks.android.frontend.food;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.views.FoodView;
import de.njsm.stocks.android.frontend.BaseAdapter;
import de.njsm.stocks.android.frontend.fooditem.FoodItemAdapter;
import org.threeten.bp.Instant;

import java.util.List;

public class FoodAdapter extends BaseAdapter<FoodView, FoodAdapter.ViewHolder> {

    private Resources resources;

    private Resources.Theme theme;

    private final Consumer<View> onLongClickListener;

    public FoodAdapter(LiveData<List<FoodView>> data,
                       Consumer<View> onClickListener,
                       Consumer<View> onLongClickListener,
                       Resources resources,
                       Resources.Theme theme) {
        super(data, onClickListener);
        this.onLongClickListener = onLongClickListener;
        this.resources = resources;
        this.theme = theme;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name;

        private TextView date;

        private TextView amount;

        private ImageView icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_food_outline_name);
            date = itemView.findViewById(R.id.item_food_outline_date);
            amount = itemView.findViewById(R.id.item_food_outline_count);
            icon = itemView.findViewById(R.id.item_food_outline_icon);
        }

        public void setName(CharSequence content) {
            name.setText(content);
        }

        public void setDate(CharSequence content) {
            date.setText(content);
        }

        public void setAmount(CharSequence content) {
            amount.setText(content);
        }

        public void setIcon(Drawable content) {
            icon.setImageDrawable(content);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food_outline, parent, false);
        ViewHolder result = new ViewHolder(v);
        v.setTag(result);
        v.setOnClickListener(this::onClick);
        v.setOnLongClickListener(this::onLongClick);
        return result;
    }

    private boolean onLongClick(View view) {
        onLongClickListener.accept(view);
        return true;
    }

    @Override
    protected void bindConcrete(ViewHolder holder, FoodView data) {
        holder.setAmount(String.valueOf(data.getAmount()));
        holder.setDate(mapToRelativeDate(data.getEatBy()));
        holder.setName(data.getName());
        holder.setIcon(FoodItemAdapter.computeIcon(resources, theme, data.getEatBy(), Instant.now()));
    }

    @Override
    protected void bindVoid(ViewHolder holder) {
        holder.setAmount("");
        holder.setDate("");
        holder.setName("");
    }

    private CharSequence mapToRelativeDate(Instant date) {
        Instant now = Instant.now();
        return DateUtils.getRelativeTimeSpanString(
                date.toEpochMilli(),
                now.toEpochMilli(),
                0L, DateUtils.FORMAT_ABBREV_ALL);
    }

}
