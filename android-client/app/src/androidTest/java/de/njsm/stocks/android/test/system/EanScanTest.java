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
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import de.njsm.stocks.android.test.system.screen.OutlineScreen;

@LargeTest
public class EanScanTest extends SystemTest {

    @Rule
    public GrantPermissionRule cameraPermission = GrantPermissionRule.grant(Manifest.permission.CAMERA);

    @Before
    public void setup() {
        Intents.init();
    }

    @After
    public void tearDown() throws Exception {
        Intents.release();
    }

    @Test
    public void testSuccessfulScan() throws Exception {
        setupScanResult("1234567891234");

        OutlineScreen.test()
                .scanSuccessful()
                .assertTitle("Beer");
    }

    @Test
    public void testSelectionOnUnknownCode() {
        setupScanResult("0000000000000");

        OutlineScreen.test()
                .scanFailing()
                .click(1)
                .assertTitle("Bread")
                .goToBarCodes()
                .assertItemCount(1);
    }

    private void setupScanResult(String code) {
        Intent data = new Intent();
        data.putExtra("SCAN_RESULT", code);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, data);
        Intents.intending(IntentMatchers.hasAction("com.google.zxing.client.android.SCAN")).respondWith(result);
    }
}