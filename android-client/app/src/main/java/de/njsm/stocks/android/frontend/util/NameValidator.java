/* stocks is client-server program to manage a household's food stock
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
 */

package de.njsm.stocks.android.frontend.util;

import android.text.Editable;
import android.text.TextWatcher;
import androidx.core.util.Consumer;
import de.njsm.stocks.R;
import de.njsm.stocks.android.util.Principals;

public class NameValidator implements TextWatcher {

    private Consumer<Integer> errorTextResourceSetter;

    public NameValidator(Consumer<Integer> errorTextResourceSetter) {
        this.errorTextResourceSetter = errorTextResourceSetter;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String input = s.toString();
        if (! Principals.isNameValid(input)) {
            errorTextResourceSetter.accept(R.string.error_wrong_name_format);
        }
    }
}
