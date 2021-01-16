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

package de.njsm.stocks;


import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import de.njsm.stocks.android.frontend.main.MainActivity;
import de.njsm.stocks.screen.OutlineScreen;

public class FoodConsumptionTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @After
    public void tearDown() throws Exception {
        mActivityRule.finishActivity();
    }

    @Test
    @Ignore("espresso is really stupid. If it shall swipe right on a " +
            "view inside the leftmost tab of a tab view it tries to move " +
            "the tab view instead of swiping the element. When swiping " +
            "manually this is not a problem")
    public void removeItemsUntilOneIsLeft() throws Exception {
        OutlineScreen.test()
                .goToEatSoon()
                .click(0)
                .eatAllButOne()
                .assertItem(0, "Jack", "android-client", "31.12.00", "Freezer");
    }
}
