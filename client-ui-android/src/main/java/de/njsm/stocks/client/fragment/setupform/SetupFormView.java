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

package de.njsm.stocks.client.fragment.setupform;

import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.StringRes;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import de.njsm.stocks.client.business.entities.RegistrationForm;
import de.njsm.stocks.client.ui.R;
import de.njsm.stocks.client.util.NonEmptyValidator;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static de.njsm.stocks.client.fragment.view.ViewUtility.*;

class SetupFormView {

    private final ScrollView scrollView;

    private final TextInputLayout serverName;

    private final TextInputLayout caPort;

    private final TextInputLayout registrationPort;

    private final TextInputLayout serverPort;

    private final TextInputLayout userName;

    private final TextInputLayout userId;

    private final TextInputLayout userDeviceName;

    private final TextInputLayout userDeviceId;

    private final TextInputLayout fingerprint;

    private final TextInputLayout ticket;

    private final Button submitButton;

    private final TextView status;

    private final CircularProgressIndicator progressIndicator;

    private final Map<TextInputLayout, Boolean> isTextFieldEmpty;

    private final Function<Integer, String> stringResourceLookup;

    SetupFormView(View root, Function<Integer, String> stringResourceLookup) {
        scrollView = root.findViewById(R.id.fragment_setup_form_scroll_view);
        serverName = root.findViewById(R.id.fragment_setup_form_server_name);
        caPort = root.findViewById(R.id.fragment_setup_form_ca_port);
        registrationPort = root.findViewById(R.id.fragment_setup_form_registration_port);
        serverPort = root.findViewById(R.id.fragment_setup_form_server_port);
        userName = root.findViewById(R.id.fragment_setup_form_user_name);
        userId = root.findViewById(R.id.fragment_setup_form_user_id);
        userDeviceName = root.findViewById(R.id.fragment_setup_form_device_name);
        userDeviceId = root.findViewById(R.id.fragment_setup_form_device_id);
        fingerprint = root.findViewById(R.id.fragment_setup_form_fingerprint);
        ticket = root.findViewById(R.id.fragment_setup_form_ticket);
        submitButton = root.findViewById(R.id.fragment_setup_form_button);
        status = root.findViewById(R.id.fragment_setup_form_status);
        progressIndicator = root.findViewById(R.id.fragment_setup_form_progress);

        this.stringResourceLookup = stringResourceLookup;
        isTextFieldEmpty = new HashMap<>();

        initialiseEmptyFieldsMap();
        addTextChangeListeners();
    }

    private void addTextChangeListeners() {
        addTextChangeListener(serverName);
        addTextChangeListener(caPort);
        addTextChangeListener(registrationPort);
        addTextChangeListener(serverPort);
        addTextChangeListener(userName);
        addTextChangeListener(userId);
        addTextChangeListener(userDeviceName);
        addTextChangeListener(userDeviceId);
        addTextChangeListener(fingerprint);
        addTextChangeListener(ticket);
    }

    private void addTextChangeListener(TextInputLayout textInputLayout) {
        onEditorOf(textInputLayout, e -> e.addTextChangedListener(new NonEmptyValidator(textInputLayout, this::onFormFieldInput)));
    }

    void initialiseForm(RegistrationForm data) {
        setText(serverName, data.serverName());
        setText(caPort, data.caPort());
        setText(registrationPort, data.registrationPort());
        setText(serverPort, data.serverPort());
        setText(userName, data.userName());
        setText(userId, data.userId());
        setText(userDeviceName, data.userDeviceName());
        setText(userDeviceId, data.userDeviceId());
        setText(fingerprint, data.fingerprint());
        setText(ticket, data.ticket());
    }

    RegistrationForm getFormData() {
        return RegistrationForm.builder()
                .serverName(stringFromForm(serverName))
                .caPort(intFromForm(caPort))
                .registrationPort(intFromForm(registrationPort))
                .serverPort(intFromForm(serverPort))
                .userName(stringFromForm(userName))
                .userId(intFromForm(userId))
                .userDeviceName(stringFromForm(userDeviceName))
                .userDeviceId(intFromForm(userDeviceId))
                .fingerprint(stringFromForm(fingerprint))
                .ticket(stringFromForm(ticket))
                .build();
    }

    private void initialiseEmptyFieldsMap() {
        checkTextFieldContent(serverName);
        checkTextFieldContent(caPort);
        checkTextFieldContent(registrationPort);
        checkTextFieldContent(serverPort);
        checkTextFieldContent(userName);
        checkTextFieldContent(userDeviceName);
        checkTextFieldContent(userId);
        checkTextFieldContent(userDeviceId);
        checkTextFieldContent(fingerprint);
        checkTextFieldContent(ticket);
        enableSubmitButtonIfEligible();
    }

    private void checkTextFieldContent(TextInputLayout textInputLayout) {
        onEditorOf(textInputLayout, e -> isTextFieldEmpty.put(textInputLayout, e.getText().length() == 0));
    }

    private void onFormFieldInput(TextInputLayout view, boolean isEmpty) {
        isTextFieldEmpty.put(view, isEmpty);
        if (isEmpty) {
            String error = stringResourceLookup.apply(R.string.error_may_not_be_empty);
            view.setError(error);
        } else {
            view.setError(null);
        }

        enableSubmitButtonIfEligible();
    }

    private void enableSubmitButtonIfEligible() {
        boolean largeEnough = isTextFieldEmpty.size() == 10;
        boolean noneEmpty = isTextFieldEmpty.values().stream().noneMatch(v -> v);
        submitButton.setEnabled(largeEnough && noneEmpty);
    }

    private int intFromForm(TextInputLayout view) {
        String rawNumber = stringFromForm(view);
        try {
            return Integer.parseInt(rawNumber);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    void bindSubmitButton(View.OnClickListener onSubmitForm) {
        submitButton.setOnClickListener(onSubmitForm);
    }

    void setProgressing(@StringRes int message) {
        status.setText(message);
        status.setVisibility(View.VISIBLE);
        progressIndicator.setVisibility(View.VISIBLE);
        submitButton.setVisibility(View.GONE);
    }

    void setError(@StringRes int message) {
        status.setText(message);
        status.setVisibility(View.VISIBLE);
        progressIndicator.setVisibility(View.GONE);
        submitButton.setVisibility(View.VISIBLE);
        submitButton.setText(R.string.dialog_retry);
        scrollView.post(() -> scrollView.smoothScrollTo(scrollView.getScrollX(), scrollView.getBottom()));
    }
}
