package de.njsm.stocks;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import de.njsm.stocks.backend.network.AsyncTaskFactory;
import de.njsm.stocks.backend.network.NetworkManager;
import de.njsm.stocks.backend.network.SwipeSyncCallback;

public class LocationActivity extends AppCompatActivity {

    public static final String KEY_LOCATION_ID = "de.njsm.stocks.LocationActivity.id";
    public static final String KEY_LOCATION_NAME = "de.njsm.stocks.LocationActivity.name";

    protected String mLocation;
    protected int mId;

    protected SwipeRefreshLayout mSwiper;
    private NetworkManager networkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        mLocation = getIntent().getExtras().getString(KEY_LOCATION_NAME);
        mId = getIntent().getExtras().getInt(KEY_LOCATION_ID);

        AsyncTaskFactory factory = new AsyncTaskFactory(this);
        networkManager = new NetworkManager(factory);
        factory.setNetworkManager(networkManager);


        mSwiper = (SwipeRefreshLayout) findViewById(R.id.location_swipe);
        mSwiper.setOnRefreshListener(new SwipeSyncCallback(mSwiper, networkManager));

        setTitle(mLocation);

        Fragment listFragment = FoodListFragment.newInstance(mId);
        getFragmentManager().beginTransaction()
                .replace(R.id.location_content, listFragment)
                .commit();

    }
}
