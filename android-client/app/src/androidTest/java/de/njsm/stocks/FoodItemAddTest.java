package de.njsm.stocks;

import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.contrib.PickerActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.widget.DatePicker;
import android.widget.ListView;
import de.njsm.stocks.frontend.StartupActivity;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.Arrays;
import java.util.Locale;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.CoreMatchers.*;

@RunWith(Parameterized.class)
public class FoodItemAddTest {

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
    public void addFoodItems() throws Exception {
        onView(withId(R.id.fragment_outline_cardview2)).perform(click());
        onData(anything()).inAdapterView(allOf(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), instanceOf(ListView.class)))
                .atPosition(0)
                .perform(click());
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.activity_add_food_item_spinner)).perform(click());
        onData(anything()).atPosition(2).perform(click());

        for (int i = 0; i < targetFoodAmount; i++) {
            onView(withId(R.id.activity_add_food_item_add_more)).perform(click());
        }

        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(2100, 31, 12));

        Thread.sleep(2000); // STOCKS-17
        onView(withId(R.id.activity_add_food_item_done)).perform(click());

        for (int i = 0; i < targetFoodAmount; i++) {
            DataInteraction item = onData(anything()).inAdapterView(allOf(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), instanceOf(ListView.class)))
                    .atPosition(i);
            item.onChildView(withId(R.id.item_food_item_date))
                    .check(matches(withText(DateTimeFormatter.ofPattern("dd.MM.yy", Locale.US).format(LocalDate.now()))));
            item.onChildView(withId(R.id.item_food_item_user))
                    .check(matches(withText("Jack")));
            item.onChildView(withId(R.id.item_food_item_device))
                    .check(matches(withText("Device")));
            item.onChildView(withId(R.id.item_food_item_location))
                    .check(matches(withText("Cupboard")));
        }

        DataInteraction lastItem = onData(anything()).inAdapterView(allOf(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), instanceOf(ListView.class)))
                .atPosition(targetFoodAmount);
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
