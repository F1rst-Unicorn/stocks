package de.njsm.stocks.frontend.emptyfood;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import de.njsm.stocks.R;
import de.njsm.stocks.backend.network.AsyncTaskFactory;
import de.njsm.stocks.backend.network.NetworkManager;
import de.njsm.stocks.frontend.util.SwipeSyncCallback;

public class EmptyFoodActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_food);

        AsyncTaskFactory factory = new AsyncTaskFactory(this);
        NetworkManager networkManager = new NetworkManager(factory);

        SwipeRefreshLayout swiper = (SwipeRefreshLayout) findViewById(R.id.empty_food_swipe);
        swiper.setOnRefreshListener(new SwipeSyncCallback(swiper, networkManager));

        Fragment listFragment = new EmptyFoodFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.content_empty_food, listFragment)
                .commit();

    }
}
