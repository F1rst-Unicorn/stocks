package de.njsm.stocks;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;

import de.njsm.stocks.backend.network.AsyncTaskCallback;
import de.njsm.stocks.backend.network.SwipeSyncCallback;
import de.njsm.stocks.backend.network.SyncTask;

public class EmptyFoodActivity extends AppCompatActivity {

    protected SwipeRefreshLayout mSwiper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_food);

        mSwiper = (SwipeRefreshLayout) findViewById(R.id.empty_food_swipe);
        mSwiper.setOnRefreshListener(new SwipeSyncCallback(mSwiper, this));

        Fragment listFragment = new EmptyFoodFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.content_empty_food, listFragment)
                .commit();

    }
}
