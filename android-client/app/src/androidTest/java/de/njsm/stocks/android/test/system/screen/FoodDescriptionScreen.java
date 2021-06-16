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

import android.view.View;
import de.njsm.stocks.R;
import org.hamcrest.Matcher;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.AllOf.allOf;

public class FoodDescriptionScreen extends AbstractScreen {

    public FoodDescriptionScreen assertExpirationOffset(int expirationOffset) {
        onView(withId(R.id.fragment_food_description_standard_expiration))
                .check(matches(withText((String.valueOf(expirationOffset)))));
        return this;
    }

    public FoodDescriptionScreen assertDefaultLocation(String location) {
        sleep(500);
        onView(withId(R.id.fragment_food_description_location))
                .check(matches(withText(location)));
        return this;
    }

    public FoodScreen goToFoodItems() {
        Matcher<View> matcher = allOf(withTagValue(is("0")),
                isDescendantOfA(withId(R.id.fragment_food_item_tabs)));
        onView(matcher).perform(click());
        return new FoodScreen();
    }


}
