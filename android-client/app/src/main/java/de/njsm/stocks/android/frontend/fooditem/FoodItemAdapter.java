package de.njsm.stocks.android.frontend.fooditem;

import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
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
import de.njsm.stocks.android.db.views.FoodItemView;
import de.njsm.stocks.android.frontend.BaseAdapter;
import de.njsm.stocks.android.util.Config;
import org.threeten.bp.Duration;
import org.threeten.bp.Instant;

import java.util.List;

public class FoodItemAdapter extends BaseAdapter<FoodItemView, FoodItemAdapter.ViewHolder> {

    private Consumer<View> onLongClickListener;

    private Resources resources;

    private Resources.Theme theme;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout view;

        private TextView date;

        private TextView location;

        private TextView buyer;

        private TextView device;

        private ImageView icon;

        ViewHolder(@NonNull RelativeLayout itemView) {
            super(itemView);
            view = itemView;
            date = view.findViewById(R.id.item_food_item_date);
            location = view.findViewById(R.id.item_food_item_location);
            buyer = view.findViewById(R.id.item_food_item_user);
            device = view.findViewById(R.id.item_food_item_device);
            icon = view.findViewById(R.id.item_food_item_icon);
        }

        public void setDate(CharSequence c) {
            date.setText(c);
        }

        public void setLocation(CharSequence c) {
            location.setText(c);
        }

        public void setBuyer(CharSequence c) {
            buyer.setText(c);
        }

        public void setDevice(CharSequence c) {
            device.setText(c);
        }

        public void setIcon(Drawable icon) {
            this.icon.setImageDrawable(icon);
        }
    }

    FoodItemAdapter(Resources resources,
                    Resources.Theme theme,
                    LiveData<List<FoodItemView>> data,
                    Consumer<View> onLongClickListener) {
        super(data, onLongClickListener);
        this.onLongClickListener = onLongClickListener;
        this.resources = resources;
        this.theme = theme;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_food_item, viewGroup, false);
        ViewHolder result =  new FoodItemAdapter.ViewHolder(v);
        v.setTag(result);
        v.setOnLongClickListener(this::onLongClick);
        return result;
    }

    private boolean onLongClick(View view) {
        this.onLongClickListener.accept(view);
        return true;
    }

    @Override
    protected void bindConcrete(ViewHolder holder, FoodItemView data) {
        holder.setBuyer(data.getUserName());
        holder.setDevice(data.getDeviceName());
        holder.setLocation(data.getLocation());
        holder.setDate(Config.PRETTY_FORMAT.format(data.getEatByDate()));
        holder.setIcon(computeIcon(data.getEatByDate(), Instant.now()));
    }

    @Override
    protected void bindVoid(ViewHolder holder) {
        holder.setLocation("");
        holder.setDevice("");
        holder.setBuyer("");
        holder.setDate("");
    }

    private Drawable computeIcon(Instant expiration, Instant now) {
        Instant inFiveDays = now.plus(Duration.ofDays(5));
        Drawable result;

        int red = resources.getColor(android.R.color.holo_red_light, theme);
        int green = resources.getColor(R.color.colorPrimary, theme);
        int colour;

        if (expiration.isBefore(now)) {
            result = resources.getDrawable(R.drawable.ic_error_black_24dp, theme);
            colour = red;
        } else if (expiration.isAfter(inFiveDays)) {
            result = resources.getDrawable(R.drawable.ic_check_black_24dp, theme);
            colour = green;
        } else {
            result = resources.getDrawable(R.drawable.ic_alarm_black_24dp, theme);
            colour = red;
        }
        result.setColorFilter(new PorterDuffColorFilter(
                colour,
                PorterDuff.Mode.SRC_ATOP));
        return result;
    }
}
