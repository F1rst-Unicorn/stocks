package de.njsm.stocks;


import androidx.test.rule.ActivityTestRule;
import de.njsm.stocks.android.frontend.main.MainActivity;
import de.njsm.stocks.screen.OutlineScreen;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

public class UserAdministrationTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    private String username = "Juliette";

    private String deviceName = "Mobile";

    @After
    public void tearDown() throws Exception {
        mActivityRule.finishActivity();
    }

    @Test
    public void addNewUserAndDevice() throws Exception {
        OutlineScreen.test()
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
                .assertEmpty();
    }
}
