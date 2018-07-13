package de.njsm.stocks.frontend.allfood;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import de.njsm.stocks.R;
import de.njsm.stocks.backend.network.AsyncTaskFactory;
import de.njsm.stocks.backend.network.NetworkManager;
import de.njsm.stocks.frontend.util.SwipeSyncCallback;

public class AllFoodActivity extends AppCompatActivity {

    public static final String KEY_EAN = "de.njsm.stocks.frontend.allfood.AllFoodActivity.ean";

    String ean;

    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_food);

        ean = getIntent().getExtras().getString(KEY_EAN);

        AsyncTaskFactory factory = new AsyncTaskFactory(this);
        NetworkManager networkManager = new NetworkManager(factory);

        SwipeRefreshLayout swiper = findViewById(R.id.all_food_swipe);
        swiper.setOnRefreshListener(new SwipeSyncCallback(swiper, networkManager));

    }

    @Override
    protected void onStart() {
        super.onStart();
        Fragment listFragment = AllFoodFragment.newInstance(ean);
        getFragmentManager().beginTransaction()
                .replace(R.id.content_all_food, listFragment)
                .commit();

    }
}
