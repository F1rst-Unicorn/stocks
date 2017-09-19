package de.njsm.stocks.frontend.location;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import de.njsm.stocks.R;
import de.njsm.stocks.backend.network.AsyncTaskFactory;
import de.njsm.stocks.backend.network.NetworkManager;
import de.njsm.stocks.frontend.util.SwipeSyncCallback;

public class LocationActivity extends AppCompatActivity {

    public static final String KEY_LOCATION_ID = "de.njsm.stocks.frontend.location.LocationActivity.id";
    public static final String KEY_LOCATION_NAME = "de.njsm.stocks.frontend.location.LocationActivity.name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        String location = getIntent().getExtras().getString(KEY_LOCATION_NAME);
        int id = getIntent().getExtras().getInt(KEY_LOCATION_ID);

        AsyncTaskFactory factory = new AsyncTaskFactory(this);
        NetworkManager networkManager = new NetworkManager(factory);


        SwipeRefreshLayout swiper = (SwipeRefreshLayout) findViewById(R.id.location_swipe);
        swiper.setOnRefreshListener(new SwipeSyncCallback(swiper, networkManager));

        setTitle(location);

        Fragment listFragment = FoodListFragment.newInstance(id);
        getFragmentManager().beginTransaction()
                .replace(R.id.location_content, listFragment)
                .commit();

    }
}
