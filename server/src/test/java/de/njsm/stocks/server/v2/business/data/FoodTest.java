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

package de.njsm.stocks.server.v2.business.data;

import org.junit.Test;

import java.time.Period;

import static org.junit.Assert.assertEquals;

public class FoodTest {

    @Test
    public void testHashCode() {
        Food data = new Food(1, "Bread", 2, true, Period.ZERO, 1, "");

        assertEquals(141675498, data.hashCode());
    }

    @Test
    public void testToString() {
        Food data = new Food(1, "Bread", 2, true, Period.ZERO, 1, "");

        assertEquals("Food (1, Bread, 2, true, 1)", data.toString());
    }
}
