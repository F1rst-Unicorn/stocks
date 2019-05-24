package de.njsm.stocks.screen;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import de.njsm.stocks.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static de.njsm.stocks.util.Matchers.atPosition;
import static org.junit.Assert.assertEquals;

public class SearchScreen extends AbstractListPresentingScreen {

    public SearchScreen assertResultCount(int expected) {
        assertEquals(expected, getListCount());
        return this;
    }

    public SearchScreen assertItemContent(int itemIndex, String name, int count) {
        checkIndex(itemIndex);

        onView(withId(R.id.template_swipe_list_list))
                .perform(RecyclerViewActions.scrollToPosition(itemIndex))
                .check(matches(atPosition(itemIndex, withChild(withText(name)))));
        onView(withId(R.id.template_swipe_list_list))
                .perform(RecyclerViewActions.scrollToPosition(itemIndex))
                .check(matches(atPosition(itemIndex, withChild(withText(String.valueOf(count))))));
        return this;
    }

    public FoodScreen click(int itemIndex) {
        checkIndex(itemIndex);

        onView(withId(R.id.template_swipe_list_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(itemIndex, ViewActions.click()));
        return new FoodScreen();
    }

}
