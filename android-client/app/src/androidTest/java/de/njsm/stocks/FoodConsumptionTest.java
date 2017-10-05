package de.njsm.stocks;

import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.widget.ListView;
import de.njsm.stocks.frontend.StartupActivity;
import de.njsm.stocks.util.StealCountAction;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.fail;

public class FoodConsumptionTest {

    @Rule
    public ActivityTestRule<StartupActivity> mActivityRule = new ActivityTestRule<>(StartupActivity.class);

    @Parameterized.Parameter
    public int targetFoodAmount;

    @Parameterized.Parameters(name = "Food at position {0}")
    public static Iterable<Object[]> getFoodNames() {
        return Arrays.asList(
                new Object[][] {
                        {0},
                        {1},
                        {2},
                        {3},
                        {4}
                });
    }

    @Test
    public void removeItemsUntilOneIsLeft() throws Exception {
        onView(withId(R.id.fragment_outline_cardview)).perform(click());
        onData(anything()).inAdapterView(allOf(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), instanceOf(ListView.class)))
                .atPosition(0)
                .perform(click());

        StealCountAction stealCountAction = new StealCountAction();
        onView(withId(android.R.id.list)).perform(stealCountAction);
        int counter = 0;
        while (stealCountAction.getCount() > 1) {
            onData(anything()).inAdapterView(allOf(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), instanceOf(ListView.class)))
                    .atPosition(0)
                    .perform(click());
            onView(withText("OK")).perform(click());

            onView(withId(android.R.id.list)).perform(stealCountAction);
            Thread.sleep(2000); // STOCKS-17
            if (counter++ > SystemTestSuite.LOOP_BREAKER) {
                fail();
            }
        }

        DataInteraction lastItem = onData(anything()).inAdapterView(allOf(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), instanceOf(ListView.class)))
                .atPosition(0);
        lastItem.onChildView(withId(R.id.item_food_item_date))
                .check(matches(withText("31.12.00")));
        lastItem.onChildView(withId(R.id.item_food_item_user))
                .check(matches(withText("Jack")));
        lastItem.onChildView(withId(R.id.item_food_item_device))
                .check(matches(withText("Device")));
        lastItem.onChildView(withId(R.id.item_food_item_location))
                .check(matches(withText("Cupboard")));

    }
}
