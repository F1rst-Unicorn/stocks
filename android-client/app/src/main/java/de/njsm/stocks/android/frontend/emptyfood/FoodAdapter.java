/* stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.njsm.stocks.android.frontend.emptyfood;

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
import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.frontend.BaseAdapter;

import java.util.List;

import static android.view.View.GONE;

public class FoodAdapter extends BaseAdapter<Food, FoodAdapter.ViewHolder> {

    private Consumer<View> onLongClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout view;

        private TextView textView;

        private ImageView shoppingCart;

        ViewHolder(@NonNull RelativeLayout itemView) {
            super(itemView);
            view = itemView;
            textView = view.findViewById(R.id.item_empty_food_outline_name);
            shoppingCart = view.findViewById(R.id.item_empty_food_outline_shopping_flag);
        }

        public void setText(CharSequence c) {
            textView.setText(c);
        }

        public void setBuyStatus(boolean toBuy) {
            if (! toBuy)
                shoppingCart.setVisibility(GONE);
            else
                shoppingCart.setVisibility(View.VISIBLE);
        }
    }

    FoodAdapter(LiveData<List<Food>> data, Consumer<View> onClickListener, Consumer<View> onLongClickListener) {
        super(data, onClickListener);
        this.onLongClickListener = onLongClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_empty_food_outline, viewGroup, false);
        ViewHolder result =  new FoodAdapter.ViewHolder(v);
        v.setTag(result);
        v.setOnClickListener(this::onClick);
        v.setOnLongClickListener(this::onLongClick);
        return result;
    }

    private boolean onLongClick(View view) {
        this.onLongClickListener.accept(view);
        return true;
    }

    @Override
    protected void bindConcrete(ViewHolder holder, Food data) {
        holder.setText(data.name);
        holder.setBuyStatus(data.toBuy);
    }

    @Override
    protected void bindVoid(ViewHolder holder) {
        holder.setText("");
        holder.setBuyStatus(false);
    }
}
