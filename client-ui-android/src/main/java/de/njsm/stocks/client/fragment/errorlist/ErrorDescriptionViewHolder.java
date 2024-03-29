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

package de.njsm.stocks.client.fragment.errorlist;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.client.ui.R;

class ErrorDescriptionViewHolder extends RecyclerView.ViewHolder {

    private final TextView headline;

    private final TextView details;

    private final TextView code;

    ErrorDescriptionViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setTag(this);

        headline = itemView.findViewById(R.id.item_error_headline);
        details = itemView.findViewById(R.id.item_error_details);
        code = itemView.findViewById(R.id.item_error_code);
    }

    void setHeadline(@StringRes int id) {
        headline.setText(id);
    }

    void setErrorCode(@StringRes int id) {
        code.setText(id);
    }

    void setDetails(String text) {
        details.setText(text);
    }
}
