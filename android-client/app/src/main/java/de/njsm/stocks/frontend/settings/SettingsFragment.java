package de.njsm.stocks.frontend.settings;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;
import de.njsm.stocks.R;
import de.njsm.stocks.backend.db.DatabaseHandler;
import de.njsm.stocks.backend.network.AsyncTaskFactory;
import de.njsm.stocks.backend.network.NetworkManager;
import de.njsm.stocks.frontend.crashlog.CrashLogActivity;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    private NetworkManager networkManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        super.getPreferenceManager()
                .findPreference("pref_full_sync")
                .setOnPreferenceClickListener(this);
        super.getPreferenceManager()
                .findPreference("pref_crash_logs")
                .setOnPreferenceClickListener(this);
        AsyncTaskFactory factory = new AsyncTaskFactory(getActivity());
        networkManager = new NetworkManager(factory);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch(preference.getKey()) {
            case "pref_full_sync":
                DatabaseHandler handler = new DatabaseHandler(getActivity());
                handler.onUpgrade(handler.getWritableDatabase(), -1, -1);
                networkManager.synchroniseData();
                Toast.makeText(
                        getActivity(),
                        getResources().getString(R.string.dialog_new_sync_done),
                        Toast.LENGTH_SHORT
                ).show();
                break;
            case "pref_crash_logs":
                Intent i = new Intent(this.getActivity(), CrashLogActivity.class);
                startActivity(i);
                break;
        }
        return true;
    }
}
