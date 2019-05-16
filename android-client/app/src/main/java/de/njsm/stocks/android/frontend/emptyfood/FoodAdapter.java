package de.njsm.stocks.android.frontend.emptyfood;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.frontend.BaseAdapter;

import java.util.List;

public class FoodAdapter extends BaseAdapter<Food, FoodAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout view;

        private TextView textView;

        ViewHolder(@NonNull RelativeLayout itemView) {
            super(itemView);
            view = itemView;
            textView = view.findViewById(R.id.item_empty_food_outline_name);
        }

        public void setText(CharSequence c) {
            textView.setText(c);
        }
    }

    FoodAdapter(LiveData<List<Food>> data, Consumer<View> onClickListener) {
        super(data, onClickListener);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_empty_food_outline, viewGroup, false);
        ViewHolder result =  new FoodAdapter.ViewHolder(v);
        v.setTag(result);
        v.setOnClickListener(this::onClick);
        return result;
    }

    @Override
    protected void bindConcrete(ViewHolder holder, Food data) {
        holder.setText(data.name);
    }

    @Override
    protected void bindVoid(ViewHolder holder) {
        holder.setText("");
    }
}
