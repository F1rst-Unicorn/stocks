package de.njsm.stocks;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.*;
import org.junit.Before;

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


    }


}