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
import androidx.annotation.StringRes;
import com.google.android.material.textfield.TextInputLayout;
import de.njsm.stocks.client.business.entities.conflict.ConflictData;
import de.njsm.stocks.client.ui.R;
import de.njsm.stocks.client.util.NonEmptyValidator;

import java.util.function.BiConsumer;

import static de.njsm.stocks.client.fragment.view.ViewUtility.*;

public class ConflictTextField {

    private final View root;

    private final TextInputLayout textInputField;

    private final View conflictLayout;

    private final TextView originalField;

    private final TextView remoteField;

    private final TextView localField;

    public ConflictTextField(View root) {
        this.root = root;
        textInputField = root.findViewById(R.id.text_field_conflict_text_field);
        conflictLayout = root.findViewById(R.id.text_field_conflict_conflict);
        originalField = root.findViewById(R.id.text_field_conflict_original_content);
        remoteField = root.findViewById(R.id.text_field_conflict_remote_content);
        localField = root.findViewById(R.id.text_field_conflict_local_content);
    }

    public void setEditorHint(@StringRes int id) {
        onEditorOf(textInputField, v -> v.setHint(id));
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

    public void hide() {
        root.setVisibility(View.GONE);
    }

    public void showConflictInfo(ConflictData<String> data) {
        conflictLayout.setVisibility(View.VISIBLE);
        originalField.setText(data.original());
        remoteField.setText(data.remote());
        localField.setText(data.local());
    }

    public String get() {
        return stringFromForm(textInputField);
    }
}
