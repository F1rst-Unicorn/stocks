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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import dagger.android.AndroidInjection;
import de.njsm.stocks.client.business.SetupStatusChecker;

import javax.inject.Inject;

public class StartupActivity extends AppCompatActivity {

    private SetupStatusChecker setupStatusChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        navigate();
    }

    void navigate() {
        if (setupStatusChecker.isSetup()) {
            goToMainMenu();
        } else {
            goToSetup();
        }
    }

    private void goToMainMenu() {
        goToActivity(MainActivity.class);
    }

    private void goToSetup() {
        goToActivity(SetupActivity.class);
    }

    private <T extends Activity> void goToActivity(Class<T> activity) {
        Intent intent = new Intent(this, activity);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Inject
    void setSetupStatusChecker(SetupStatusChecker setupStatusChecker) {
        this.setupStatusChecker = setupStatusChecker;
    }
}
