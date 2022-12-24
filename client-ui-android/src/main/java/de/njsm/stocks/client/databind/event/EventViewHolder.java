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

package de.njsm.stocks.client.databind.event;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.client.ui.R;

public class EventViewHolder extends RecyclerView.ViewHolder {

    private final TextView time;

    private final TextView text;

    private final ImageView dataIcon;

    private final ImageView actionIcon;

    public EventViewHolder(@NonNull View itemView) {
        super(itemView);
        time = itemView.findViewById(R.id.item_event_date);
        text = itemView.findViewById(R.id.item_event_text);
        dataIcon = itemView.findViewById(R.id.item_event_entity_icon);
        actionIcon = itemView.findViewById(R.id.item_event_event_icon);
    }

    public void setTime(String time) {
        this.time.setText(time);
    }

    public void setText(String text) {
        this.text.setText(text);
    }

    public void setLoading() {
        text.setText("...");
        time.setText("");
        dataIcon.setImageDrawable(null);
        actionIcon.setImageDrawable(null);
    }

    public void setDataIcon(@DrawableRes int icon) {
        dataIcon.setImageDrawable(AppCompatResources.getDrawable(
                itemView.getContext(),
                icon));
    }

    public void setActionIcon(@DrawableRes int icon) {
        actionIcon.setImageDrawable(AppCompatResources.getDrawable(
                itemView.getContext(),
                icon));
    }
}
