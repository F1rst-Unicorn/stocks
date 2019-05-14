package de.njsm.stocks.android.frontend.device;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.android.db.entities.UserDevice;

import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {

    private LiveData<List<UserDevice>> data;

    private Consumer<View> onClickListener;

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

    DeviceAdapter(LiveData<List<UserDevice>> data, Consumer<View> onClickListener) {
        this.data = data;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        TextView v = (TextView) LayoutInflater.from(viewGroup.getContext())
                .inflate(android.R.layout.simple_list_item_1, viewGroup, false);
        ViewHolder result =  new DeviceAdapter.ViewHolder(v);
        v.setTag(result);
        v.setOnClickListener(this::onClick);
        return result;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        List<UserDevice> list = data.getValue();
        if (list != null) {
            String username = list.get(i).name;
            viewHolder.setText(username);
        } else {
            viewHolder.setText("");
        }
    }

    private void onClick(View view) {
        onClickListener.accept(view);
    }

    @Override
    public int getItemCount() {
        List<UserDevice> list = data.getValue();
        if (list != null)
            return list.size();
        else
            return 0;
    }
}
