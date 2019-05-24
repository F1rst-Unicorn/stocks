package de.njsm.stocks.android.frontend.locations;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.Location;
import de.njsm.stocks.android.frontend.BaseAdapter;

import java.util.List;

public class LocationAdapter extends BaseAdapter<Location, LocationAdapter.ViewHolder> {

    private Consumer<View> onLongClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView view;

        ViewHolder(@NonNull TextView itemView) {
            super(itemView);
            view = itemView;
        }

        public void setText(CharSequence c) {
            view.setText(c);
        }
    }

    LocationAdapter(LiveData<List<Location>> data,
                    Consumer<View> onClickListener,
                    Consumer<View> onLongClickListener) {
        super(data, onClickListener);
        this.onLongClickListener = onLongClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        TextView v = (TextView) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_location, viewGroup, false);
        ViewHolder result =  new LocationAdapter.ViewHolder(v);
        v.setTag(result);
        v.setOnClickListener(this::onClick);
        v.setOnLongClickListener(this::onLongClick);
        return result;
    }

    @Override
    protected void bindConcrete(ViewHolder holder, Location data) {
        holder.setText(data.name);
    }

    @Override
    protected void bindVoid(ViewHolder holder) {
        holder.setText("");
    }

    private boolean onLongClick(View view) {
        if (onLongClickListener != null)
            onLongClickListener.accept(view);
        return true;
    }
}
