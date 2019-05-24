package de.njsm.stocks;


import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;
import de.njsm.stocks.android.frontend.main.MainActivity;
import de.njsm.stocks.screen.ServerInputScreen;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SetupTest {

    @Rule
    public GrantPermissionRule cameraPermission = GrantPermissionRule.grant(Manifest.permission.CAMERA);

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setup() throws Exception {
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
                + ticket);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, data);
        Intents.intending(IntentMatchers.hasAction("com.google.zxing.client.android.SCAN")).respondWith(result);
    }

    @After
    public void tearDown() throws Exception {
        Intents.release();
        mActivityRule.finishActivity();
    }

    @Test
    public void testRegistration() {
        ServerInputScreen.test()
                .enterServerName("10.0.2.2")
                .next()
                .next()
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
