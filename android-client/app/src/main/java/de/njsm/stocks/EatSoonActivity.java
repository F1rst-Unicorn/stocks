package de.njsm.stocks;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import de.njsm.stocks.backend.network.AsyncTaskFactory;
import de.njsm.stocks.backend.network.NetworkManager;
import de.njsm.stocks.backend.util.SwipeSyncCallback;

public class EatSoonActivity extends AppCompatActivity {

    protected SwipeRefreshLayout mSwiper;
    private NetworkManager networkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eat_soon);

        AsyncTaskFactory factory = new AsyncTaskFactory(this);
        networkManager = new NetworkManager(factory);
        factory.setNetworkManager(networkManager);


        mSwiper = (SwipeRefreshLayout) findViewById(R.id.eat_soon_swipe);
        mSwiper.setOnRefreshListener(new SwipeSyncCallback(mSwiper, networkManager));

        Fragment listFragment = new EatSoonFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.content_eat_soon, listFragment)
                .commit();

    }
}
