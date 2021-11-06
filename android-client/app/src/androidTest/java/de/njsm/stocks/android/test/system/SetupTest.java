/* stocks is client-server program to manage a household's food stock
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

package de.njsm.stocks.android.test.system;


import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;
import de.njsm.stocks.android.test.system.screen.QrScreen;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SetupTest extends SystemTest {

    @Rule
    public GrantPermissionRule cameraPermission = GrantPermissionRule.grant(Manifest.permission.CAMERA);

    @Before
    public void setup() {
        Intents.init();
        Intent data = new Intent();
        String user = "Jack";
        String device = "android-client";
        int uid = 1;
        int did = Properties.deviceId;
        String ticket = Properties.ticket;
        String fingerprint = Properties.fingerprint;
        data.putExtra("SCAN_RESULT", user + "\n"
                + device + "\n"
                + uid + "\n"
                + did + "\n"
                + fingerprint + "\n"
                + ticket + "\n"
                + Properties.server + "\n"
                + "10910\n10911\n10912\n");
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, data);
        Intents.intending(IntentMatchers.hasAction("com.google.zxing.client.android.SCAN")).respondWith(result);
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testRegistration() {
        new QrScreen()
                .scanQrCode()
                .assertServer(Properties.server)
                .assertCaPort(10910)
                .assertSentryPort(10911)
                .assertServerPort(10912)
                .assertUser("Jack")
                .assertDevice("android-client")
                .assertUserId(1)
                .assertDeviceId(Properties.deviceId)
                .assertFingerPrint(Properties.fingerprint)
                .assertTicket(Properties.ticket)
                .submit()
                .assertRegistrationSuccess();
    }

}
