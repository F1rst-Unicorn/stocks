package de.njsm.stocks.android.frontend.eannumber;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.android.db.entities.EanNumber;
import de.njsm.stocks.android.frontend.BaseAdapter;

import java.util.List;

public class EanNumberAdapter extends BaseAdapter<EanNumber, EanNumberAdapter.ViewHolder> {

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

    EanNumberAdapter(LiveData<List<EanNumber>> data, Consumer<View> onClickListener) {
        super(data, onClickListener);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        TextView v = (TextView) LayoutInflater.from(viewGroup.getContext())
                .inflate(android.R.layout.simple_list_item_1, viewGroup, false);
        ViewHolder result =  new EanNumberAdapter.ViewHolder(v);
        v.setTag(result);
        v.setOnClickListener(this::onClick);
        return result;
    }

    @Override
    protected void bindConcrete(ViewHolder holder, EanNumber data) {
        holder.setText(data.eanCode);
    }

    @Override
    protected void bindVoid(ViewHolder holder) {
        holder.setText("");
    }
}
