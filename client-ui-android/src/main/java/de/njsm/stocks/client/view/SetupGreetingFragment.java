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

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import de.njsm.stocks.client.navigation.SetupGreetingNavigator;
import de.njsm.stocks.client.ui.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class SetupGreetingFragment extends InjectableFragment implements CameraPermissionProber {

    private static final Logger LOG = LoggerFactory.getLogger(SetupGreetingFragment.class);

    private SetupGreetingNavigator navigator;

    private final ActivityResultLauncher<Activity> qrScanOperation;

    public SetupGreetingFragment() {
        this.qrScanOperation = registerForActivityResult(new ScanRegistrationFormContract(),
                v -> v.ifPresent(navigator::registerWithPrefilledData));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_setup_greeting, container, false);
        result.findViewById(R.id.fragment_setup_greeting_manual).setOnClickListener(this::onManualSetupClicked);
        result.findViewById(R.id.fragment_setup_greeting_scan).setOnClickListener(this::onScanButtonClicked);

        return result;
    }

    private void onScanButtonClicked(View view) {
        if (probeForCameraPermission()) {
            qrScanOperation.launch(requireActivity());
        }
    }

    private void onManualSetupClicked(View view) {
        navigator.registerManually();
    }

    @Inject
    public void setNavigator(SetupGreetingNavigator navigator) {
        this.navigator = navigator;
    }
}
