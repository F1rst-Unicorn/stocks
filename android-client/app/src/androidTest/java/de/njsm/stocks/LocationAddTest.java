package de.njsm.stocks;

import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.rule.ActivityTestRule;
import android.view.Gravity;
import android.widget.ListView;
import de.njsm.stocks.frontend.StartupActivity;
import de.njsm.stocks.util.StealCountAction;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.CoreMatchers.*;

@RunWith(Parameterized.class)
public class LocationAddTest {

    @Rule
    public ActivityTestRule<StartupActivity> mActivityRule = new ActivityTestRule<>(StartupActivity.class);

    @Parameterized.Parameter
    public String locationName;

    @Parameterized.Parameters(name = "Location {0}")
    public static Iterable<Object[]> getFoodNames() {
        return Arrays.asList(
                new Object[][] {
                        {"Fridge"},
                        {"Ground"},
                        {"Cupboard"},
                        {"Basement"}
                });
    }

    @Test
    public void addLocation() throws Exception {
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.START)))
                .perform(open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.locations));

        onView(withId(R.id.fab)).perform(click());
        onView(withHint(R.string.hint_location)).perform(replaceText(locationName));
        onView(withText("OK")).perform(click());

        StealCountAction stealCountAction = new StealCountAction();
        onView(withId(android.R.id.list)).perform(stealCountAction);
        int position = stealCountAction.getCount() - 1;
        onData(anything()).inAdapterView(allOf(withEffectiveVisibility(Visibility.VISIBLE), instanceOf(ListView.class)))
                .atPosition(position)
                .onChildView(withId(R.id.item_location_name))
                .check(matches(withText(locationName)));
        pressBack();
    }
}
