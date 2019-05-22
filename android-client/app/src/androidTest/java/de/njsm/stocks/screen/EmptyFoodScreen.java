package de.njsm.stocks.screen;


import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import de.njsm.stocks.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static de.njsm.stocks.util.Matchers.atPosition;
import static junit.framework.TestCase.assertTrue;

public class EmptyFoodScreen extends AbstractListPresentingScreen {

    public FoodScreen click(int itemIndex) {
        checkIndex(itemIndex);

        onView(withId(R.id.template_swipe_list_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(itemIndex, ViewActions.click()));

        return new FoodScreen();
    }

    public EmptyFoodScreen assertLastItemIsNamed(String text) {
        int itemCount = getListCount();
        assertTrue(itemCount >= 0);

        int position = itemCount - 1;
        onView(withId(R.id.template_swipe_list_list))
                .perform(RecyclerViewActions.scrollToPosition(position))
                .check(matches(atPosition(position, withChild(withText(text)))));
        return this;

    }
}
