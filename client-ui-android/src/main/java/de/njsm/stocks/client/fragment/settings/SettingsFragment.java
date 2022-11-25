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

package de.njsm.stocks.client.fragment.settings;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import dagger.android.support.AndroidSupportInjection;
import de.njsm.stocks.client.navigation.SettingsNavigator;
import de.njsm.stocks.client.presenter.SettingsViewModel;
import de.njsm.stocks.client.ui.R;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat {

    private SettingsNavigator navigator;

    private SettingsViewModel viewModel;

    private EditTextPreference serverName;

    private EditTextPreference caPort;

    private EditTextPreference registrationPort;

    private EditTextPreference serverPort;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        serverName = findPreference("pref_server");
        caPort = findPreference("pref_ca_port");
        registrationPort = findPreference("pref_registration_port");
        serverPort = findPreference("pref_server_port");

        Objects.requireNonNull(serverName);
        Objects.requireNonNull(caPort);
        Objects.requireNonNull(registrationPort);
        Objects.requireNonNull(serverPort);

        serverName.setOnPreferenceChangeListener((p, v) -> {
            if (v instanceof String) {
                String name = (String) v;
                viewModel.updateServerName(name);
                p.setSummary(name);
                return true;
            }
            return false;
        });

        caPort.setOnPreferenceChangeListener((PortChangedHandler)
                port -> viewModel.updateCaPort(port));

        registrationPort.setOnPreferenceChangeListener((PortChangedHandler)
                port -> viewModel.updateRegistrationPort(port));

        serverPort.setOnPreferenceChangeListener((PortChangedHandler)
                port -> viewModel.updateServerPort(port));

        findPreference("pref_full_sync").setOnPreferenceClickListener(v -> {
            viewModel.performFullSync();
            return true;
        });

        findPreference("pref_crash_logs").setOnPreferenceClickListener(v -> {
            navigator.showCrashLogs();
            return true;
        });

        findPreference("pref_clear_search_history").setOnPreferenceClickListener(v -> {
            viewModel.clearSearchHistory();
            return true;
        });
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        viewModel.getSettings().observe(getViewLifecycleOwner(), v -> {
            serverName.setSummary(v.serverName());
            serverName.setText(v.serverName());
            caPort.setSummary(String.valueOf(v.caPort()));
            caPort.setText(String.valueOf(v.caPort()));
            registrationPort.setSummary(String.valueOf(v.registrationPort()));
            registrationPort.setText(String.valueOf(v.registrationPort()));
            serverPort.setSummary(String.valueOf(v.serverPort()));
            serverPort.setText(String.valueOf(v.serverPort()));
        });
        return view;
    }

    private interface PortChangedHandler extends Preference.OnPreferenceChangeListener {

        void notifyViewModel(int port);

        @Override
        default boolean onPreferenceChange(@NotNull Preference p, Object v) {
            if (v instanceof String) {
                String value = (String) v;
                int port;
                try {
                    port = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    return false;
                }
                notifyViewModel(port);
                p.setSummary(String.valueOf(port));
                return true;
            }
            return false;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Inject
    void setNavigator(SettingsNavigator navigator) {
        this.navigator = navigator;
    }

    @Inject
    protected void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        viewModel = viewModelProvider.get(SettingsViewModel.class);
    }
}
