package de.njsm.stocks.frontend.crashlog;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import de.njsm.stocks.R;

public class CrashLogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        setTitle(R.string.title_crash_logs);

        Fragment listFragment = new CrashLogListFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.location_content, listFragment)
                .commit();

    }
}