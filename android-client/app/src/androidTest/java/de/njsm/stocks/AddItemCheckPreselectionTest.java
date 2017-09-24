package de.njsm.stocks;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.widget.ListView;
import de.njsm.stocks.frontend.StartupActivity;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.CoreMatchers.*;

public class AddItemCheckPreselectionTest {

    @Rule
    public ActivityTestRule<StartupActivity> mActivityRule = new ActivityTestRule<>(StartupActivity.class);

    @Test
    public void testPreselection() throws Exception {
        onView(withId(R.id.fragment_outline_cardview)).perform(click());
        onData(anything()).inAdapterView(allOf(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), instanceOf(ListView.class)))
                .atPosition(0)
                .perform(click());
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.activity_add_food_item_spinner)).check(matches(withSpinnerText(containsString("Cupboard"))));
    }
}
