package de.njsm.stocks.screen;

import de.njsm.stocks.R;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.not;

public class DeviceScreen extends AbstractListPresentingScreen {

    public DeviceScreen() {
        super(R.id.user_detail_device_list);
    }

    public DeviceQrScreen addDevice(String name) {
        onView(withId(R.id.fab)).perform(click());
        onView(withHint(R.string.hint_device_name)).perform(replaceText(name));
        onView(withText("OK")).perform(click());
        return new DeviceQrScreen();
    }

    @Override
    public UserScreen pressBack() {
        super.pressBack();
        return new UserScreen();
    }

    public DeviceScreen assertDevice(int index, String name) {
        checkIndex(index);
        onData(anything()).inAdapterView(withId(R.id.user_detail_device_list))
                .atPosition(index)
                .check(matches(withText(name)));
        return this;
    }

    public DeviceScreen removeDevice(int index) {
        checkIndex(index);
        onData(anything()).inAdapterView(withId(R.id.user_detail_device_list))
                .atPosition(index)
                .perform(longClick());
        onView(withText("OK")).perform(click());
        return this;
    }

    public DeviceScreen assertEmptyList() {
        onView(withId(R.id.user_detail_device_list))
                .check(matches(not(isDisplayed())));
        return this;
    }
}
