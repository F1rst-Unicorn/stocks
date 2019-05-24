package de.njsm.stocks;


import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import de.njsm.stocks.android.frontend.main.MainActivity;
import de.njsm.stocks.screen.OutlineScreen;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class EanScanTest {

    @Rule
    public GrantPermissionRule cameraPermission = GrantPermissionRule.grant(Manifest.permission.CAMERA);

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

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
