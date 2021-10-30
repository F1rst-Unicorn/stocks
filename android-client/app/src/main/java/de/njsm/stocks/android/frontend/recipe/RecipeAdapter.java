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

package de.njsm.stocks.android.frontend.recipe;

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
import de.njsm.stocks.android.db.views.RecipeWithRating;
import de.njsm.stocks.android.frontend.BaseAdapter;

import java.util.List;

public class RecipeAdapter extends BaseAdapter<RecipeWithRating, RecipeAdapter.ViewHolder> {

    private final String ratingFormatString;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;

        private final TextView rating;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_recipe_name);
            rating = itemView.findViewById(R.id.item_recipe_rating);
        }

        public void setName(CharSequence c) {
            name.setText(c);
        }

        public void setRating(CharSequence c) {
            rating.setText(c);
        }
    }

    RecipeAdapter(LiveData<List<RecipeWithRating>> data,
                  Consumer<View> onClickListener,
                  Consumer<View> onLongClickListener,
                  String ratingFormatString) {
        super(data, onClickListener, onLongClickListener);
        this.ratingFormatString = ratingFormatString;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_recipe, viewGroup, false);
        ViewHolder result =  new RecipeAdapter.ViewHolder(v);
        v.setTag(result);
        v.setOnClickListener(this::onClick);
        v.setOnLongClickListener(this::onLongClick);
        return result;
    }

    @Override
    protected void bindConcrete(ViewHolder holder, RecipeWithRating data) {
        holder.setName(data.getRecipe().getName());
        holder.setRating(data.getRating().format(ratingFormatString));
    }

    @Override
    protected void bindVoid(ViewHolder holder) {
        holder.setName("");
        holder.setRating("");
    }
}
