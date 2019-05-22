package de.njsm.stocks.screen;


import androidx.test.espresso.contrib.RecyclerViewActions;
import de.njsm.stocks.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static de.njsm.stocks.util.Matchers.atPosition;
import static junit.framework.TestCase.assertTrue;

public class LocationScreen extends AbstractListPresentingScreen {

    public LocationScreen addLocation(String name) {
        onView(withId(R.id.template_swipe_list_fab)).perform(click());
        onView(withHint(R.string.hint_location)).perform(replaceText(name));
        onView(withText("OK")).perform(click());
        return this;
    }

    public LocationScreen assertLastItemIsNamed(String text) {
        int itemCount = getListCount();
        assertTrue(itemCount >= 0);

        int position = itemCount - 1;
        onView(withId(R.id.template_swipe_list_list))
                .perform(RecyclerViewActions.scrollToPosition(position))
                .check(matches(atPosition(position, withText(text))));

        return this;
    }
}