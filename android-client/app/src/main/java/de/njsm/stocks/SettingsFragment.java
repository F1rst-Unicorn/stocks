package de.njsm.stocks;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;
import de.njsm.stocks.backend.db.DatabaseHandler;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

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
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch(preference.getKey()) {
            case "pref_full_sync":
                DatabaseHandler handler = new DatabaseHandler(getActivity());
                handler.onUpgrade(handler.getWritableDatabase(), -1, -1);
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
