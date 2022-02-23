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

package de.njsm.stocks.client.view;

import android.view.View;
import android.widget.EditText;
import com.google.android.material.textfield.TextInputLayout;
import de.njsm.stocks.client.business.entities.RegistrationForm;
import de.njsm.stocks.client.ui.R;

class SetupFormView {

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

    SetupFormView(View root) {
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

    private void setText(TextInputLayout view, int text) {
        setText(view, String.valueOf(text));
    }

    private void setText(TextInputLayout inputField, String text) {
        EditText editor = inputField.getEditText();
        if (editor != null) {
            editor.setText(text);
        }
    }

    private int intFromForm(TextInputLayout view) {
        String rawNumber = stringFromForm(view);
        try {
            return Integer.parseInt(rawNumber);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String stringFromForm(TextInputLayout view) {
        EditText editText = view.getEditText();
        if (editText != null) {
            return editText.getText().toString();
        } else {
            return "";
        }
    }
}
