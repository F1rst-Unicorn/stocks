package de.njsm.stocks.android.frontend.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.function.BiConsumer;

public class NonEmptyValidator implements TextWatcher {

    private final EditText view;

    private BiConsumer<EditText, Boolean> errorTextResourceSetter;

    public NonEmptyValidator(BiConsumer<EditText, Boolean> errorTextResourceSetter, EditText v) {
        this.errorTextResourceSetter = errorTextResourceSetter;
        this.view = v;
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
        errorTextResourceSetter.accept(view, input.isEmpty());
    }
}
