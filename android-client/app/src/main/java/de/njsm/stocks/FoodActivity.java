package de.njsm.stocks;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import de.njsm.stocks.backend.network.AsyncTaskCallback;
import de.njsm.stocks.backend.network.SyncTask;

public class FoodActivity extends AppCompatActivity implements
        SwipeRefreshLayout.OnRefreshListener,
        AsyncTaskCallback {

    public static final String KEY_ID = "de.njsm.stocks.FoodActivity.id";
    public static final String KEY_NAME = "de.njsm.stocks.FoodActivity.name";

    protected String mName;
    protected int mId;

    SwipeRefreshLayout mSwiper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        Bundle extras = getIntent().getExtras();
        mName = extras.getString(KEY_NAME);
        mId = extras.getInt(KEY_ID);

        mSwiper = (SwipeRefreshLayout) findViewById(R.id.food_swipe);
        mSwiper.setOnRefreshListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(mName);

        Fragment listFragment = FoodFragment.newInstance(mId);
        getFragmentManager().beginTransaction()
                .replace(R.id.food_content, listFragment)
                .commit();

    }

    @Override
    public void onRefresh() {
        SyncTask task = new SyncTask(this, this);
        task.execute();
    }

    @Override
    public void onAsyncTaskStart() {
        mSwiper.setRefreshing(true);
    }

    @Override
    public void onAsyncTaskComplete() {
        mSwiper.setRefreshing(false);
    }
}
