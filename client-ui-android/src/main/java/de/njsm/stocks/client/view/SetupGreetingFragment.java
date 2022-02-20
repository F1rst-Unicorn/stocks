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
 */

package de.njsm.stocks.client.view;

import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.google.zxing.integration.android.IntentIntegrator;
import de.njsm.stocks.client.business.entities.RegistrationForm;
import de.njsm.stocks.client.navigation.SetupGreetingNavigator;
import de.njsm.stocks.client.ui.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class SetupGreetingFragment extends InjectableFragment implements CameraPermissionProber {

    private static final Logger LOG = LoggerFactory.getLogger(SetupGreetingFragment.class);

    private SetupGreetingNavigator navigator;

    private QrCodeDataBroadcastReceiver receiver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_setup_greeting, container, false);
        result.findViewById(R.id.fragment_setup_greeting_manual).setOnClickListener(this::onManualSetupClicked);
        result.findViewById(R.id.fragment_setup_greeting_scan).setOnClickListener(this::onScanButtonClicked);

        receiver = new QrCodeDataBroadcastReceiver(this);
        IntentFilter filter = new IntentFilter(QrCodeDataBroadcastReceiver.ACTION_QR_CODE_SCANNED);
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(receiver, filter);

        return result;
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiver);
        super.onDestroy();
    }

    private void onScanButtonClicked(View view) {
        if (probeForCameraPermission()) {
            LOG.info("Starting QR code reader");
            IntentIntegrator integrator = new IntentIntegrator(getActivity());
            integrator.initiateScan();
        }
    }

    private void onManualSetupClicked(View view) {
        navigator.registerManually();
    }

    @Inject
    public void setNavigator(SetupGreetingNavigator navigator) {
        this.navigator = navigator;
    }

    public void getQrResult(String rawData) {
        RegistrationForm registrationForm = RegistrationForm.parseRawString(rawData);
        navigator.registerWithPrefilledData(registrationForm);
    }
}
