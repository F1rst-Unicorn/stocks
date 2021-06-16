/* stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.njsm.stocks.android.test.system.screen;


import android.view.View;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import de.njsm.stocks.R;
import de.njsm.stocks.android.test.system.SystemTestSuite;
import org.hamcrest.Matcher;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.Locale;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static de.njsm.stocks.android.test.system.Matchers.childAtPosition;
import static de.njsm.stocks.android.test.system.util.Matchers.atPosition;
import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.core.AllOf.allOf;

public class FoodScreen extends AbstractListPresentingScreen {

    public FoodScreen() {
        super(R.id.fragment_food_item_list_list);
    }

    public FoodScreen assertTitle(String title) {
        onView(withText(title))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        return this;
    }

    public FoodItemAddScreen addItems() {
        onView(withId(R.id.fragment_food_item_list_fab)).perform(click());
        return new FoodItemAddScreen();
    }

    public FoodItemAddScreen longClick(int index) {
        checkIndex(index);
        onView(withId(R.id.fragment_food_item_list_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(index, ViewActions.longClick()));
        return new FoodItemAddScreen();
    }

    public FoodItemAddScreen longClickLast() {
        return longClick(getListCount()-1);
    }

    public BarcodeScreen goToBarCodes() {
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext());
        onView(withText(R.string.title_barcode)).perform(click());
        return new BarcodeScreen();
    }

    public FoodScreen toggleShoppingList() {
        onView(withId(R.id.fragment_food_item_options_shopping)).perform(click());
        return this;
    }

    public FoodScreen assertExpirationOffset(int offset) {
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext());
        onView(withText(R.string.title_expiration_offset)).perform(click());
        onView(withId(R.id.number_picker_picker)).check(matches(withChild(withText(String.valueOf(offset)))));
        onView(anyOf(withText("CANCEL"), withText("ABBRECHEN"))).perform(click());
        return this;
    }

    public FoodEditScreen edit() {
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext());
        onView(withText(R.string.dialog_edit)).perform(click());
        return new FoodEditScreen();
    }

    public FoodScreen assertDefaultLocation(String location) {
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext());
        onView(allOf(withId(R.id.title), anyOf(withText("Default Location"), withText("Standardort")))).perform(click());
        onView(allOf(withId(R.id.spinner_spinner),
                childAtPosition(
                        allOf(withId(R.id.custom),
                                childAtPosition(
                                        withId(R.id.customPanel),
                                        0)),
                        0),
                isDisplayed())).check(matches(withChild(withText(location))));
        onView(anyOf(withText("CANCEL"), withText("ABBRECHEN"))).perform(click());
        return this;
    }

    public FoodScreen eatAllButOne() {
        int counter = 0;
        while (getListCount() > 1) {
            onView(withId(R.id.fragment_food_item_list_list))
                    .perform(
                            RecyclerViewActions.actionOnItemAtPosition(0, click()),
                            RecyclerViewActions.actionOnItemAtPosition(0, swipeUp()),
                            RecyclerViewActions.actionOnItemAtPosition(0, swipeRight()));

            sleep(1000);
            if (counter++ > SystemTestSuite.LOOP_BREAKER) {
                fail("LOOP BREAKER triggered, list count is " + getListCount());
            }
        }
        return this;
    }

    public FoodScreen assertItem(int index, String user, String device, String date, String location) {
        checkIndex(index);
        ViewInteraction item = onView(withId(R.id.fragment_food_item_list_list))
                .perform(RecyclerViewActions.scrollToPosition(index));

        item.check(matches(atPosition(index, withChild(allOf(withId(R.id.item_food_item_date),
                                 withText(date))))));
        item.check(matches(atPosition(0, withChild(allOf(withId(R.id.item_food_item_user),
                                 withText(user))))));
        item.check(matches(atPosition(0, withChild(allOf(withId(R.id.item_food_item_device),
                                 withText(device))))));
        item.check(matches(atPosition(0, withChild(allOf(withId(R.id.item_food_item_location),
                                 withText(location))))));
        return this;
    }

    public FoodScreen assertItem(int index, String user, String device, LocalDate date, String location) {
        return assertItem(index, user, device, DateTimeFormatter.ofPattern("dd.MM.yy", Locale.US).format(date), location);
    }

    public FoodScreen assertLastItem(String user, String device, String date, String location) {
        assertItem(getListCount()-1, user, device, date, location);
        return this;
    }

    public FoodDescriptionScreen goToFoodDescription() {
        Matcher<View> matcher = allOf(withTagValue(is("1")),
                isDescendantOfA(withId(R.id.fragment_food_item_tabs)));
        onView(matcher).perform(click());
        return new FoodDescriptionScreen();
    }
}
