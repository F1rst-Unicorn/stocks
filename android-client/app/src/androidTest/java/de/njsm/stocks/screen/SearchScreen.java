package de.njsm.stocks.screen;

import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.action.ViewActions;
import android.widget.ListView;
import de.njsm.stocks.R;
import de.njsm.stocks.util.StealCountAction;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SearchScreen {

    public SearchScreen assertResultCount(int expected) {
        assertEquals(expected, getListCount());
        return this;
    }

    public SearchScreen assertItemContent(int itemIndex, String name, int count) {
        checkIndex(itemIndex);

        DataInteraction item = onData(anything()).inAdapterView(instanceOf(ListView.class))
                .atPosition(itemIndex);
        item.onChildView(withId(R.id.item_food_amount_name))
                .check(matches(withText(name)));
        item.onChildView(withId(R.id.item_food_amount_amount))
                .check(matches(withText(count)));
        return this;
    }

    public FoodScreen click(int itemIndex) {
        checkIndex(itemIndex);

        DataInteraction item = onData(anything()).inAdapterView(instanceOf(ListView.class))
                .atPosition(itemIndex);
        item.perform(ViewActions.click());
        return new FoodScreen();
    }

    private void checkIndex(int itemIndex) {
        if (itemIndex < 0 || itemIndex <= getListCount()) {
            fail();
        }
    }

    private int getListCount() {
        StealCountAction stealCountAction = new StealCountAction();
        onView(withId(android.R.id.list)).perform(stealCountAction);
        return stealCountAction.getCount();
    }
}
