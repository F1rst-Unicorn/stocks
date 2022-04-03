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

package de.njsm.stocks.client.fragment.view;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.njsm.stocks.client.ui.R;

public class TextWithPrefixIconViewHolder extends RecyclerView.ViewHolder {

    private final TextView text;

    public TextWithPrefixIconViewHolder(@NonNull View itemView) {
        super(itemView);
        text = itemView.findViewById(R.id.item_text_with_prefix_icon_name);
        itemView.setTag(this);

        text.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_account_balance_wallet_black_24dp, 0, 0, 0);
    }

    public void setText(CharSequence text) {
        this.text.setText(text);
    }
}
