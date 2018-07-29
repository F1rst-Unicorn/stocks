package de.njsm.stocks;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.espresso.intent.Intents;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import de.njsm.stocks.frontend.StartupActivity;
import de.njsm.stocks.screen.ServerInputScreen;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SetupTest {

    @Rule
    public ActivityTestRule<StartupActivity> mActivityRule = new ActivityTestRule<>(StartupActivity.class);

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
        intending(toPackage("com.google.zxing.client.android")).respondWith(result);
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
                .assertDevice("Device")
                .assertUserId(1)
                .assertDeviceId(Properties.deviceId)
                .assertFingerPrint(Properties.fingerprint)
                .assertTicket(Properties.ticket)
                .submit()
                .assertRegistrationSuccess();
    }

}
