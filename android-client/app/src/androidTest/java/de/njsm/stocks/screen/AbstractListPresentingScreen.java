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


import de.njsm.stocks.R;
import de.njsm.stocks.util.StealCountAction;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.TestCase.fail;

public class AbstractListPresentingScreen extends AbstractScreen {

    private int listId;

    public AbstractListPresentingScreen() {
        this(R.id.template_swipe_list_list);
    }

    public AbstractListPresentingScreen(int listId) {
        this.listId = listId;
    }

    protected void checkIndex(int itemIndex) {
        int count = getListCount();
        if (itemIndex < 0 || itemIndex >= count) {
            fail("index " + itemIndex + " is not in valid range [0," + count + ")");
        }
    }

    protected int getListCount() {
        return getListCount(listId);
    }

    protected int getListCount(int listId) {
        StealCountAction stealCountAction = new StealCountAction();
        onView(withId(listId)).perform(stealCountAction);
        return stealCountAction.getCount();
    }

}
