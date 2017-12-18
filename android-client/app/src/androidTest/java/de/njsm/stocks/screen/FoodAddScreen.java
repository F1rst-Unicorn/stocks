package de.njsm.stocks.screen;

import android.support.test.espresso.contrib.PickerActions;
import android.widget.DatePicker;
import de.njsm.stocks.R;
import org.hamcrest.Matchers;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.anything;

public class FoodAddScreen extends AbstractScreen {

    public FoodAddScreen selectLocation(int itemIndex) {
        onView(withId(R.id.activity_add_food_item_spinner)).perform(click());
        onData(anything()).atPosition(itemIndex).perform(click());
        return this;
    }

    public FoodAddScreen selectDate(int year, int month, int day) {
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(year, month, day));
        return this;
    }

    public FoodScreen addAndFinish() {
        onView(withId(R.id.activity_add_food_item_done)).perform(click());
        return new FoodScreen();
    }

    public FoodAddScreen addItem() {
        onView(withId(R.id.activity_add_food_item_add_more)).perform(click());
        return this;
    }

    public FoodAddScreen addManyItems(int number) {
        for (int i = 0; i < number; i++) {
            onView(withId(R.id.activity_add_food_item_add_more)).perform(click());
        }
        return this;
    }
}
