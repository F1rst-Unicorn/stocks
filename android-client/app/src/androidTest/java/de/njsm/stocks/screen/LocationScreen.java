package de.njsm.stocks.screen;

import android.support.test.espresso.matcher.ViewMatchers;
import android.widget.ListView;
import de.njsm.stocks.R;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertTrue;

public class LocationScreen extends AbstractListPresentingScreen {

    public LocationScreen addLocation(String name) {
        onView(withId(R.id.fab)).perform(click());
        onView(withHint(R.string.hint_location)).perform(replaceText(name));
        onView(withText("OK")).perform(click());
        return this;
    }

    public LocationScreen assertLastItemIsNamed(String text) {
        int itemCount = getListCount();
        assertTrue(itemCount >= 0);

        int position = itemCount - 1;
        onData(anything()).inAdapterView(allOf(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), instanceOf(ListView.class)))
                .atPosition(position)
                .onChildView(withId(R.id.item_location_name))
                .check(matches(withText(text)));

        return this;
    }
}