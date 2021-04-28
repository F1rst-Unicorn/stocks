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

package de.njsm.stocks.android.test.system;


import androidx.test.filters.LargeTest;

import org.junit.Test;

import de.njsm.stocks.android.test.system.screen.OutlineScreen;
import de.njsm.stocks.android.test.system.screen.ShoppingListScreen;

@LargeTest
public class ShoppingListTest extends SystemTest {

    @Test
    public void searchMultipleResults() throws Exception {
        OutlineScreen.test()
                .goToEatSoon()
                .swipeToShoppingList(0)
                .click(1)
                .toggleShoppingList()
                .pressBack()
                .pressBack();

        ShoppingListScreen list = OutlineScreen.test()
                .goToShoppingList();

        list.assertLength(2);

        list.click(0)
                .toggleShoppingList()
                .pressBack();

        list.assertLength(1);

    }
}
