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
import de.njsm.stocks.client.util.NonEmptyValidator;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static de.njsm.stocks.client.fragment.view.ViewUtility.*;

public class UnitForm {

    private final TextInputLayout nameField;

    private final TextInputLayout abbreviationField;

    private final Function<Integer, String> stringProvider;

    private final Set<TextInputLayout> invalidFields;

    public UnitForm(View root, Function<Integer, String> stringProvider) {
        this.nameField = root.findViewById(R.id.fragment_unit_form_name);
        this.abbreviationField = root.findViewById(R.id.fragment_unit_form_abbreviation);
        this.stringProvider = stringProvider;
        invalidFields = new HashSet<>();
        invalidFields.add(nameField);
        invalidFields.add(abbreviationField);

        onEditorOf(nameField, e -> e.addTextChangedListener(new NonEmptyValidator(nameField, this::onInputChanged)));
        onEditorOf(abbreviationField, e -> e.addTextChangedListener(new NonEmptyValidator(abbreviationField, this::onInputChanged)));
    }

    private void onInputChanged(TextInputLayout textInputLayout, boolean isEmpty) {
        if (isEmpty) {
            invalidFields.add(textInputLayout);
            textInputLayout.setError(stringProvider.apply(R.string.error_may_not_be_empty));
        } else {
            invalidFields.remove(textInputLayout);
            textInputLayout.setError(null);
        }
    }

    public boolean maySubmit() {
        return invalidFields.isEmpty();
    }

    public void setError(@StringRes int text) {
        for (TextInputLayout invalidField : invalidFields)
            invalidField.setError(stringProvider.apply(text));
    }

    public String getName() {
        return stringFromForm(nameField);
    }

    public String getAbbreviation() {
        return stringFromForm(abbreviationField);
    }

    public void setName(String name) {
        setText(nameField, name);
    }

    public void setAbbreviation(String abbreviation) {
        setText(abbreviationField, abbreviation);
    }
}
