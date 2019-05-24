package de.njsm.stocks.android.frontend;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public abstract class StringListAdapter<T> extends BaseAdapter<T, StringListAdapter.ViewHolder> {

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

    public StringListAdapter(LiveData<List<T>> data, Consumer<View> onClickListener) {
        super(data, onClickListener);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        TextView v = (TextView) LayoutInflater.from(viewGroup.getContext())
                .inflate(android.R.layout.simple_list_item_1, viewGroup, false);
        ViewHolder result =  new StringListAdapter.ViewHolder(v);
        v.setTag(result);
        v.setOnClickListener(this::onClick);
        return result;
    }

    @Override
    protected void bindVoid(ViewHolder holder) {
        holder.setText("");
    }
}
