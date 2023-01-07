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
import androidx.annotation.StringRes;
import com.google.android.material.textfield.TextInputLayout;
import de.njsm.stocks.client.ui.R;

import java.util.function.BiConsumer;

import static android.view.View.GONE;
import static de.njsm.stocks.client.fragment.view.ViewUtility.*;

public class ConflictTextField extends ConflictField {

    private final View root;

    private final TextInputLayout textInputField;

    public ConflictTextField(View root) {
        super(root.findViewById(R.id.text_field_conflict_conflict));
        this.root = root;
        this.textInputField = root.findViewById(R.id.text_field_conflict_text_field);
    }

    public void setEditorHint(@StringRes int id) {
        textInputField.setHint(id);
    }

    /**
     * @see android.view.inputmethod.EditorInfo
     */
    public void setInputType(int inputType) {
        onEditorOf(textInputField, v -> v.setInputType(inputType));
    }

    public void setError(String error) {
        textInputField.setError(error);
    }

    public void setEditorContent(String text) {
        setText(textInputField, text);
    }

    public void addNonEmptyValidator(BiConsumer<TextInputLayout, Boolean> callback) {
        onEditorOf(textInputField, v -> v.addTextChangedListener(new NonEmptyValidator(textInputField, callback)));
    }

    public String get() {
        return stringFromForm(textInputField);
    }

    @Override
    public void hide() {
        super.hide();
        root.setVisibility(GONE);
        textInputField.setVisibility(GONE);
    }
}
