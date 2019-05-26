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


import androidx.test.espresso.contrib.RecyclerViewActions;
import de.njsm.stocks.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static de.njsm.stocks.util.Matchers.atPosition;
import static junit.framework.TestCase.assertEquals;

public class UserScreen extends AbstractListPresentingScreen {

    public UserScreen addUser(String name) {
        onView(withId(R.id.template_swipe_list_fab)).perform(click());
        onView(withHint(R.string.hint_username)).perform(replaceText(name));
        onView(withText("OK")).perform(click());
        sleep(100);
        return this;
    }

    public UserScreen assertUser(int index, String name) {
        checkIndex(index);
        onView(withId(R.id.template_swipe_list_list))
                .perform(RecyclerViewActions.scrollToPosition(index))
                .check(matches(atPosition(index, withText(name))));

        return this;
    }

    public DeviceScreen selectUser(int index) {
        checkIndex(index);
        onView(withId(R.id.template_swipe_list_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(index, click()));
        return new DeviceScreen();
    }

    public UserScreen removeUser(int index) {
        checkIndex(index);
        onView(withId(R.id.template_swipe_list_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(index, swipeRight()));
        onView(withText(R.string.action_undo))
                .perform(swipeRight());
        sleep(1000);
        return this;
    }

    public void assertEmpty() {
        assertEquals(1, getListCount());
    }
}
