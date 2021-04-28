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
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.NumberPicker;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.Locale;

import de.njsm.stocks.R;
import de.njsm.stocks.android.test.system.SystemTestSuite;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withChild;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static de.njsm.stocks.android.test.system.util.Matchers.atPosition;
import static junit.framework.TestCase.fail;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.anything;
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

    public FoodAddScreen addItems() {
        onView(withId(R.id.fragment_food_item_list_fab)).perform(click());
        return new FoodAddScreen();
    }

    public FoodAddScreen longClick(int index) {
        checkIndex(index);
        onView(withId(R.id.fragment_food_item_list_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(index, ViewActions.longClick()));
        return new FoodAddScreen();
    }

    public FoodAddScreen longClickLast() {
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

    public FoodScreen setExpirationOffset(int offset) {
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext());
        onView(withText(R.string.title_expiration_offset)).perform(click());
        onView(withId(R.id.number_picker_picker)).perform(setNumber(offset));
        onView(withText("OK")).perform(click());
        return this;
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

    public FoodScreen setDefaultLocation(int index) {
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext());
        onView(allOf(withId(R.id.title), anyOf(withText("Default Location"), withText("Standardort")))).perform(click());
        sleep(1000);
        onView(allOf(withId(R.id.spinner_spinner),
                        childAtPosition(
                                allOf(withId(R.id.custom),
                                        childAtPosition(
                                                withId(R.id.customPanel),
                                                0)),
                                0),
                        isDisplayed())).perform(click());
        onData(anything()).inRoot(isPlatformPopup()).atPosition(index).perform(click());
        onView(withText("OK")).perform(click());
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

    public static ViewAction setNumber(final int num) {
        return new ViewAction() {
            @Override
            public void perform(UiController uiController, View view) {
                NumberPicker np = (NumberPicker) view;
                np.setValue(num);
            }

            @Override
            public String getDescription() {
                return "Set the passed number into the NumberPicker";
            }

            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(NumberPicker.class);
            }
        };
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

}
