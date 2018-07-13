package de.njsm.stocks;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import de.njsm.stocks.frontend.StartupActivity;
import de.njsm.stocks.screen.MainScreen;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;

public class EanScanTest {

    @Rule
    public ActivityTestRule<StartupActivity> mActivityRule = new ActivityTestRule<>(StartupActivity.class);

    @Before
    public void setup() throws Exception {
        Intents.init();
    }

    @After
    public void tearDown() throws Exception {
        Intents.release();
        mActivityRule.finishActivity();
    }

    @Test
    public void testSuccessfulScan() throws Exception {
        setupScanResult("1234567891234");

        MainScreen.test()
                .scanSuccessful()
                .assertTitle("Beer");
    }

    @Test
    public void testSelectionOnUnknownCode() {
        setupScanResult("0000000000000");

        MainScreen.test()
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
        intending(toPackage("com.google.zxing.client.android")).respondWith(result);
    }
}
