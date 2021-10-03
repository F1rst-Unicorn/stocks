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
import java.time.LocalDate;

import de.njsm.stocks.android.test.system.screen.FoodScreen;
import de.njsm.stocks.android.test.system.screen.OutlineScreen;

@LargeTest
public class FoodItemAddTest extends SystemTest {

    @Test
    public void addFoodItems() throws Exception {
        int numberOfItems = 1;
        FoodScreen finalScreen = OutlineScreen.test()
                .goToEmptyFood()
                .click(0)
                .addItems()
                .selectLocation(2)
                .assertLocation("Freezer")
                .addManyItems(numberOfItems)
                .selectDate(2100, 12, 31)
                .addAndFinish();

        for (int i = 0; i < numberOfItems; i++) {
            finalScreen.assertItem(i, "Jack", "android-client", LocalDate.now(), "Freezer");
        }

        finalScreen.assertItem(numberOfItems, "Jack", "android-client", "31.12.00", "Freezer");
    }

    @Test
    public void addFromExplicitDefaults() {
        int offset = 42;
        LocalDate expected = LocalDate.now().plusDays(offset);
        OutlineScreen.test()
                .goToEmptyFood()
                .click(0)
                .edit()
                .setExpirationOffset(offset)
                .setLocation(5)
                .save()
                .goToFoodDescription()
                .assertExpirationOffset(offset)
                .assertDefaultLocation("Ground")
                .goToFoodItems()
                .addItems()
                .assertDate(expected.getYear(), expected.getMonthValue(), expected.getDayOfMonth())
                .assertLocation("Ground");
    }
}
