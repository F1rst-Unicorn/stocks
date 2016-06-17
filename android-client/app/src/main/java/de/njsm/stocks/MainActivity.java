package de.njsm.stocks;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import de.njsm.stocks.backend.db.DatabaseHandler;
import de.njsm.stocks.backend.network.ServerManager;
import de.njsm.stocks.backend.network.SyncTask;
import de.njsm.stocks.setup.SetupActivity;
import de.njsm.stocks.setup.SetupFinishedListener;
import de.njsm.stocks.setup.SetupTask;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                   SetupFinishedListener,
                   SwipeRefreshLayout.OnRefreshListener {

    protected DrawerLayout drawer;
    protected View content;
    protected SwipeRefreshLayout swiper;

    protected Fragment outlineFragment;
    protected Fragment usersFragment;
    protected Fragment locationsFragment;

    protected Config config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        drawer.getChildAt(0).setSelected(true);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        swiper = (SwipeRefreshLayout) findViewById(R.id.swipe_overlay);
        swiper.setOnRefreshListener(this);

        content = findViewById(R.id.main_content);
        usersFragment = new UserListFragment();
        locationsFragment = new LocationListFragment();
        outlineFragment = new OutlineFragment();
        config = new Config(this);

        getFragmentManager().beginTransaction()
                .replace(R.id.main_content, outlineFragment)
                .commit();

        if (! config.isConfigured()) {
            Intent i = new Intent(this, SetupActivity.class);
            startActivity(i);
        } else {
            finished();
            TextView view = ((TextView) navigationView.getHeaderView(0).findViewById(R.id.drawer_user_name));
            if (view != null) {
                view.setText(config.getUsername());
            }
            view = (TextView) navigationView.getHeaderView(0).findViewById(R.id.drawer_server);
            if (view != null) {
                view.setText(config.getServerName());
            }
            view = (TextView) navigationView.getHeaderView(0).findViewById(R.id.drawer_user_dev);
            if (view != null) {
                view.setText(config.getDeviceName());
            }
        }

        if (getIntent().hasExtra("setup")) {
            getIntent().getExtras().remove("setup");
            SetupTask s = new SetupTask(this);
            s.addListener(this);
            s.execute();
        }



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Fragment f;
        FloatingActionButton fab;

        switch (item.getItemId()) {
            case R.id.users:
                f = usersFragment;
                fab = ((FloatingActionButton) findViewById(R.id.fab));
                fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_add_white_24dp));
                break;
            case R.id.locations:
                f = locationsFragment;
                fab = ((FloatingActionButton) findViewById(R.id.fab));
                fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_add_white_24dp));
                break;
            default:
                f = outlineFragment;
                fab = ((FloatingActionButton) findViewById(R.id.fab));
                fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_local_dining_white_24dp));
        }

        getFragmentManager().beginTransaction()
                .replace(R.id.main_content, f)
                .commit();

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void testConnect(View view) {

    }

    @Override
    public void finished() {
        config.refresh();
        ServerManager.init(config);
        DatabaseHandler.init(this);
    }

    public Config getConfig() {
        return config;
    }

    @Override
    public void onRefresh() {
        SyncTask task = new SyncTask(swiper);
        task.execute();
    }

}
