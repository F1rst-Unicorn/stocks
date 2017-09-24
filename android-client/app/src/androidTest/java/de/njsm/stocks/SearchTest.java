package de.njsm.stocks;

import android.support.test.espresso.DataInteraction;
import android.support.test.rule.ActivityTestRule;
import android.widget.ListView;
import de.njsm.stocks.frontend.StartupActivity;
import de.njsm.stocks.util.StealCountAction;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;

public class SearchTest {

    @Rule
    public ActivityTestRule<StartupActivity> mActivityRule = new ActivityTestRule<>(StartupActivity.class);

    @Test
    public void searchWithoutResult() throws Exception {
        String searchText = "Meat";

        onView(withId(R.id.action_search)).perform(click());
        onView(withId(R.id.search_src_text)).perform(replaceText(searchText));
        onView(withId(R.id.search_go_btn)).perform(click());

        StealCountAction stealCountAction = new StealCountAction();
        onView(withId(android.R.id.list)).perform(stealCountAction);

        assertEquals(0, stealCountAction.getCount());
    }

    @Test
    public void searchSingleResult() throws Exception {
        String searchText = "Beer";

        onView(withId(R.id.action_search)).perform(click());
        onView(withId(R.id.search_src_text)).perform(replaceText(searchText));
        onView(withId(R.id.search_go_btn)).perform(click());

        StealCountAction stealCountAction = new StealCountAction();
        onView(withId(android.R.id.list)).perform(stealCountAction);

        assertEquals(1, stealCountAction.getCount());

        DataInteraction item = onData(anything()).inAdapterView(instanceOf(ListView.class))
                .atPosition(0);
        item.onChildView(withId(R.id.item_food_amount_name))
                .check(matches(withText(searchText)));
        item.onChildView(withId(R.id.item_food_amount_amount))
                .check(matches(withText("1")));

        item.perform(click());
        onView(withText(searchText))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)));
    }

    @Test
    public void searchMultipleResults() throws Exception {
        String searchText = "ee";

        onView(withId(R.id.action_search)).perform(click());
        onView(withId(R.id.search_src_text)).perform(replaceText(searchText));
        onView(withId(R.id.search_go_btn)).perform(click());

        StealCountAction stealCountAction = new StealCountAction();
        onView(withId(android.R.id.list)).perform(stealCountAction);

        assertEquals(2, stealCountAction.getCount());
    }
}
