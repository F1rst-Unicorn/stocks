package de.njsm.stocks;

import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.rule.ActivityTestRule;
import android.view.Gravity;
import android.widget.ListView;
import de.njsm.stocks.frontend.StartupActivity;
import de.njsm.stocks.util.StealCountAction;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.CoreMatchers.*;

public class InitialDataSetupTest {

    @Rule
    public ActivityTestRule<StartupActivity> mActivityRule = new ActivityTestRule<>(StartupActivity.class);

    @Test
    public void addInitialFood() throws Exception {
        String foodName = "Beer";

        onView(withId(R.id.fab)).perform(click());
        onView(withHint(R.string.hint_food)).perform(replaceText(foodName));
        onView(withText("OK")).perform(click());

        onView(withId(R.id.fragment_outline_cardview2)).perform(click());
        StealCountAction stealCountAction = new StealCountAction();
        onView(withId(android.R.id.list)).perform(stealCountAction);
        int position = stealCountAction.getCount() - 1;
        onData(anything()).inAdapterView(allOf(withEffectiveVisibility(Visibility.VISIBLE), instanceOf(ListView.class)))
                .atPosition(position)
                .onChildView(withId(R.id.item_empty_food_outline_name))
                .check(matches(withText(foodName)));
        pressBack();
    }

    @Test
    public void addMoreFood() throws Exception {
        String foodName = "Carrot";

        onView(withId(R.id.fab)).perform(click());
        onView(withHint(R.string.hint_food)).perform(replaceText(foodName));
        onView(withText("OK")).perform(click());

        onView(withId(R.id.fragment_outline_cardview2)).perform(click());
        StealCountAction stealCountAction = new StealCountAction();
        onView(withId(android.R.id.list)).perform(stealCountAction);
        int position = stealCountAction.getCount() - 1;
        onData(anything()).inAdapterView(allOf(withEffectiveVisibility(Visibility.VISIBLE), instanceOf(ListView.class)))
                .atPosition(position)
                .onChildView(withId(R.id.item_empty_food_outline_name))
                .check(matches(withText(foodName)));
        pressBack();
    }

    @Test
    public void addInitialLocation() throws Exception {
        String locationName = "Fridge";

        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.START)))
                .perform(open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.locations));

        onView(withId(R.id.fab)).perform(click());
        onView(withHint(R.string.hint_location)).perform(replaceText(locationName));
        onView(withText("OK")).perform(click());

        onData(anything()).inAdapterView(allOf(withEffectiveVisibility(Visibility.VISIBLE), instanceOf(ListView.class)))
                .atPosition(0)
                .onChildView(withId(R.id.item_location_name))
                .check(matches(withText(locationName)));
        pressBack();
    }



}
