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
import com.google.android.material.textfield.TextInputLayout;
import de.njsm.stocks.client.ui.R;

import java.util.function.Function;

public class UserForm {

    private final ConflictTextField nameField;

    private final Function<Integer, String> dictionary;

    private boolean isEmpty = true;

    private boolean containsInvalidCharacter = true;

    public UserForm(View root, Function<Integer, String> dictionary) {
        this.nameField = new ConflictTextField(root.findViewById(R.id.fragment_user_form_name));
        this.dictionary = dictionary;

        nameField.addNonEmptyValidator(this::onNameChanged);
        nameField.addPrincipalNameValidator(this::onPrincipalNameChanged);
        nameField.setEditorHint(R.string.hint_name);
    }

    private void onNameChanged(TextInputLayout textInputLayout, boolean isEmpty) {
        this.isEmpty = isEmpty;
        if (isEmpty)
            textInputLayout.setError(dictionary.apply(R.string.error_may_not_be_empty));
        else if (maySubmit())
            textInputLayout.setError(null);
    }

    private void onPrincipalNameChanged(TextInputLayout textInputLayout, boolean containsInvalidCharacter) {
        this.containsInvalidCharacter = containsInvalidCharacter;
        if (containsInvalidCharacter)
            textInputLayout.setError(dictionary.apply(R.string.error_wrong_name_format));
        else if (maySubmit())
            textInputLayout.setError(null);
    }

    public boolean maySubmit() {
        return !isEmpty && !containsInvalidCharacter;
    }

    public void showErrors() {
        nameField.setError(dictionary.apply(R.string.error_may_not_be_empty));
    }

    public String getName() {
        return nameField.get();
    }
}
