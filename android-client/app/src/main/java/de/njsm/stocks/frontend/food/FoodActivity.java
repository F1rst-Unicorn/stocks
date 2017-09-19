package de.njsm.stocks.frontend.food;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import de.njsm.stocks.R;
import de.njsm.stocks.backend.network.AsyncTaskFactory;
import de.njsm.stocks.backend.network.NetworkManager;
import de.njsm.stocks.frontend.util.SwipeSyncCallback;
import de.njsm.stocks.common.data.Food;
import de.njsm.stocks.frontend.addfood.AddFoodItemActivity;

public class FoodActivity extends AppCompatActivity {

    public static final String KEY_ID = "de.njsm.stocks.frontend.food.FoodActivity.id";
    public static final String KEY_NAME = "de.njsm.stocks.frontend.food.FoodActivity.name";

    private String name;
    private int id;

    private Fragment mFragment;
    private NetworkManager networkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        Bundle extras = getIntent().getExtras();
        name = extras.getString(KEY_NAME);
        id = extras.getInt(KEY_ID);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_food_toolbar);
        setSupportActionBar(toolbar);
        setTitle(name);

        AsyncTaskFactory factory = new AsyncTaskFactory(this);
        networkManager = new NetworkManager(factory);

        SwipeRefreshLayout swiper = (SwipeRefreshLayout) findViewById(R.id.food_swipe);
        swiper.setOnRefreshListener(new SwipeSyncCallback(swiper, networkManager));

    }

    @Override
    protected void onStart() {
        super.onStart();
        mFragment = FoodFragment.newInstance(id);
        getFragmentManager().beginTransaction()
                .replace(R.id.food_content, mFragment)
                .commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFragment = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_food_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.activity_food_menu_delete:
                String message = String.format(getResources().getString(R.string.dialog_delete_food),
                        getTitle());
                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.title_delete_food))
                        .setMessage(message)
                        .setPositiveButton(getResources().getString(android.R.string.yes), (DialogInterface dialog, int whichButton) -> {
                                networkManager.deleteFood(new Food(id, name));
                                onBackPressed();
                        })
                        .setNegativeButton(getResources().getString(android.R.string.no), (DialogInterface dialog, int whichButton) -> {
                                dialog.dismiss();
                        })
                        .show();

                break;
            default:
        }
        return true;
    }

    public void addItem(View view) {
        Intent i = new Intent(this, AddFoodItemActivity.class);
        Bundle extras = new Bundle();
        extras.putInt(AddFoodItemActivity.KEY_ID, id);
        extras.putString(AddFoodItemActivity.KEY_FOOD, name);
        i.putExtras(extras);
        startActivity(i);
    }
}
