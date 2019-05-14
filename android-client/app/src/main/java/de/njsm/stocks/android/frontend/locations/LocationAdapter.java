package de.njsm.stocks.android.frontend.locations;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.Location;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    private LiveData<List<Location>> data;

    private Consumer<View> onClickListener;

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
                    @Nullable Consumer<View> onClickListener,
                    @Nullable Consumer<View> onLongClickListener) {
        this.data = data;
        this.onClickListener = onClickListener;
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
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        List<Location> list = data.getValue();
        if (list != null) {
            String locationname = list.get(i).name;
            viewHolder.setText(locationname);
        } else {
            viewHolder.setText("");
        }
    }

    private void onClick(View view) {
        if (onClickListener != null)
            onClickListener.accept(view);
    }

    private boolean onLongClick(View view) {
        if (onLongClickListener != null)
            onLongClickListener.accept(view);
        return true;
    }

    @Override
    public int getItemCount() {
        List<Location> list = data.getValue();
        if (list != null)
            return list.size();
        else
            return 0;
    }
}
