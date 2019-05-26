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

package de.njsm.stocks.android.frontend.settings;


import android.content.Context;
import android.os.Bundle;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import dagger.android.support.AndroidSupportInjection;
import de.njsm.stocks.R;
import de.njsm.stocks.android.frontend.BaseFragment;
import de.njsm.stocks.android.frontend.util.RefreshViewModel;
import de.njsm.stocks.android.network.server.StatusCode;

import javax.inject.Inject;

public class SettingsFragment extends PreferenceFragmentCompat {

    private ViewModelProvider.Factory viewModelFactory;

    private RefreshViewModel refreshViewModel;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        refreshViewModel = ViewModelProviders.of(this, viewModelFactory).get(RefreshViewModel.class);

        Preference pref = getPreferenceManager().findPreference("pref_full_sync");
        if (pref != null)
                pref.setOnPreferenceClickListener(this::full_sync);

        pref = getPreferenceManager().findPreference("pref_crash_logs");
        if (pref != null)
                pref.setOnPreferenceClickListener(this::goToCrashLogs);
    }

    @Inject
    public void setViewModelFactory(ViewModelProvider.Factory viewModelFactory) {
        this.viewModelFactory = viewModelFactory;
    }

    private boolean full_sync(Preference preference) {
        LiveData<StatusCode> result = refreshViewModel.refreshComplete();
        result.observe(requireActivity(), data ->
                BaseFragment.maybeShowReadError(requireActivity(), data));
        return true;
    }

    private boolean goToCrashLogs(Preference preference) {
        Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
                .navigate(R.id.action_nav_fragment_settings_to_nav_fragment_crashlogs);
        return true;
    }

}
