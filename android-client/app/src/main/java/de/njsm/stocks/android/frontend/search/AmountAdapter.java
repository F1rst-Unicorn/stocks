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
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.R;
import de.njsm.stocks.android.db.views.FoodView;
import de.njsm.stocks.android.frontend.BaseAdapter;

import java.util.List;

public class AmountAdapter extends BaseAdapter<FoodView, AmountAdapter.ViewHolder> {

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

    public AmountAdapter(LiveData<List<FoodView>> data,
                  Consumer<View> onClickListener) {
        super(data, onClickListener);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_food_amount, viewGroup, false);
        ViewHolder result =  new AmountAdapter.ViewHolder(v);
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
