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
import de.njsm.stocks.android.frontend.main.MainActivity;
import de.njsm.stocks.screen.OutlineScreen;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

public class SearchTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @After
    public void tearDown() throws Exception {
        mActivityRule.finishActivity();
    }

    @Test
    public void searchWithoutResult() throws Exception {
        OutlineScreen.test()
                .search("Meat")
                .assertResultCount(0);
    }

    @Test
    public void searchSingleResult() throws Exception {
        String searchText = "Beer";
        OutlineScreen.test()
                .search("Beer")
                .assertResultCount(1)
                .assertItemContent(0, searchText, 2)
                .click(0)
                .assertTitle(searchText);
    }

    @Test
    public void searchMultipleResults() throws Exception {
        String searchText = "ee";
        OutlineScreen.test()
                .search(searchText)
                .assertResultCount(2);
    }
}
