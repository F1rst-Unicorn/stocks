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


import androidx.test.espresso.contrib.RecyclerViewActions;

import de.njsm.stocks.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static de.njsm.stocks.android.test.system.util.Matchers.atPosition;
import static junit.framework.TestCase.assertTrue;

public class LocationScreen extends AbstractListPresentingScreen {

    public LocationScreen addLocation(String name) {
        performFlakyAction(v -> {
            onView(withId(R.id.template_swipe_list_fab)).perform(click());
            onView(withHint(R.string.hint_location)).perform(replaceText(name));
        });

        onView(withText("OK")).perform(click());
        return this;
    }

    public LocationScreen assertLastItemIsNamed(String text) {
        int itemCount = getListCount();
        assertTrue(itemCount >= 0);

        int position = itemCount - 1;
        onView(withId(R.id.template_swipe_list_list))
                .perform(RecyclerViewActions.scrollToPosition(position))
                .check(matches(atPosition(position, withText(text))));

        return this;
    }
}