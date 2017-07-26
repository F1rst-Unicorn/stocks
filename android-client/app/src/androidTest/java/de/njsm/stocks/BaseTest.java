package de.njsm.stocks;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.*;
import de.njsm.stocks.error.TextResourceException;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.openssl.PEMReader;
import org.junit.Before;

import java.io.InputStreamReader;
import java.net.URL;
import java.security.MessageDigest;
import java.security.cert.Certificate;

import static org.junit.Assert.assertNotNull;

public class BaseTest {

    private static final String PACKAGE = "de.njsm.stocks";
    private static final int LAUNCH_TIMEOUT = 5000;

    private UiDevice device;

    @Before
    public void startUpApplication() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        device.pressHome();
        final String launcherPackage = device.getLauncherPackageName();
        assertNotNull(launcherPackage);
        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);

        Context context = InstrumentationRegistry.getContext();
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(PACKAGE);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        device.wait(Until.hasObject(By.pkg(PACKAGE).depth(0)), LAUNCH_TIMEOUT);
    }

    protected boolean isInitialised() {
        return false;
    }

    protected void performRegistrationViaSentry() throws Exception {
        UiObject serverNameTextField = device.findObject(new UiSelector()
                .resourceId("de.njsm.stocks:id/server_url"));

        serverNameTextField.setText("dp-server");

        UiObject nextStepButton = device.findObject(new UiSelector()
                .resourceId("de.njsm.stocks:id/stepNext"));

        nextStepButton.click();

        UiObject noToBarcodeButton = device.findObject(new UiSelector()
                .text("NO")
                .resourceId("android:id/button2"));

        noToBarcodeButton.click();
        nextStepButton.click();

        device.findObject(new UiSelector()
                .resourceId("de.njsm.stocks:id/user_name"))
                .setText("Jack");
        device.findObject(new UiSelector()
                .resourceId("de.njsm.stocks:id/device_name"))
                .setText("Device");
        device.findObject(new UiSelector()
                .resourceId("de.njsm.stocks:id/user_id"))
                .setText("1");
        device.findObject(new UiSelector()
                .resourceId("de.njsm.stocks:id/device_id"))
                .setText("1");
        device.findObject(new UiSelector()
                .resourceId("de.njsm.stocks:id/fingerprint"))
                .setText(getFingerprintFromServer());
        device.findObject(new UiSelector()
                .resourceId("de.njsm.stocks:id/ticket"))
                .setText("0000");

        device.findObject(new UiSelector()
                .resourceId("de.njsm.stocks:id/stepEnd"))
                .click();

    }

    private String getFingerprintFromServer() throws Exception {
        URL website = new URL("http://dp-server.vm-tp.j.njsm.de:10910/ca");
        String caCert = IOUtils.toString(website.openStream());

        PEMReader reader = new PEMReader(new InputStreamReader(IOUtils.toInputStream(caCert)));
        Object rawCert = reader.readObject();
        Certificate cert;
        if (rawCert instanceof Certificate) {
            cert = (Certificate) rawCert;
        } else {
            throw new TextResourceException(R.string.dialog_cert_unreadable);
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