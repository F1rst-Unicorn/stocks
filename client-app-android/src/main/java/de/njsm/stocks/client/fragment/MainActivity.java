/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package de.njsm.stocks.client.fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;
import dagger.android.AndroidInjection;
import de.njsm.stocks.R;
import de.njsm.stocks.client.navigation.NavigationArgConsumerImpl;
import de.njsm.stocks.client.navigation.NavigationGraphDirections;
import de.njsm.stocks.client.presenter.MainActivityViewModel;

import javax.inject.Inject;

public class MainActivity extends BaseActivity {

    private NavController navController;

    private DrawerLayout drawerLayout;

    private NavigationArgConsumerImpl navigationArgConsumer;

    private DialogDisplayerImpl dialogDisplayer;

    private MainActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavigationView navigationView = findViewById(R.id.main_nav);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        drawerLayout = findViewById(R.id.activity_main_drawer_layout);

        Toolbar toolbar = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);

        navController = Navigation.findNavController(this, R.id.activity_main_nav_host_fragment);
        navigationArgConsumer.setNavController(navController);
        NavigationUI.setupWithNavController(toolbar, navController, drawerLayout);

        setAccountInformation();
    }

    private void setAccountInformation() {
        viewModel.get().observe(this, accountInformation -> {
            NavigationView nav = findViewById(R.id.main_nav);
            View headerView = nav.getHeaderView(0);
            TextView view = headerView.findViewById(R.id.nav_header_main_username);
            view.setText(accountInformation.userName());
            view = headerView.findViewById(R.id.nav_header_main_dev);
            view.setText(accountInformation.deviceName());
            view = headerView.findViewById(R.id.nav_header_main_server);
            view.setText(accountInformation.serverName());
        });
    }

    private boolean onNavigationItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.activity_main_drawer_units) {
            navigationArgConsumer.navigate(NavigationGraphDirections.actionGlobalNavFragmentUnitTabs());
        } else if (id == R.id.activity_main_drawer_locations) {
            navigationArgConsumer.navigate(NavigationGraphDirections.actionGlobalNavFragmentLocationList());
        } else if (id == R.id.activity_main_drawer_users) {
            navigationArgConsumer.navigate(NavigationGraphDirections.actionGlobalNavFragmentUsers());
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

    @Inject
    void setNavigationArgConsumer(NavigationArgConsumerImpl navigationArgConsumer) {
        this.navigationArgConsumer = navigationArgConsumer;
    }

    @Inject
    void setDialogDisplayer(DialogDisplayerImpl dialogDisplayer) {
        this.dialogDisplayer = dialogDisplayer;
        this.dialogDisplayer.setActivity(this);
    }

    @Inject
    void setViewModel(ViewModelProvider.Factory viewModelFactory) {
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, viewModelFactory);
        viewModel = viewModelProvider.get(MainActivityViewModel.class);
    }
}
