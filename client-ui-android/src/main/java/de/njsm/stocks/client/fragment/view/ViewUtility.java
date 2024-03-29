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

import android.widget.EditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Optional;
import java.util.function.Consumer;

public class ViewUtility {

    public static String stringFromForm(TextInputLayout view) {
        EditText editText = view.getEditText();
        if (editText != null) {
            return editText.getText().toString();
        } else {
            return "";
        }
    }

    public static void setText(TextInputLayout view, int text) {
        setText(view, String.valueOf(text));
    }

    public static void setText(TextInputLayout inputField, String text) {
        onEditorOf(inputField, e -> e.setText(text));
    }

    public static void onEditorOf(TextInputLayout inputField, Consumer<EditText> callback) {
        Optional.ofNullable(inputField.getEditText()).ifPresent(callback);
    }
}
