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

package de.njsm.stocks.android.frontend.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.njsm.stocks.R;
import de.njsm.stocks.android.db.views.FoodWithLatestItemView;
import de.njsm.stocks.android.frontend.BaseAdapter;

import static android.view.View.GONE;

public class AmountAdapter extends BaseAdapter<FoodWithLatestItemView, AmountAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name;

        private TextView amount;

        private ImageView shoppingCart;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_food_amount_name);
            amount = itemView.findViewById(R.id.item_food_amount_amout);
            shoppingCart = itemView.findViewById(R.id.item_food_amount_shopping_flag);
        }

        public void setName(CharSequence content) {
            name.setText(content);
        }

        public void setAmount(CharSequence content) {
            amount.setText(content);
        }

        public void setBuyStatus(boolean toBuy) {
            if (! toBuy)
                shoppingCart.setVisibility(GONE);
            else
                shoppingCart.setVisibility(View.VISIBLE);
        }
    }

    public AmountAdapter(LiveData<List<FoodWithLatestItemView>> data, Consumer<View> onClickListener, Consumer<View> onLongClickListener) {
        super(data, onClickListener, onLongClickListener);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_food_amount, viewGroup, false);
        ViewHolder result =  new AmountAdapter.ViewHolder(v);
        v.setTag(result);
        v.setOnClickListener(this::onClick);
        v.setOnLongClickListener(this::onLongClick);
        return result;
    }

    @Override
    protected void bindConcrete(ViewHolder holder, FoodWithLatestItemView data) {
        holder.setName(data.getName());
        holder.setAmount(String.valueOf(data.getAmount()));
        holder.setBuyStatus(data.getToBuy());
    }

    @Override
    protected void bindVoid(ViewHolder holder) {
        holder.setName("");
        holder.setAmount("");
        holder.setBuyStatus(false);
    }
}
