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
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import de.njsm.stocks.client.ui.R;

public class ConflictSpinner extends ConflictField {

    private final View root;

    private final Spinner spinner;

    public ConflictSpinner(View root) {
        super(root.findViewById(R.id.spinner_conflict_conflict));
        this.root = root;
        spinner = root.findViewById(R.id.spinner_conflict_spinner);
    }

    public void setAdapter(SpinnerAdapter adapter) {
        spinner.setAdapter(adapter);
    }

    public void setSelection(int position) {
        spinner.setSelection(position);
    }

    @Override
    public void hide() {
        super.hide();
        root.setVisibility(View.GONE);
        spinner.setVisibility(View.GONE);
    }

    public <T> T getSelectedItem() {
        return (T) spinner.getSelectedItem();
    }
}
