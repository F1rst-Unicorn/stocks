package de.njsm.stocks.screen;

import androidx.test.espresso.contrib.RecyclerViewActions;
import de.njsm.stocks.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static junit.framework.TestCase.assertEquals;

public class DeviceScreen extends AbstractListPresentingScreen {

    public DeviceScreen() {
        super(R.id.fragment_devices_device_list);
    }

    public DeviceQrScreen addDevice(String name) {
        onView(withId(R.id.fragment_devices_fab)).perform(click());
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
        sleep(1000);
        checkIndex(index);
        onView(withId(R.id.fragment_devices_device_list))
                .perform(RecyclerViewActions.scrollToPosition(index))
                .check(matches(withChild(withText(name))));
        return this;
    }

    public DeviceScreen removeDevice(int index) {
        checkIndex(index);
        onView(withId(R.id.fragment_devices_device_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(index, swipeRight()));
        onView(withText(R.string.action_undo))
                .perform(swipeRight());
        sleep(1000);
        return this;
    }

    public DeviceScreen assertEmptyList() {
        assertEquals(0, getListCount());
        return this;
    }
}
