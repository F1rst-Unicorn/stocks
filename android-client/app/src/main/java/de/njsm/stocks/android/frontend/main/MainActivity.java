package de.njsm.stocks.android.frontend.main;


import android.Manifest;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import dagger.android.AndroidInjection;
import de.njsm.stocks.R;
import de.njsm.stocks.android.util.Logger;

public class MainActivity extends AppCompatActivity {

    private static final Logger LOG = new Logger(MainActivity.class);

    public static final String ACTION_QR_CODE_SCANNED = "de.njsm.stocks.android.frontend.main.MainActivity.ACTION_QR_CODE_SCANNED";

    public static final String PARAM_QR_CONTENT = "de.njsm.stocks.android.frontend.main.MainActivity.PARAM_QR_CONTENT";

    private NavController navController;

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavigationView navigationView = findViewById(R.id.main_nav);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        drawerLayout = findViewById(R.id.main_drawer_layout);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        navController = Navigation.findNavController(this, R.id.main_nav_host_fragment);
        NavigationUI.setupWithNavController(toolbar, navController, drawerLayout);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            OutlineFragmentDirections.ActionNavFragmentOutlineToNavFragmentSearch args =
                    OutlineFragmentDirections.actionNavFragmentOutlineToNavFragmentSearch(query);
            navController.navigate(args);
        }
    }

    private boolean onNavigationItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.activity_main_drawer_users:
                navController.navigate(R.id.action_global_nav_fragment_users);
                break;
            case R.id.activity_main_drawer_locations:
                navController.navigate(R.id.action_global_nav_fragment_locations);
                break;
            case R.id.activity_main_drawer_settings:
                navController.navigate(R.id.action_global_nav_fragment_settings);
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0
                && permissions[0].equals(Manifest.permission.CAMERA)
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            LOG.i("Starting QR code reader");
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.initiateScan();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            LOG.d("Got QR code from intent");
            String content = scanResult.getContents();
            LOG.i("Got QR data: " + content);
            Intent i = new Intent();
            i.setAction(ACTION_QR_CODE_SCANNED);
            i.putExtra(PARAM_QR_CONTENT, content);
            LocalBroadcastManager.getInstance(this).sendBroadcast(i);
        } else {
            LOG.d("Got invalid result from activity");
        }
    }
}
