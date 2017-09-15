package de.njsm.stocks;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import de.njsm.stocks.backend.network.AsyncTaskFactory;
import de.njsm.stocks.backend.network.NetworkManager;
import de.njsm.stocks.backend.setup.SetupTask;
import de.njsm.stocks.backend.util.ExceptionHandler;
import de.njsm.stocks.backend.util.SwipeSyncCallback;
import de.njsm.stocks.common.data.Food;
import de.njsm.stocks.common.data.Location;
import de.njsm.stocks.common.data.User;
import de.njsm.stocks.frontend.setup.SetupActivity;
import de.njsm.stocks.frontend.setup.SetupFinishedListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                   SetupFinishedListener {

    protected DrawerLayout drawer;
    protected SwipeRefreshLayout swiper;
    protected NavigationView navigationView;

    protected Fragment outlineFragment;
    protected Fragment usersFragment;
    protected Fragment locationsFragment;
    protected Fragment currentFragment;

    protected SharedPreferences prefs;
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
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        swiper = (SwipeRefreshLayout) findViewById(R.id.swipe_overlay);
        assert swiper != null;
        swiper.setOnRefreshListener(new SwipeSyncCallback(swiper, networkManager));

        usersFragment = new UserListFragment();
        locationsFragment = new LocationListFragment();
        outlineFragment = new OutlineFragment();
        currentFragment = outlineFragment;
        prefs = getSharedPreferences(Config.PREFERENCES_FILE, Context.MODE_PRIVATE);

        getFragmentManager().beginTransaction()
                .replace(R.id.main_content, outlineFragment)
                .commit();

        if (getIntent().hasExtra(SetupActivity.SETUP_FINISHED)) {
            getIntent().getExtras().remove(SetupActivity.SETUP_FINISHED);
            SetupTask s = new SetupTask(this);
            s.registerListener(this);
            s.execute();
        } else if (! prefs.contains(Config.USERNAME_CONFIG)) {
            Intent i = new Intent(this, SetupActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        } else {
            finished();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        prefs = null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        prefs = prefs == null ? getSharedPreferences(Config.PREFERENCES_FILE, Context.MODE_PRIVATE) : prefs;
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (currentFragment != outlineFragment) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.main_content, outlineFragment)
                        .commit();
                currentFragment = outlineFragment;
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
    public boolean onNavigationItemSelected(MenuItem item) {

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

        getFragmentManager().beginTransaction()
                .replace(R.id.main_content, f)
                .commit();
        currentFragment = f;

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void finished() {
        AsyncTaskFactory factory = new AsyncTaskFactory(this);
        networkManager = new NetworkManager(factory);
        factory.setNetworkManager(networkManager);
        networkManager.synchroniseData();
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

    public void addEntity(View view) {
        if (currentFragment == usersFragment) {
            final EditText textField = (EditText) getLayoutInflater().inflate(R.layout.text_field, null);
            textField.setHint(getResources().getString(R.string.hint_username));
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.dialog_new_user))
                    .setView(textField)
                    .setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String name = textField.getText().toString().trim();
                            networkManager.addUser(new User(0, name));
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    })
                    .show();
        } else if (currentFragment == locationsFragment) {
            final EditText textField = (EditText) getLayoutInflater().inflate(R.layout.text_field, null);
            textField.setHint(getResources().getString(R.string.hint_location));
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.dialog_new_location))
                    .setView(textField)
                    .setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String name = textField.getText().toString().trim();
                            networkManager.addLocation(new Location(0, name));
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    })
                    .show();
        } else {
            final EditText textField = (EditText) getLayoutInflater().inflate(R.layout.text_field, null);
            textField.setHint(getResources().getString(R.string.hint_food));
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.dialog_new_food))
                    .setView(textField)
                    .setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String name = textField.getText().toString().trim();
                            networkManager.addFood(new Food(0, name));
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    })
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
}
