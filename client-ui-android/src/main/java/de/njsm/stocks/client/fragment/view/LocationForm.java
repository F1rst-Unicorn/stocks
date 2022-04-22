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
import android.widget.EditText;
import androidx.annotation.StringRes;
import com.google.android.material.textfield.TextInputLayout;
import de.njsm.stocks.client.ui.R;
import de.njsm.stocks.client.util.NonEmptyValidator;

import java.util.function.Function;

import static de.njsm.stocks.client.fragment.view.ViewUtility.setText;
import static de.njsm.stocks.client.fragment.view.ViewUtility.stringFromForm;

public class LocationForm {

    private TextInputLayout nameField;

    private TextInputLayout descriptionField;

    private final Function<Integer, String> stringResourceLookup;

    private boolean maySubmit = false;

    public LocationForm(View root, Function<Integer, String> stringResourceLookup) {
        this.nameField = root.findViewById(R.id.fragment_location_form_name);
        this.descriptionField = root.findViewById(R.id.fragment_location_form_description);
        this.stringResourceLookup = stringResourceLookup;

        EditText editText = nameField.getEditText();
        if (editText != null)
            editText.addTextChangedListener(new NonEmptyValidator(nameField, this::onNameChanged));
    }

    public void setName(String text) {
        setText(nameField, text);
    }

    public void setNameError(@StringRes int message) {
        nameField.setError(stringResourceLookup.apply(message));
    }

    public void setDescription(String text) {
        setText(descriptionField, text);
    }

    public boolean maySubmit() {
        return maySubmit;
    }

    public String getName() {
        return stringFromForm(nameField);
    }

    public String getDescription() {
        return stringFromForm(descriptionField);
    }

    private void onNameChanged(TextInputLayout textInputLayout, Boolean isEmpty) {
        maySubmit = !isEmpty;
        if (isEmpty)
            textInputLayout.setError(stringResourceLookup.apply(R.string.error_may_not_be_empty));
        else
            textInputLayout.setError(null);
    }
}
