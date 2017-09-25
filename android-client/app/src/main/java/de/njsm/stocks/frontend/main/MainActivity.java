package de.njsm.stocks.frontend.main;

import android.app.Fragment;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import de.njsm.stocks.R;
import de.njsm.stocks.backend.network.AsyncTaskFactory;
import de.njsm.stocks.backend.network.NetworkManager;
import de.njsm.stocks.backend.util.Config;
import de.njsm.stocks.backend.util.ExceptionHandler;
import de.njsm.stocks.common.data.Food;
import de.njsm.stocks.frontend.ActivitySwitcher;
import de.njsm.stocks.frontend.DialogFactory;
import de.njsm.stocks.frontend.search.SearchActivity;
import de.njsm.stocks.frontend.util.SwipeSyncCallback;
import de.njsm.stocks.zxing.IntentIntegrator;
import de.njsm.stocks.zxing.IntentResult;

import java.util.Collections;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    protected DrawerLayout drawer;

    protected NavigationView navigationView;

    protected Fragment outlineFragment;
    protected Fragment usersFragment;
    protected Fragment locationsFragment;
    protected Fragment currentFragment;

    private NetworkManager networkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this.getFilesDir(),
                Thread.getDefaultUncaughtExceptionHandler()));

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_food_toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        drawer.getChildAt(0).setSelected(true);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        usersFragment = new UserListFragment();
        locationsFragment = new LocationListFragment();
        outlineFragment = new OutlineFragment();
        setActiveFragment(outlineFragment);

        AsyncTaskFactory factory = new AsyncTaskFactory(this);
        networkManager = new NetworkManager(factory);
        SwipeRefreshLayout swiper = (SwipeRefreshLayout) findViewById(R.id.swipe_overlay);
        swiper.setOnRefreshListener(new SwipeSyncCallback(swiper, networkManager));
        networkManager.synchroniseData();

        SharedPreferences prefs = getSharedPreferences(Config.PREFERENCES_FILE, Context.MODE_PRIVATE);
        TextView view = ((TextView) navigationView.getHeaderView(0).findViewById(R.id.drawer_username));
        if (view != null) {
            view.setText(prefs.getString(Config.USERNAME_CONFIG, ""));
        }
        view = (TextView) navigationView.getHeaderView(0).findViewById(R.id.drawer_server);
        if (view != null) {
            view.setText(prefs.getString(Config.SERVER_NAME_CONFIG, ""));
        }
        view = (TextView) navigationView.getHeaderView(0).findViewById(R.id.drawer_user_dev);
        if (view != null) {
            view.setText(prefs.getString(Config.DEVICE_NAME_CONFIG, ""));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentFragment == outlineFragment) {
            navigationView.setCheckedItem(R.id.outline);
        } else if (currentFragment == locationsFragment) {
            navigationView.setCheckedItem(R.id.locations);
        } else if (currentFragment == usersFragment) {
            navigationView.setCheckedItem(R.id.users);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (currentFragment != outlineFragment) {
                setActiveFragment(outlineFragment);
                navigationView.setCheckedItem(R.id.outline);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchActivity.class)));
        searchView.setIconifiedByDefault(true);
        searchView.setSubmitButtonEnabled(true);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment f;

        switch (item.getItemId()) {
            case R.id.users:
                f = usersFragment;
                break;
            case R.id.locations:
                f = locationsFragment;
                break;
            case R.id.settings:
                ActivitySwitcher.switchToSettings(this);
            default:
                f = outlineFragment;
        }

        setActiveFragment(f);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            String eanNumber = scanResult.getContents();
            if (eanNumber.length() != 13) {
                Log.w(Config.LOG_TAG, "Scanned invalid code");
                Toast.makeText(this,
                        getResources().getString(R.string.dialog_no_barcode_result),
                        Toast.LENGTH_SHORT).show();
                return;
            }
            Log.i(Config.LOG_TAG, "Starting loading for EAN code " + eanNumber);
            ScanResultCallback loader = new ScanResultCallback(this,
                    (Food food) -> ActivitySwitcher.switchToFoodActivity(this, food),
                    (Void dummy) ->             Toast.makeText(this,
                            getResources().getString(R.string.dialog_no_barcode_result),
                            Toast.LENGTH_SHORT).show());
            Bundle args = new Bundle();
            args.putString(ScanResultCallback.KEY_EAN_NUMBER, eanNumber);
            getLoaderManager().restartLoader(0, args, loader);
        } else {
            Log.d(Config.LOG_TAG, "Got invalid result from activity");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_scan:
                IntentIntegrator intent = new IntentIntegrator(this);
                intent.initiateScan(Collections.singletonList("EAN_13"));
                break;
            default:
                return false;
        }
        return true;
    }

    public void addEntity(View view) {
        if (currentFragment == usersFragment) {
            DialogFactory.showUserAddingDialog(this, networkManager);
        } else if (currentFragment == locationsFragment) {
            DialogFactory.showLocationAddingDialog(this, networkManager);
        } else {
            DialogFactory.showFoddAddingDialog(this, networkManager);
        }
    }

    public void showAllFood(View view) {
        ActivitySwitcher.switchToEatSoonActivity(this);
    }

    public void showMissingFood(View view) {
        ActivitySwitcher.switchToEmptyFoodActivity(this);
    }

    private void setActiveFragment(Fragment fragment) {
        getFragmentManager().beginTransaction()
                .replace(R.id.main_content, fragment)
                .commit();
        currentFragment = fragment;
    }
}
