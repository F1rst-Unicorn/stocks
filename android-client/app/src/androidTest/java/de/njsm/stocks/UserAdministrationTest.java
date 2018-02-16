package de.njsm.stocks;

import android.support.test.rule.ActivityTestRule;
import de.njsm.stocks.frontend.StartupActivity;
import de.njsm.stocks.screen.MainScreen;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

public class UserAdministrationTest {
    @Rule
    public ActivityTestRule<StartupActivity> mActivityRule = new ActivityTestRule<>(StartupActivity.class);

    private String username = "Juliette";

    private String deviceName = "Mobile";

    @After
    public void tearDown() throws Exception {
        mActivityRule.finishActivity();
    }

    @Test
    public void addNewUserAndDevice() throws Exception {
        MainScreen.test()
                .goToUsers()
                .addUser(username)
                .assertUser(1, username)
                .selectUser(1)
                .addDevice(deviceName)
                .pressBack()
                .assertDevice(0, deviceName)
                .removeDevice(0)
                .assertEmptyList()
                .pressBack()
                .removeUser(1)
                .assertNotContains(username);
    }
}
