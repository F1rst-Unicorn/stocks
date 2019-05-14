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
