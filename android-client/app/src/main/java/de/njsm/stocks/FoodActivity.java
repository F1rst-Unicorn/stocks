package de.njsm.stocks;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import de.njsm.stocks.backend.network.SwipeSyncCallback;

public class FoodActivity extends AppCompatActivity {

    public static final String KEY_ID = "de.njsm.stocks.FoodActivity.id";
    public static final String KEY_NAME = "de.njsm.stocks.FoodActivity.name";

    protected String mName;
    protected int mId;

    SwipeRefreshLayout mSwiper;
    Fragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        Bundle extras = getIntent().getExtras();
        mName = extras.getString(KEY_NAME);
        mId = extras.getInt(KEY_ID);

        mSwiper = (SwipeRefreshLayout) findViewById(R.id.food_swipe);
        mSwiper.setOnRefreshListener(new SwipeSyncCallback(mSwiper, this));

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_food_toolbar);
        setSupportActionBar(toolbar);
        setTitle(mName);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mFragment = FoodFragment.newInstance(mId);
        getFragmentManager().beginTransaction()
                .replace(R.id.food_content, mFragment)
                .commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFragment = null;
    }


    public void addItem(View view) {

        Intent i = new Intent(this, AddFoodItemActivity.class);
        Bundle extras = new Bundle();
        extras.putInt(AddFoodItemActivity.KEY_ID, mId);
        extras.putString(AddFoodItemActivity.KEY_FOOD, mName);
        i.putExtras(extras);
        startActivity(i);
    }
}
