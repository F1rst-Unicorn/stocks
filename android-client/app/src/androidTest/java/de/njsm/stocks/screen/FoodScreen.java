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

package de.njsm.stocks.screen;


import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import de.njsm.stocks.R;
import de.njsm.stocks.SystemTestSuite;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.Locale;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static de.njsm.stocks.util.Matchers.atPosition;
import static junit.framework.TestCase.fail;
import static org.hamcrest.core.AllOf.allOf;

public class FoodScreen extends AbstractListPresentingScreen {

    public FoodScreen assertTitle(String title) {
        onView(withText(title))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        return this;
    }

    public FoodAddScreen addItems() {
        onView(withId(R.id.template_swipe_list_fab)).perform(click());
        return new FoodAddScreen();
    }

    public FoodAddScreen longClick(int index) {
        checkIndex(index);
        onView(withId(R.id.template_swipe_list_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(index, ViewActions.longClick()));
        return new FoodAddScreen();
    }

    public FoodAddScreen longClickLast() {
        return longClick(getListCount()-1);
    }

    public BarcodeScreen goToBarCodes() {
        onView(withId(R.id.fragment_food_item_options_ean)).perform(click());
        return new BarcodeScreen();
    }

    public FoodScreen toggleShoppingList() {
        onView(withId(R.id.fragment_food_item_options_shopping)).perform(click());
        return this;
    }

    public FoodScreen eatAllButOne() {
        int counter = 0;
        while (getListCount() > 1) {
            onView(withId(R.id.template_swipe_list_list))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(0, swipeRight()));

            onView(withText(R.string.action_undo))
                    .perform(swipeRight());
            sleep(700);

            if (counter++ > SystemTestSuite.LOOP_BREAKER) {
                fail("LOOP BREAKER triggered, list count is " + getListCount());
            }
        }
        return this;
    }

    public FoodScreen assertItem(int index, String user, String device, String date, String location) {
        checkIndex(index);
        ViewInteraction item = onView(withId(R.id.template_swipe_list_list))
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
}
