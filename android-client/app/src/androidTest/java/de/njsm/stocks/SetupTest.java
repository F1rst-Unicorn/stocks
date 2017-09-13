package de.njsm.stocks;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.openssl.PEMReader;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.InputStreamReader;
import java.net.URL;
import java.security.MessageDigest;
import java.security.cert.Certificate;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SetupTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    private String fingerprint;

    private String hostname = "10.0.2.2";

    @Before
    public void setup() throws Exception {
        Intents.init();
        Intent data = new Intent();
        String user = "Jack";
        String device = "Device";
        int uid = 1;
        int did = 1;
        fingerprint = getFingerprintFromServer();
        String ticket = "0000";
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
    }

    @Test
    public void testRegistration() {

        ViewInteraction hostnameTextField = onView(
                allOf(withId(R.id.server_url), isDisplayed()));
        hostnameTextField.perform(replaceText(hostname), closeSoftKeyboard());

        ViewInteraction nextScreenButton = onView(
                allOf(withId(R.id.stepNext), withText("NEXT"),
                        withParent(allOf(withId(R.id.navigation),
                                withParent(withId(R.id.stepSwitcher)))),
                        isDisplayed()));
        nextScreenButton.perform(click());

        ViewInteraction lockedViewPager = onView(
                allOf(withId(R.id.stepPager), isDisplayed()));
        lockedViewPager.perform(swipeLeft());
        lockedViewPager.perform(swipeLeft());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.user_name), isDisplayed()));
        appCompatEditText4.check(matches(ViewMatchers.withText("Jack")));

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.device_name), isDisplayed()));
        appCompatEditText5.check(matches(ViewMatchers.withText("Device")));

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.user_id), isDisplayed()));
        appCompatEditText6.check(matches(ViewMatchers.withText("1")));

        ViewInteraction appCompatEditText7 = onView(
                allOf(withId(R.id.device_id), isDisplayed()));
        appCompatEditText7.check(matches(ViewMatchers.withText("1")));

        ViewInteraction appCompatEditText9 = onView(
                allOf(withId(R.id.fingerprint), isDisplayed()));
        appCompatEditText9.check(matches(ViewMatchers.withText(fingerprint)));

        ViewInteraction appCompatEditText10 = onView(
                allOf(withId(R.id.ticket), isDisplayed()));
        appCompatEditText10.check(matches(ViewMatchers.withText("0000")));

        ViewInteraction appCompatTextView3 = onView(
                allOf(withId(R.id.stepEnd), withText("COMPLETE"),
                        withParent(allOf(withId(R.id.navigation),
                                withParent(withId(R.id.stepSwitcher)))),
                        isDisplayed()));
        appCompatTextView3.perform(closeSoftKeyboard(), click());

        onView(allOf(withId(android.R.id.message)))
                .check(matches(withText(R.string.dialog_finished)));

        ViewInteraction appCompatButton = onView(
                allOf(withId(android.R.id.button1), withText("OK")));
        appCompatButton.perform(scrollTo(), click());
    }

    private String getFingerprintFromServer() throws Exception {
        URL website = new URL("http://" + hostname + ":10910/ca");
        String caCert = IOUtils.toString(website.openStream());

        PEMReader reader = new PEMReader(new InputStreamReader(IOUtils.toInputStream(caCert)));
        Object rawCert = reader.readObject();
        Certificate cert;
        if (rawCert instanceof Certificate) {
            cert = (Certificate) rawCert;
        } else {
            throw new IllegalArgumentException("Could not parse certificate");
        }        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(cert.getEncoded());
        byte[] digest = md.digest();

        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7',
                '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        StringBuilder buf = new StringBuilder(digest.length * 2);
        for (byte aDigest : digest) {
            buf.append(hexDigits[(aDigest & 0xf0) >> 4]);
            buf.append(hexDigits[aDigest & 0x0f]);
            buf.append(":");
        }
        buf.delete(buf.length()-1, buf.length());

        return buf.toString();
    }

}
