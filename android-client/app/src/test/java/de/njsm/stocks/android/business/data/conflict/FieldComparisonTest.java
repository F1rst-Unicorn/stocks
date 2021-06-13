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

package de.njsm.stocks.android.business.data.conflict;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FieldComparisonTest {

    @Test
    public void testComparisonValue() {
        assertEquals(FieldComparison.EQUAL, FieldComparison.compare(1, 1, 1));
        assertEquals(FieldComparison.REMOTE_DIFFERS, FieldComparison.compare(1, 2, 1));
        assertEquals(FieldComparison.LOCAL_DIFFERS, FieldComparison.compare(1, 1, 2));
        assertEquals(FieldComparison.BOTH_DIFFER, FieldComparison.compare(1, 2, 3));
        assertEquals(FieldComparison.BOTH_DIFFER_EQUALLY, FieldComparison.compare(1, 2, 2));
    }

    @Test
    public void testGettingValue() {
        assertEquals(Integer.valueOf(1), FieldComparison.getValue(1, 1, 1));
        assertEquals(Integer.valueOf(2), FieldComparison.getValue(1, 2, 1));
        assertEquals(Integer.valueOf(2), FieldComparison.getValue(1, 1, 2));
        assertEquals(Integer.valueOf(3), FieldComparison.getValue(1, 2, 3));
        assertEquals(Integer.valueOf(2), FieldComparison.getValue(1, 2, 2));
    }
}
