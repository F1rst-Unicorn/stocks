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
import de.njsm.stocks.client.business.entities.conflict.ConflictData;
import de.njsm.stocks.client.ui.R;

import java.util.function.Function;

public class ConflictField {

    private final View root;

    private final TextView originalField;

    private final TextView remoteField;

    private final TextView localField;

    public ConflictField(View root) {
        this.root = root;
        originalField = root.findViewById(R.id.conflict_labels_original_content);
        remoteField = root.findViewById(R.id.conflict_labels_remote_content);
        localField = root.findViewById(R.id.conflict_labels_local_content);
    }

    public void hide() {
        root.setVisibility(View.GONE);
    }

    public <T> void showConflictInfo(ConflictData<T> data, Function<T, String> mapper) {
        root.setVisibility(View.VISIBLE);
        originalField.setText(mapper.apply(data.original()));
        remoteField.setText(mapper.apply(data.remote()));
        localField.setText(mapper.apply(data.local()));
    }

    public void showConflictInfo(ConflictData<String> data) {
        showConflictInfo(data, v -> v);
    }
}
