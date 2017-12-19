package de.njsm.stocks.screen;

import android.widget.ListView;
import de.njsm.stocks.R;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.CoreMatchers.*;

public class UserScreen extends AbstractListPresentingScreen {

    public UserScreen addUser(String name) {
        onView(withId(R.id.fab)).perform(click());
        onView(withHint(R.string.hint_username)).perform(replaceText(name));
        onView(withText("OK")).perform(click());
        return this;
    }

    public UserScreen assertUser(int index, String name) {
        checkIndex(index);
        onData(anything()).inAdapterView(allOf(withEffectiveVisibility(Visibility.VISIBLE), instanceOf(ListView.class)))
                .atPosition(index)
                .onChildView(withId(R.id.item_user_name))
                .check(matches(withText(name)));
        return this;
    }

    public DeviceScreen selectUser(int index) {
        checkIndex(index);
        onData(anything()).inAdapterView(allOf(withEffectiveVisibility(Visibility.VISIBLE), instanceOf(ListView.class)))
                .atPosition(index)
                .perform(click());
        return new DeviceScreen();
    }

    public UserScreen removeUser(int index) {
        checkIndex(index);
        onData(anything()).inAdapterView(allOf(withEffectiveVisibility(Visibility.VISIBLE), instanceOf(ListView.class)))
                .atPosition(index)
                .perform(longClick());
        onView(withText("OK")).perform(click());
        return this;
    }

    public UserScreen assertNotContains(String name) {
        onData(anything()).inAdapterView(allOf(withEffectiveVisibility(Visibility.VISIBLE), instanceOf(ListView.class)))
                .check(matches(not(withChild(withText(name)))));
        return this;
    }
}
