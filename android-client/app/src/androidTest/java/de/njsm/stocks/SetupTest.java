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

import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SetupTest {

    @Rule
    public ActivityTestRule<StartupActivity> mActivityRule = new ActivityTestRule<>(StartupActivity.class);

    private String fingerprint;

    private String hostname = "10.0.2.2";

    @Before
    public void setup() throws Exception {
        Intents.init();
        Intent data = new Intent();
        String user = "Jack";
        String device = "Device";
        int uid = 1;
        int did = Properties.deviceId;
        String ticket = Properties.ticket;
        fingerprint = getFingerprintFromServer();
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
                .enterServerName(hostname)
                .next()
                .next()
                .assertUser("Jack")
                .assertDevice("Device")
                .assertUserId(1)
                .assertDeviceId(Properties.deviceId)
                .assertFingerPrint(fingerprint)
                .assertTicket(Properties.ticket)
                .submit()
                .assertRegistrationSuccess();
    }

    private String getFingerprintFromServer() throws Exception {
        URL website = new URL("http://" + hostname + ":10910/ca");
        String caCert = IOUtils.toString(website.openStream());

        PEMReader reader = new PEMReader(new InputStreamReader(IOUtils.toInputStream(caCert)));
        Object rawCert = reader.readObject();
        reader.close();
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
