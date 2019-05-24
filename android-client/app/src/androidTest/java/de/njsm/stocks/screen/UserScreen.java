package de.njsm.stocks.screen;


import androidx.test.espresso.contrib.RecyclerViewActions;
import de.njsm.stocks.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static de.njsm.stocks.util.Matchers.atPosition;
import static junit.framework.TestCase.assertEquals;

public class UserScreen extends AbstractListPresentingScreen {

    public UserScreen addUser(String name) {
        onView(withId(R.id.template_swipe_list_fab)).perform(click());
        onView(withHint(R.string.hint_username)).perform(replaceText(name));
        onView(withText("OK")).perform(click());
        sleep(100);
        return this;
    }

    public UserScreen assertUser(int index, String name) {
        checkIndex(index);
        onView(withId(R.id.template_swipe_list_list))
                .perform(RecyclerViewActions.scrollToPosition(index))
                .check(matches(atPosition(index, withText(name))));

        return this;
    }

    public DeviceScreen selectUser(int index) {
        checkIndex(index);
        onView(withId(R.id.template_swipe_list_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(index, click()));
        return new DeviceScreen();
    }

    public UserScreen removeUser(int index) {
        checkIndex(index);
        onView(withId(R.id.template_swipe_list_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(index, swipeRight()));
        onView(withText(R.string.action_undo))
                .perform(swipeRight());
        sleep(1000);
        return this;
    }

    public void assertEmpty() {
        assertEquals(1, getListCount());
    }
}
