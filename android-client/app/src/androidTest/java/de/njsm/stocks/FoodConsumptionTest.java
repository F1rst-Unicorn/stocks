package de.njsm.stocks;

import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.widget.ListView;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.CoreMatchers.*;

public class FoodConsumptionTest {

    @Rule
    public ActivityTestRule<StartupActivity> mActivityRule = new ActivityTestRule<>(StartupActivity.class);

    @Test
    public void drinkTwoBeer() throws Exception {
        onView(withId(R.id.fragment_outline_cardview)).perform(click());
        onData(anything()).inAdapterView(allOf(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), instanceOf(ListView.class)))
                .atPosition(0)
                .perform(click());
        onData(anything()).inAdapterView(allOf(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), instanceOf(ListView.class)))
                .atPosition(0)
                .perform(click());
        onView(withText("OK")).perform(click());
        onData(anything()).inAdapterView(allOf(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), instanceOf(ListView.class)))
                .atPosition(0)
                .perform(click());
        onView(withText("OK")).perform(click());

        DataInteraction lastItem = onData(anything()).inAdapterView(allOf(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), instanceOf(ListView.class)))
                .atPosition(0);
        lastItem.onChildView(withId(R.id.item_food_item_date))
                .check(matches(withText("31.12.00")));
        lastItem.onChildView(withId(R.id.item_food_item_user))
                .check(matches(withText("Jack")));
        lastItem.onChildView(withId(R.id.item_food_item_device))
                .check(matches(withText("Device")));
        lastItem.onChildView(withId(R.id.item_food_item_location))
                .check(matches(withText("Fridge")));

    }
}
