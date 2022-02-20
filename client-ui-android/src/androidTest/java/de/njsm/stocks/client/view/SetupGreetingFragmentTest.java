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
 */

package de.njsm.stocks.client.view;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.entities.RegistrationForm;
import de.njsm.stocks.client.navigation.SetupGreetingNavigator;
import de.njsm.stocks.client.ui.R;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class SetupGreetingFragmentTest {

    @Rule
    public GrantPermissionRule cameraPermission = GrantPermissionRule.grant(Manifest.permission.CAMERA);

    private FragmentScenario<SetupGreetingFragment> scenario;

    private SetupGreetingNavigator setupGreetingNavigator;

    @Before
    public void setup() {
        Intents.init();
        scenario = FragmentScenario.launchInContainer(SetupGreetingFragment.class, new Bundle(), R.style.StocksTheme);
        scenario.onFragment(fragment -> ((Application) fragment.requireActivity().getApplication()).getDaggerRoot().inject(this));
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void uiIsVisible() {
        onView(withId(R.id.fragment_setup_greeting_manual))
                .check(matches(isDisplayed()));
        onView(withText(R.string.text_qr_explanation))
                .check(matches(isDisplayed()));
        onView(withId(R.id.fragment_setup_greeting_scan))
                .check(matches(isDisplayed()));
    }

    @Test
    public void choosingManualSetupNavigates() {
        onView(withId(R.id.fragment_setup_greeting_manual)).perform(click());

        verify(setupGreetingNavigator).registerManually();
    }

    @Test
    public void obtainingQrCodeDataNavigates() {
        RegistrationForm registrationForm = RegistrationForm.builder()
                .serverName("test.example")
                .caPort(1409)
                .registrationPort(1410)
                .serverPort(1411)
                .userId(1412)
                .userName("username")
                .userDeviceId(1412)
                .userDeviceName("userdevicename")
                .fingerprint("fingerprint")
                .ticket("ticket")
                .build();
        Intent intent = returnDataUponScanning(registrationForm);

        scenario.onFragment(v -> LocalBroadcastManager.getInstance(v.requireContext()).sendBroadcast(intent));

        verify(setupGreetingNavigator).registerWithPrefilledData(registrationForm);
    }

    @Test
    public void choosingQrScanRequestsQrData() {
        onView(withId(R.id.fragment_setup_greeting_scan)).perform(click());

        Intents.intended(IntentMatchers.hasAction("com.google.zxing.client.android.SCAN"));
    }

    private Intent returnDataUponScanning(RegistrationForm registrationForm) {
        Intent intent = new Intent();
        intent.setAction(QrCodeDataBroadcastReceiver.ACTION_QR_CODE_SCANNED);
        intent.putExtra(QrCodeDataBroadcastReceiver.PARAM_QR_CONTENT, registrationForm.toQrString());
        return intent;
    }

    @Inject
    public void setSetupGreetingNavigator(SetupGreetingNavigator setupGreetingNavigator) {
        this.setupGreetingNavigator = setupGreetingNavigator;
    }
}
