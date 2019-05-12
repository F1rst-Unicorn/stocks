package de.njsm.stocks.android.frontend.settings;


import android.content.Context;
import android.os.Bundle;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
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
        return true;
    }

}
