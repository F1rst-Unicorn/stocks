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

package de.njsm.stocks.client.view;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import dagger.android.AndroidInjection;
import de.njsm.stocks.R;
import de.njsm.stocks.client.NavigationArgConsumerImpl;

import javax.inject.Inject;

public class SetupActivity extends AppCompatActivity {

    private NavController navController;

    private NavigationArgConsumerImpl navigationArgConsumer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        Toolbar toolbar = findViewById(R.id.activity_setup_toolbar);
        setSupportActionBar(toolbar);

        navController = Navigation.findNavController(this, R.id.activity_setup_nav_host_fragment);
        navigationArgConsumer.setNavController(navController);
        NavigationUI.setupWithNavController(toolbar, navController);
    }

    @Override
    public boolean onNavigateUp() {
        return navController.navigateUp()
                || super.onNavigateUp();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp()
                || super.onSupportNavigateUp();
    }

    @Inject
    public void setNavigationArgConsumer(NavigationArgConsumerImpl navigationArgConsumer) {
        this.navigationArgConsumer = navigationArgConsumer;
    }
}
