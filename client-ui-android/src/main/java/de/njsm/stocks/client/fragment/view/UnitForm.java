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
import de.njsm.stocks.client.business.entities.conflict.ConflictData;
import de.njsm.stocks.client.ui.R;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class UnitForm {

    private final ConflictTextField nameField;

    private final ConflictTextField abbreviationField;

    private final Function<Integer, String> dictionary;

    private final Set<ConflictTextField> invalidFields;

    public UnitForm(View root, Function<Integer, String> dictionary) {
        this.nameField = new ConflictTextField(root.findViewById(R.id.fragment_unit_form_name));
        this.abbreviationField = new ConflictTextField(root.findViewById(R.id.fragment_unit_form_abbreviation));
        this.dictionary = dictionary;
        invalidFields = new HashSet<>();
        invalidFields.add(nameField);
        invalidFields.add(abbreviationField);

        nameField.addNonEmptyValidator((a,b) -> onInputChanged(nameField, a, b));
        nameField.setEditorHint(R.string.hint_name);
        abbreviationField.addNonEmptyValidator((a,b) -> onInputChanged(abbreviationField, a, b));
        abbreviationField.setEditorHint(R.string.hint_abbreviation);
    }

    private void onInputChanged(ConflictTextField conflictTextField, TextInputLayout textInputLayout, boolean isEmpty) {
        if (isEmpty) {
            invalidFields.add(conflictTextField);
            textInputLayout.setError(dictionary.apply(R.string.error_may_not_be_empty));
        } else {
            invalidFields.remove(conflictTextField);
            textInputLayout.setError(null);
        }
    }

    public boolean maySubmit() {
        return invalidFields.isEmpty();
    }

    public void setError(@StringRes int text) {
        for (ConflictTextField invalidField : invalidFields)
            invalidField.setError(dictionary.apply(text));
    }

    public String getName() {
        return nameField.get();
    }

    public String getAbbreviation() {
        return abbreviationField.get();
    }

    public void setName(String name) {
        nameField.setEditorContent(name);
    }

    public void setAbbreviation(String abbreviation) {
        abbreviationField.setEditorContent(abbreviation);
    }

    public void showNameConflict(ConflictData<String> name) {
        nameField.showConflictInfo(name);
    }

    public void hideName() {
        nameField.hide();
    }

    public void showAbbreviationConflict(ConflictData<String> abbreviation) {
        abbreviationField.showConflictInfo(abbreviation);
    }

    public void hideAbbreviation() {
        abbreviationField.hide();
    }
}
