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

package de.njsm.stocks.android.frontend.units;

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
import de.njsm.stocks.android.db.views.ScaledUnitView;
import de.njsm.stocks.android.frontend.BaseAdapter;

import java.util.List;

public class ScaledUnitAdapter extends BaseAdapter<ScaledUnitView, ScaledUnitAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView scale;

        private final TextView unit;

        ViewHolder(RelativeLayout layout) {
            super(layout);
            this.scale = layout.findViewById(R.id.item_scaled_unit_scale);
            this.unit = layout.findViewById(R.id.item_scaled_unit_unit);
        }

        public void setScale(CharSequence c) {
            scale.setText(c);
        }

        public void setUnit(CharSequence c) {
            unit.setText(c);
        }
    }

    ScaledUnitAdapter(LiveData<List<ScaledUnitView>> data,
                      Consumer<View> onClickListener,
                      Consumer<View> onLongClickListener) {
        super(data, onClickListener, onLongClickListener);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_scaled_unit, viewGroup, false);
        ViewHolder result =  new ScaledUnitAdapter.ViewHolder(v);
        v.setTag(result);
        v.setOnClickListener(this::onClick);
        v.setOnLongClickListener(this::onLongClick);
        return result;
    }

    @Override
    protected void bindConcrete(ViewHolder holder, ScaledUnitView data) {
        holder.setScale(data.getScale().toString());
        holder.setUnit(data.getUnitEntity().getAbbreviation());
    }

    @Override
    protected void bindVoid(ViewHolder holder) {
        holder.setScale("");
        holder.setUnit("");
    }
}
