package de.njsm.stocks.frontend;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import de.njsm.stocks.R;
import de.njsm.stocks.backend.network.AsyncTaskFactory;
import de.njsm.stocks.backend.network.NetworkManager;
import de.njsm.stocks.backend.util.SwipeSyncCallback;

public class EatSoonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eat_soon);

        AsyncTaskFactory factory = new AsyncTaskFactory(this);
        NetworkManager networkManager = new NetworkManager(factory);

        SwipeRefreshLayout swiper = (SwipeRefreshLayout) findViewById(R.id.eat_soon_swipe);
        swiper.setOnRefreshListener(new SwipeSyncCallback(swiper, networkManager));

        Fragment listFragment = new EatSoonFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.content_eat_soon, listFragment)
                .commit();

    }
}
