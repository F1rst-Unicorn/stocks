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
import static de.njsm.stocks.android.test.system.util.Matchers.matchesDate;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anything;

public class FoodAddScreen extends AbstractScreen {

    public FoodAddScreen selectLocation(int itemIndex) {
        onView(withId(R.id.fragment_add_food_item_location)).perform(click());
        onData(anything()).atPosition(itemIndex).perform(click());
        return this;
    }

    public FoodAddScreen assertLocation(String text) {
        onView(withId(R.id.item_location_name))
                .check(matches(allOf(withText(text), withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))));
        return this;
    }

    public FoodAddScreen selectDate(int year, int month, int day) {
        sleep(100);
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(year, month, day));
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(year, month, day));
        sleep(100);
        return this;
    }

    public FoodAddScreen assertDate(int year, int month, int day) {
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
