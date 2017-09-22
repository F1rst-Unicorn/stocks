package de.njsm.stocks.frontend.search;

import android.app.Fragment;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import de.njsm.stocks.R;

public class SearchActivity extends AppCompatActivity {

    public static final ComponentName NAME = new ComponentName("de.njsm.stocks.frontend.search",
            "SearchActivity");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Fragment fragment = SearchFragment.newInstance(query);
            getFragmentManager().beginTransaction()
                    .replace(R.id.search_content, fragment)
                    .commit();

        }
    }
}
