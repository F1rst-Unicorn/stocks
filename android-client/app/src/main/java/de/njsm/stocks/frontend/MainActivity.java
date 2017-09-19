package de.njsm.stocks.frontend;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import de.njsm.stocks.R;
import de.njsm.stocks.backend.network.AsyncTaskFactory;
import de.njsm.stocks.backend.network.NetworkManager;
import de.njsm.stocks.backend.util.Config;
import de.njsm.stocks.backend.util.ExceptionHandler;
import de.njsm.stocks.backend.util.SwipeSyncCallback;
import de.njsm.stocks.common.data.Food;
import de.njsm.stocks.common.data.Location;
import de.njsm.stocks.common.data.User;

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
        getMenuInflater().inflate(R.menu.main, menu);
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
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
            default:
                f = outlineFragment;
        }

        setActiveFragment(f);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void addEntity(View view) {
        if (currentFragment == usersFragment) {
            final EditText textField = (EditText) getLayoutInflater().inflate(R.layout.text_field, null);
            textField.setHint(getResources().getString(R.string.hint_username));
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.dialog_new_user))
                    .setView(textField)
                    .setPositiveButton(getResources().getString(R.string.dialog_ok), (DialogInterface dialog, int whichButton) -> {
                            String name = textField.getText().toString().trim();
                            networkManager.addUser(new User(0, name));
                    })
                    .setNegativeButton(getResources().getString(R.string.dialog_cancel), (DialogInterface dialog, int whichButton) -> {})
                    .show();
        } else if (currentFragment == locationsFragment) {
            final EditText textField = (EditText) getLayoutInflater().inflate(R.layout.text_field, null);
            textField.setHint(getResources().getString(R.string.hint_location));
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.dialog_new_location))
                    .setView(textField)
                    .setPositiveButton(getResources().getString(R.string.dialog_ok), (DialogInterface dialog, int whichButton) -> {
                            String name = textField.getText().toString().trim();
                            networkManager.addLocation(new Location(0, name));
                    })
                    .setNegativeButton(getResources().getString(R.string.dialog_cancel), (DialogInterface dialog, int whichButton) -> {})
                    .show();
        } else {
            final EditText textField = (EditText) getLayoutInflater().inflate(R.layout.text_field, null);
            textField.setHint(getResources().getString(R.string.hint_food));
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.dialog_new_food))
                    .setView(textField)
                    .setPositiveButton(getResources().getString(R.string.dialog_ok), (DialogInterface dialog, int whichButton) -> {
                            String name = textField.getText().toString().trim();
                            networkManager.addFood(new Food(0, name));
                    })
                    .setNegativeButton(getResources().getString(R.string.dialog_cancel), (DialogInterface dialog, int whichButton) -> {})
                    .show();
        }
    }

    public void showAllFood(View view) {
        Intent i = new Intent(this, EatSoonActivity.class);
        startActivity(i);
    }

    public void showMissingFood(View view) {
        Intent i = new Intent(this, EmptyFoodActivity.class);
        startActivity(i);
    }

    private void setActiveFragment(Fragment fragment) {
        getFragmentManager().beginTransaction()
                .replace(R.id.main_content, fragment)
                .commit();
        currentFragment = fragment;
    }
}
