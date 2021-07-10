/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2021  The stocks developers
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

import com.google.android.material.textfield.TextInputEditText;
import de.njsm.stocks.R;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;

public class FoodEditScreen extends AbstractScreen {

    public FoodEditScreen setExpirationOffset(int expirationOffset) {
        onView(allOf(
                isDescendantOfA(withId(R.id.fragment_food_edit_expiration_offset)),
                withClassName(is(TextInputEditText.class.getName()))))
        .perform(replaceText(String.valueOf(expirationOffset)));
        return this;
    }

    /**
     * Select index 0 to set zero
     */
    public FoodEditScreen setLocation(int index) {
        onView(withId(R.id.fragment_food_edit_location)).perform(click());
        onData(anything()).atPosition(index).perform(click());
        return this;
    }

    public FoodScreen save() {
        onView(withId(R.id.fragment_food_edit_options_save)).perform(click());
        return new FoodScreen();
    }
}
