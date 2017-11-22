package de.njsm.stocks;

import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.contrib.PickerActions;
import android.support.test.rule.ActivityTestRule;
import android.widget.DatePicker;
import android.widget.ListView;
import de.njsm.stocks.frontend.StartupActivity;
import de.njsm.stocks.util.StealCountAction;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static de.njsm.stocks.util.Matchers.matchesDate;
import static org.hamcrest.CoreMatchers.*;

public class FoodEditTest {

    @Rule
    public ActivityTestRule<StartupActivity> mActivityRule = new ActivityTestRule<>(StartupActivity.class);

    @Test
    public void editLastItemInList() throws Exception {
        onView(withId(R.id.fragment_outline_cardview)).perform(click());
        onData(anything()).inAdapterView(allOf(withEffectiveVisibility(Visibility.VISIBLE), instanceOf(ListView.class)))
                .atPosition(0)
                .perform(click());

        StealCountAction stealCountAction = new StealCountAction();
        onView(withId(android.R.id.list)).perform(stealCountAction);

        DataInteraction lastItem = onData(anything()).inAdapterView(allOf(withEffectiveVisibility(Visibility.VISIBLE), instanceOf(ListView.class)))
                .atPosition(stealCountAction.getCount()-1);
        lastItem.onChildView(withId(R.id.item_food_item_date))
                .check(matches(withText("31.12.00")));
        lastItem.onChildView(withId(R.id.item_food_item_user))
                .check(matches(withText("Jack")));
        lastItem.onChildView(withId(R.id.item_food_item_device))
                .check(matches(withText("Device")));
        lastItem.onChildView(withId(R.id.item_food_item_location))
                .check(matches(withText("Cupboard")));

        lastItem.perform(longClick());
        onView(withId(R.id.item_location_name))
                .check(matches(withText("Cupboard")));
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .check(matches(matchesDate(2100, 12, 31)));
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(2099, 12, 31));
        onView(withId(R.id.activity_add_food_item_done)).perform(click());
        lastItem.onChildView(withId(R.id.item_food_item_date))
                .check(matches(withText("31.12.99")));
        lastItem.onChildView(withId(R.id.item_food_item_user))
                .check(matches(withText("Jack")));
        lastItem.onChildView(withId(R.id.item_food_item_device))
                .check(matches(withText("Device")));
        lastItem.onChildView(withId(R.id.item_food_item_location))
                .check(matches(withText("Cupboard")));


        lastItem.perform(longClick());
        onView(withId(R.id.item_location_name))
                .check(matches(withText("Cupboard")));
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .check(matches(matchesDate(2099, 12, 31)));
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(2100, 12, 31));
        onView(withId(R.id.activity_add_food_item_done)).perform(click());

        lastItem.onChildView(withId(R.id.item_food_item_date))
                .check(matches(withText("31.12.00")));
        lastItem.onChildView(withId(R.id.item_food_item_user))
                .check(matches(withText("Jack")));
        lastItem.onChildView(withId(R.id.item_food_item_device))
                .check(matches(withText("Device")));
        lastItem.onChildView(withId(R.id.item_food_item_location))
                .check(matches(withText("Cupboard")));
    }

    @Test
    public void verifyMaximumLocationIsSelected() throws Exception {
        onView(withId(R.id.fragment_outline_cardview)).perform(click());
        onData(anything()).inAdapterView(allOf(withEffectiveVisibility(Visibility.VISIBLE), instanceOf(ListView.class)))
                .atPosition(0)
                .perform(click());
        onView(withId(R.id.fab)).perform(click());

        onView(withId(R.id.item_location_name))
                .check(matches(withText("Cupboard")));
    }

    @Test
    public void pretendEditingButPressBack() throws Exception {
        onView(withId(R.id.fragment_outline_cardview)).perform(click());
        onData(anything()).inAdapterView(allOf(withEffectiveVisibility(Visibility.VISIBLE), instanceOf(ListView.class)))
                .atPosition(0)
                .perform(click());

        StealCountAction stealCountAction = new StealCountAction();
        onView(withId(android.R.id.list)).perform(stealCountAction);

        DataInteraction lastItem = onData(anything()).inAdapterView(allOf(withEffectiveVisibility(Visibility.VISIBLE), instanceOf(ListView.class)))
                .atPosition(stealCountAction.getCount()-1);
        lastItem.onChildView(withId(R.id.item_food_item_date))
                .check(matches(withText("31.12.00")));
        lastItem.onChildView(withId(R.id.item_food_item_user))
                .check(matches(withText("Jack")));
        lastItem.onChildView(withId(R.id.item_food_item_device))
                .check(matches(withText("Device")));
        lastItem.onChildView(withId(R.id.item_food_item_location))
                .check(matches(withText("Cupboard")));

        lastItem.perform(longClick());
        pressBack();

        onView(withId(android.R.id.list)).perform(stealCountAction);
        lastItem = onData(anything()).inAdapterView(allOf(withEffectiveVisibility(Visibility.VISIBLE), instanceOf(ListView.class)))
                .atPosition(stealCountAction.getCount()-1);
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
