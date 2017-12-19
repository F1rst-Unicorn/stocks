package de.njsm.stocks.screen;

import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.widget.ListView;
import de.njsm.stocks.R;
import de.njsm.stocks.SystemTestSuite;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.Locale;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.fail;

public class FoodScreen extends AbstractListPresentingScreen {

    public FoodScreen assertTitle(String title) {
        onView(withText(title))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        return this;
    }

    public FoodAddScreen addItems() {
        onView(withId(R.id.fab)).perform(click());
        return new FoodAddScreen();
    }

    public FoodAddScreen longClick(int index) {
        checkIndex(index);
        onData(anything()).inAdapterView(allOf(withEffectiveVisibility(Visibility.VISIBLE), instanceOf(ListView.class)))
                .atPosition(getListCount()-1)
                .perform(ViewActions.longClick());
        return new FoodAddScreen();
    }

    public FoodAddScreen longClickLast() {
        return longClick(getListCount()-1);
    }

    public BarcodeScreen goToBarCodes() {
        onView(withId(R.id.activity_food_menu_ean)).perform(click());
        return new BarcodeScreen();
    }

    public FoodScreen eatAllButOne() {
        int counter = 0;
        while (getListCount() > 1) {
            onData(anything()).inAdapterView(allOf(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), instanceOf(ListView.class)))
                    .atPosition(0)
                    .perform(click());
            onView(withText("OK")).perform(click());

            if (counter++ > SystemTestSuite.LOOP_BREAKER) {
                fail("LOOP BREAKER triggered, list count is " + getListCount());
            }
        }
        return this;
    }

    public FoodScreen assertItem(int index, String user, String device, String date, String location) {
        checkIndex(index);
        DataInteraction item = onData(anything()).inAdapterView(allOf(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), instanceOf(ListView.class)))
                .atPosition(index);
        item.onChildView(withId(R.id.item_food_item_date))
                .check(matches(withText(date)));
        item.onChildView(withId(R.id.item_food_item_user))
                .check(matches(withText(user)));
        item.onChildView(withId(R.id.item_food_item_device))
                .check(matches(withText(device)));
        item.onChildView(withId(R.id.item_food_item_location))
                .check(matches(withText(location)));
        return this;
    }

    public FoodScreen assertItem(int index, String user, String device, LocalDate date, String location) {
        return assertItem(index, user, device, DateTimeFormatter.ofPattern("dd.MM.yy", Locale.US).format(date), location);
    }

    public FoodScreen assertLastItem(String user, String device, String date, String location) {
        assertItem(getListCount()-1, user, device, date, location);
        return this;
    }
}
