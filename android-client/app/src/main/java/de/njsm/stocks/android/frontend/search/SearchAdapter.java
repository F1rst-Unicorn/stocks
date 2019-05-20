package de.njsm.stocks.android.frontend.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.views.FoodView;
import de.njsm.stocks.android.frontend.BaseAdapter;

import java.util.List;

public class SearchAdapter extends BaseAdapter<FoodView, SearchAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name;

        private TextView amount;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_food_amount_name);
            amount = itemView.findViewById(R.id.item_food_amount_amout);
        }

        public void setName(CharSequence content) {
            name.setText(content);
        }

        public void setAmount(CharSequence content) {
            amount.setText(content);
        }
    }

    SearchAdapter(LiveData<List<FoodView>> data,
                  Consumer<View> onClickListener) {
        super(data, onClickListener);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_food_amount, viewGroup, false);
        ViewHolder result =  new SearchAdapter.ViewHolder(v);
        v.setTag(result);
        v.setOnClickListener(this::onClick);
        return result;
    }

    @Override
    protected void bindConcrete(ViewHolder holder, FoodView data) {
        holder.setName(data.getName());
        holder.setAmount(String.valueOf(data.getAmount()));
    }

    @Override
    protected void bindVoid(ViewHolder holder) {
        holder.setName("");
        holder.setAmount("");
    }
}
