package de.njsm.stocks.android.frontend.main;


import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;
import dagger.android.AndroidInjection;
import de.njsm.stocks.R;
import de.njsm.stocks.android.util.Logger;

public class MainActivity extends AppCompatActivity {

    private static final Logger LOG = new Logger(MainActivity.class);

    private NavController navController;

    private NavigationView navigationView;

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationView = findViewById(R.id.main_nav);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        drawerLayout = findViewById(R.id.main_drawer_layout);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        navController = Navigation.findNavController(this, R.id.main_nav_host_fragment);
        NavigationUI.setupWithNavController(toolbar, navController, drawerLayout);
    }

    private boolean onNavigationItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.users:
                navController.navigate(R.id.action_nav_fragment_outline_to_nav_fragment_users);
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        /*SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchActivity.class)));
        searchView.setIconifiedByDefault(true);
        searchView.setSubmitButtonEnabled(true);*/
        return true;
    }

    @Override
    public boolean onNavigateUp() {
        return navController.navigateUp()
                || super.onNavigateUp()
                || openDrawer();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp()
                || super.onSupportNavigateUp()
                || openDrawer();
    }

    private boolean openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
        return true;
    }

    /*    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            String eanNumber = scanResult.getContents();
            if (eanNumber == null || eanNumber.length() != 13) {
                LOG.w("Scanned invalid code");
                Toast.makeText(this,
                        getResources().getString(R.string.dialog_no_barcode_result),
                        Toast.LENGTH_SHORT).show();
                return;
            }
            LOG.i("Starting loading for EAN code " + eanNumber);
            ScanResultCallback loader = new ScanResultCallback(this,
                    (Food food) -> ActivitySwitcher.switchToFoodActivity(this, food),
                    (Void dummy) -> ActivitySwitcher.switchToAllFoodActivity(this, eanNumber));
            Bundle args = new Bundle();
            args.putString(ScanResultCallback.KEY_EAN_NUMBER, eanNumber);
            LoaderManager.getInstance(this).restartLoader(0, args, loader);
        } else {
            LOG.d("Got invalid result from activity");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_scan) {
            IntentIntegrator intent = new IntentIntegrator(this);
            intent.initiateScan();
        } else {
            return false;
        }
        return true;
    }*/
}
