package de.njsm.stocks.screen;


import androidx.test.espresso.contrib.RecyclerViewActions;
import de.njsm.stocks.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

public class BarcodeScreen extends AbstractListPresentingScreen {

    public BarcodeScreen recordNewBarcode() {
        onView(withId(R.id.template_swipe_list_fab)).perform(click());
        return this;
    }

    public BarcodeScreen deleteBarcode(int index) {
        checkIndex(index);

        onView(withId(R.id.template_swipe_list_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(index, swipeRight()));
        onView(withText(R.string.action_undo))
                .perform(swipeRight());

        sleep(700);
        return this;
    }

    public BarcodeScreen assertItemCount(int count) {
        assertEquals(count, getListCount());
        return this;
    }
}
