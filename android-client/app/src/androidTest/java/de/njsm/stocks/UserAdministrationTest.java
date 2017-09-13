package de.njsm.stocks;

import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.rule.ActivityTestRule;
import android.view.Gravity;
import android.widget.ListView;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.CoreMatchers.*;

public class UserAdministrationTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    private String username = "Juliette";

    @Test
    public void addNewUserAndDevice() throws Exception {
        addUser();
        addDevice();
        removeDevice();
        removeUser();
    }

    private void addUser() {
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.START)))
                .perform(open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.users));

        onView(withId(R.id.fab)).perform(click());
        onView(withHint(R.string.hint_username)).perform(replaceText(username));
        onView(withText("OK")).perform(click());

        onData(anything()).inAdapterView(allOf(withEffectiveVisibility(Visibility.VISIBLE), instanceOf(ListView.class)))
                .atPosition(1)
                .onChildView(withId(R.id.item_user_name))
                .check(matches(withText(username)));
    }

    private void addDevice() {
        String deviceName = "Mobile";
        onData(anything()).inAdapterView(allOf(withEffectiveVisibility(Visibility.VISIBLE), instanceOf(ListView.class)))
                .atPosition(1)
                .perform(click());

        onView(withId(R.id.fab)).perform(click());
        onView(withHint(R.string.hint_device_name)).perform(replaceText(deviceName));
        onView(withText("OK")).perform(click());

        pressBack();
        onData(anything()).inAdapterView(withId(R.id.user_detail_device_list))
                .atPosition(0)
                .check(matches(withText(deviceName)));
    }

    private void removeDevice() {
        onData(anything()).inAdapterView(withId(R.id.user_detail_device_list))
                .atPosition(0)
                .perform(longClick());
        onView(withText("OK")).perform(click());

        onView(withId(R.id.user_detail_device_list))
                .check(matches(not(isDisplayed())));

        pressBack();
    }

    private void removeUser() {
        onData(anything()).inAdapterView(allOf(withEffectiveVisibility(Visibility.VISIBLE), instanceOf(ListView.class)))
                .atPosition(1)
                .perform(longClick());
        onView(withText("OK")).perform(click());
        onData(anything()).inAdapterView(allOf(withEffectiveVisibility(Visibility.VISIBLE), instanceOf(ListView.class)))
                .check(matches(not(withChild(withText(username)))));
    }
}
