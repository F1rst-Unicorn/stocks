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

package de.njsm.stocks.client.databind;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.client.business.entities.FoodForSelection;
import de.njsm.stocks.client.business.entities.ScaledUnitForSelection;
import de.njsm.stocks.client.ui.R;

import java.util.ArrayList;
import java.util.List;

public abstract class RecipeFoodAdapter<T> extends RecyclerView.Adapter<RecipeFoodViewHolder> {

    protected final List<FoodForSelection> foodForSelection;

    protected final List<ScaledUnitForSelection> unitsForSelection;

    protected final List<T> list;

    public RecipeFoodAdapter(List<FoodForSelection> foodForSelection, List<ScaledUnitForSelection> unitsForSelection) {
        this.foodForSelection = foodForSelection;
        this.unitsForSelection = unitsForSelection;
        this.list = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecipeFoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe_food, parent, false);
        return new RecipeFoodViewHolder(v, foodForSelection, unitsForSelection);
    }

    public void add(List<T> items) {
        list.addAll(items);
        notifyItemRangeInserted(list.size() - items.size(), items.size());
    }

    public void delete(int listItemPosition) {
        list.remove(listItemPosition);
        notifyItemRemoved(listItemPosition);
    }

    public List<T> get() {
        return list;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
