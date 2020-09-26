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

import android.view.Gravity;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.NavigationViewActions;

import de.njsm.stocks.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerActions.open;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

public class OutlineScreen extends AbstractScreen {

    public SearchScreen search(String searchText) {
        onView(withId(R.id.fragment_outline_options_search)).perform(click());
        onView(withId(R.id.search_src_text)).perform(replaceText(searchText));
        onView(withId(R.id.search_go_btn)).perform(click());

        return new SearchScreen();
    }

    public OutlineScreen addFoodType(String name) {
        onView(withId(R.id.fragment_outline_fab)).perform(click());
        onView(withHint(R.string.hint_food)).perform(replaceText(name));
        onView(withText("OK")).perform(click());
        return this;
    }

    public EmptyFoodScreen goToEmptyFood() {
        onView(withId(R.id.fragment_outline_content_scroll_view)).perform(swipeDown());
        onView(withId(R.id.fragment_outline_content_cardview2))
                .perform(click());
        return new EmptyFoodScreen();
    }

    public LocationScreen goToLocations() {
        onView(withId(R.id.main_drawer_layout))
                .check(matches(isClosed(Gravity.START)))
                .perform(open());
        onView(withId(R.id.main_nav)).perform(NavigationViewActions.navigateTo(R.id.activity_main_drawer_locations));
        return new LocationScreen();
    }

    public UserScreen goToUsers() {
        onView(withId(R.id.main_drawer_layout))
                .check(matches(isClosed(Gravity.START)))
                .perform(open());
        onView(withId(R.id.main_nav)).perform(NavigationViewActions.navigateTo(R.id.activity_main_drawer_users));
        return new UserScreen();
    }

    public ShoppingListScreen goToShoppingList() {
        onView(withId(R.id.main_drawer_layout))
                .check(matches(isClosed(Gravity.START)))
                .perform(open());
        onView(withId(R.id.main_nav)).perform(NavigationViewActions.navigateTo(R.id.activity_main_drawer_shopping_list));
        return new ShoppingListScreen();
    }

    public EatSoonScreen goToEatSoon() {
        onView(withId(R.id.fragment_outline_content_scroll_view)).perform(swipeDown());
        onView(withId(R.id.fragment_outline_content_cardview))
                .perform(click());
        return new EatSoonScreen();
    }

    public FoodScreen scanSuccessful() {
        onView(withId(R.id.fragment_outline_options_scan)).perform(click());
        sleep(500);
        return new FoodScreen();
    }

    public AllFoodScreen scanFailing() {
        onView(withId(R.id.fragment_outline_options_scan)).perform(click());
        sleep(500);
        return new AllFoodScreen();
    }

    public static OutlineScreen test() {
        return new OutlineScreen();
    }

    public OutlineScreen assertRegistrationSuccess() {
        onView(withId(android.R.id.message))
                .check(matches(withText(R.string.dialog_finished)));

        ViewInteraction appCompatButton = onView(
                allOf(withId(android.R.id.button1), withText("OK")));
        appCompatButton.perform(scrollTo(), click());
        return this;
    }
}
