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

package de.njsm.stocks.android.frontend.setup;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import de.njsm.stocks.R;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.frontend.util.NonEmptyValidator;
import de.njsm.stocks.android.util.Logger;

import java.util.HashMap;
import java.util.Map;

public class PrincipalsFragment extends BaseFragment {

    private static final Logger LOG = new Logger(PrincipalsFragment.class);

    private PrincipalsFragmentArgs input;

    private EditText serverUrl;

    private EditText caPort;

    private EditText sentryPort;

    private EditText serverPort;

    private EditText userName;

    private EditText userId;

    private EditText deviceName;

    private EditText deviceId;

    private EditText fingerprint;

    private EditText ticket;

    private Map<EditText, Boolean> isTextFieldEmpty;

    private Button next;

    private Button back;

    public PrincipalsFragment() {
        isTextFieldEmpty = new HashMap<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_principals, container, false);
        assert getArguments() != null;
        input = PrincipalsFragmentArgs.fromBundle(getArguments());

        serverUrl = root.findViewById(R.id.fragment_principals_server_url);
        caPort = root.findViewById(R.id.fragment_principals_ca_port);
        sentryPort = root.findViewById(R.id.fragment_principals_sentry_port);
        serverPort = root.findViewById(R.id.fragment_principals_server_port);
        userName = root.findViewById(R.id.fragment_principals_user_name);
        userId = root.findViewById(R.id.fragment_principals_user_id);
        deviceName = root.findViewById(R.id.fragment_principals_device_name);
        deviceId = root.findViewById(R.id.fragment_principals_device_id);
        fingerprint = root.findViewById(R.id.fragment_principals_fingerprint);
        ticket = root.findViewById(R.id.fragment_principals_ticket);
        next = root.findViewById(R.id.fragment_principals_button);
        back = root.findViewById(R.id.fragment_principals_button_back);

        if (input.getUsername() != null) {
            LOG.d("Got input from qr fragment");
            serverUrl.setText(input.getServerUrl());
            caPort.setText(String.valueOf(input.getCaPort()));
            sentryPort.setText(String.valueOf(input.getSentryPort()));
            serverPort.setText(String.valueOf(input.getServerPort()));
            userName.setText(input.getUsername());
            deviceName.setText(input.getDeviceName());
            fingerprint.setText(input.getFingerprint());
            ticket.setText(input.getTicket());
            userId.setText(String.valueOf(input.getUserId()));
            deviceId.setText(String.valueOf(input.getDeviceId()));
        } else {
            LOG.d("Requiring user input");
            caPort.setText("10910");
            sentryPort.setText("10911");
            serverPort.setText("10912");
        }

        initTextFieldEmptyMap();

        serverUrl.addTextChangedListener(new NonEmptyValidator(serverUrl, this::invalidateButton));
        caPort.addTextChangedListener(new NonEmptyValidator(caPort, this::invalidateButton));
        sentryPort.addTextChangedListener(new NonEmptyValidator(sentryPort, this::invalidateButton));
        serverPort.addTextChangedListener(new NonEmptyValidator(serverPort, this::invalidateButton));
        userName.addTextChangedListener(new NonEmptyValidator(userName, this::invalidateButton));
        userId.addTextChangedListener(new NonEmptyValidator(userId, this::invalidateButton));
        deviceName.addTextChangedListener(new NonEmptyValidator(deviceName, this::invalidateButton));
        deviceId.addTextChangedListener(new NonEmptyValidator(deviceId, this::invalidateButton));
        fingerprint.addTextChangedListener(new NonEmptyValidator(fingerprint, this::invalidateButton));
        ticket.addTextChangedListener(new NonEmptyValidator(ticket, this::invalidateButton));

        next.setOnClickListener(this::next);
        back.setOnClickListener(this::back);

        requireActivity().setTitle(R.string.title_principals);

        return root;
    }

    private void initTextFieldEmptyMap() {
        checkTextFieldContent(serverUrl);
        checkTextFieldContent(caPort);
        checkTextFieldContent(sentryPort);
        checkTextFieldContent(serverPort);
        checkTextFieldContent(userName);
        checkTextFieldContent(deviceName);
        checkTextFieldContent(userId);
        checkTextFieldContent(deviceId);
        checkTextFieldContent(fingerprint);
        checkTextFieldContent(ticket);
        updateNextButton();
    }

    private void checkTextFieldContent(EditText textField) {
        String content = textField.getText().toString();
        boolean value = content == null || content.isEmpty();
        isTextFieldEmpty.put(textField, value);
    }

    private void invalidateButton(EditText view, Boolean isEmpty) {
        isTextFieldEmpty.put(view, isEmpty);
        if (isEmpty) {
            String error = requireActivity().getResources().getString(R.string.error_may_not_be_empty);
            view.setError(error);
        } else {
            view.setError(null);
        }

        updateNextButton();
    }

    private void updateNextButton() {
        boolean largeEnough = isTextFieldEmpty.size() == 10;
        boolean noneEmpty = isTextFieldEmpty.values().stream().noneMatch(v -> v);
        boolean enabled = largeEnough && noneEmpty;
        next.setEnabled(enabled);
    }

    private void back(View view) {
        PrincipalsFragmentDirections.ActionNavFragmentPrincipalsToNavFragmentQr args =
                PrincipalsFragmentDirections.actionNavFragmentPrincipalsToNavFragmentQr(null);
        Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment).navigate(args);
    }

    private void next(View view) {
        PrincipalsFragmentDirections.ActionNavFragmentPrincipalsToNavFragmentStartup args =
                PrincipalsFragmentDirections.actionNavFragmentPrincipalsToNavFragmentStartup()
                .setServerUrl(serverUrl.getText().toString())
                .setCaPort(Integer.parseInt(caPort.getText().toString()))
                .setSentryPort(Integer.parseInt(sentryPort.getText().toString()))
                .setServerPort(Integer.parseInt(serverPort.getText().toString()))
                .setUsername(userName.getText().toString())
                .setUserId(Integer.parseInt(userId.getText().toString()))
                .setDeviceName(deviceName.getText().toString())
                .setDeviceId(Integer.parseInt(deviceId.getText().toString()))
                .setFingerprint(fingerprint.getText().toString())
                .setTicket(ticket.getText().toString());
        Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment).navigate(args);
    }
}
