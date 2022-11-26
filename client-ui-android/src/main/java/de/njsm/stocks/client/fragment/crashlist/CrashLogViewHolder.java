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

package de.njsm.stocks.client.fragment.crashlist;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.client.ui.R;

class CrashLogViewHolder extends RecyclerView.ViewHolder {

    private final TextView name;

    private final TextView date;

    CrashLogViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.item_crash_log_name);
        date = itemView.findViewById(R.id.item_crash_log_date);
        itemView.setTag(this);
    }

    void setName(CharSequence name) {
        this.name.setText(name);
    }

    void setDate(CharSequence date) {
        this.date.setText(date);
    }
}
