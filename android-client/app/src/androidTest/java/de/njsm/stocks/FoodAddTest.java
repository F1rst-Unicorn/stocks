package de.njsm.stocks;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
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
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.instanceOf;

@RunWith(Parameterized.class)
public class FoodAddTest {

    @Rule
    public ActivityTestRule<StartupActivity> mActivityRule = new ActivityTestRule<>(StartupActivity.class);

    @Parameterized.Parameter
    public String foodName;

    @Parameterized.Parameters(name = "Food {0}")
    public static Iterable<Object[]> getFoodNames() {
        return Arrays.asList(
                new Object[][] {
                        {"Beer"},
                        {"Carrot"},
                        {"Bread"},
                        {"Pepper"},
                        {"Cheese"}
                });
    }

    @Test
    public void addFood() throws Exception {
        onView(withId(R.id.fab)).perform(click());
        onView(withHint(R.string.hint_food)).perform(replaceText(foodName));
        onView(withText("OK")).perform(click());

        onView(withId(R.id.fragment_outline_cardview2)).perform(click());
        StealCountAction stealCountAction = new StealCountAction();
        onView(withId(android.R.id.list)).perform(stealCountAction);
        int position = stealCountAction.getCount() - 1;
        onData(anything()).inAdapterView(allOf(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), instanceOf(ListView.class)))
                .atPosition(position)
                .onChildView(withId(R.id.item_empty_food_outline_name))
                .check(matches(withText(foodName)));
        pressBack();


    }
}
