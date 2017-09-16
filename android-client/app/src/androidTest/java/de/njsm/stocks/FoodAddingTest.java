package de.njsm.stocks;

import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.contrib.PickerActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.widget.DatePicker;
import android.widget.ListView;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.CoreMatchers.*;

public class FoodAddingTest {

    @Rule
    public ActivityTestRule<StartupActivity> mActivityRule = new ActivityTestRule<>(StartupActivity.class);

    @Test
    public void addFoodItems() throws Exception {
        onView(withId(R.id.fragment_outline_cardview2)).perform(click());
        onData(anything()).inAdapterView(allOf(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), instanceOf(ListView.class)))
                .atPosition(0)
                .perform(click());
        onView(withId(R.id.fab)).perform(click());

        onView(withId(R.id.activity_add_food_item_add_more)).perform(click());
        onView(withId(R.id.activity_add_food_item_add_more)).perform(click());

        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(2100, 31, 12));

        Thread.sleep(2000);
        onView(withId(R.id.activity_add_food_item_done)).perform(click());

        DataInteraction firstItem = onData(anything()).inAdapterView(allOf(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), instanceOf(ListView.class)))
                .atPosition(0);
        firstItem.onChildView(withId(R.id.item_food_item_date))
                .check(matches(withText(new SimpleDateFormat("dd.MM.yy", Locale.US).format(new Date()))));
        firstItem.onChildView(withId(R.id.item_food_item_user))
                .check(matches(withText("Jack")));
        firstItem.onChildView(withId(R.id.item_food_item_device))
                .check(matches(withText("Device")));
        firstItem.onChildView(withId(R.id.item_food_item_location))
                .check(matches(withText("Fridge")));

        DataInteraction middleItem = onData(anything()).inAdapterView(allOf(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), instanceOf(ListView.class)))
                .atPosition(0);
        middleItem.onChildView(withId(R.id.item_food_item_date))
                .check(matches(withText(new SimpleDateFormat("dd.MM.yy", Locale.US).format(new Date()))));
        middleItem.onChildView(withId(R.id.item_food_item_user))
                .check(matches(withText("Jack")));
        middleItem.onChildView(withId(R.id.item_food_item_device))
                .check(matches(withText("Device")));
        middleItem.onChildView(withId(R.id.item_food_item_location))
                .check(matches(withText("Fridge")));

        DataInteraction lastItem = onData(anything()).inAdapterView(allOf(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), instanceOf(ListView.class)))
                .atPosition(2);
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
