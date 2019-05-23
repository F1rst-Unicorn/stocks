package de.njsm.stocks.screen;

import android.widget.DatePicker;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.matcher.ViewMatchers;
import de.njsm.stocks.R;
import org.hamcrest.Matchers;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static de.njsm.stocks.util.Matchers.matchesDate;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anything;

public class FoodAddScreen extends AbstractScreen {

    public FoodAddScreen selectLocation(int itemIndex) {
        onView(withId(R.id.fragment_add_food_item_spinner)).perform(click());
        onData(anything()).atPosition(itemIndex).perform(click());
        return this;
    }

    public FoodAddScreen assertLocation(String text) {
        onView(withId(R.id.item_location_name))
                .check(matches(allOf(withText(text), withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))));
        return this;
    }

    public FoodAddScreen selectDate(int year, int month, int day) {
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(year, month, day));
        return this;
    }

    public FoodAddScreen assertDate(int year, int month, int day) {
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .check(matches(matchesDate(year, month, day)));
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .check(matches(matchesDate(year, month, day)));
        return this;
    }

    public FoodScreen addAndFinish() {
        onView(withId(R.id.fragment_add_item_options_done)).perform(click());
        return new FoodScreen();
    }

    public FoodAddScreen addItem() {
        onView(withId(R.id.fragment_add_item_options_add_more)).perform(click());
        return this;
    }

    public FoodScreen editItem() {
        onView(withId(R.id.fragment_edit_item_options_done)).perform(click());
        return new FoodScreen();
    }

    @Override
    public FoodScreen pressBack() {
        super.pressBack();
        return new FoodScreen();
    }

    public FoodAddScreen addManyItems(int number) {
        for (int i = 0; i < number; i++) {
            addItem();
        }
        return this;
    }
}
