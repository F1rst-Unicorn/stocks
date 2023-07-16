/*
 * stocks is client-server program to manage a household's food stock
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
 *
 */

package de.njsm.stocks.client.fragment.recipecook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.client.business.entities.RecipeCookingFormDataProduct;
import de.njsm.stocks.client.presenter.UnitAmountRenderStrategy;
import de.njsm.stocks.client.ui.R;

import java.util.List;

public class ProductAmountAdapter extends RecyclerView.Adapter<ItemAmountIncrementorViewHolder> {

    private List<RecipeCookingFormDataProduct.Amount> data;

    private final UnitAmountRenderStrategy unitAmountRenderStrategy;

    public ProductAmountAdapter() {
        unitAmountRenderStrategy = new UnitAmountRenderStrategy();
    }

    public void setData(List<RecipeCookingFormDataProduct.Amount> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemAmountIncrementorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_amount_incrementor_product, parent, false);
        return new ItemAmountIncrementorViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAmountIncrementorViewHolder holder, int position) {
        var item = data.get(position);
        holder.setCurrentAmount(unitAmountRenderStrategy.render(item.scaledDefaultProductedAmount()));
        holder.setUnitAbbreviation(unitAmountRenderStrategy.renderUnitSymbol(item));
    }

    @Override
    public int getItemCount() {
        if (data == null) {
            return 0;
        } else {
            return data.size();
        }
    }
}
