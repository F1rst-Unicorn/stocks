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

package de.njsm.stocks.android.db;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.time.Instant;

import java.util.Arrays;
import java.util.Collection;

import static de.njsm.stocks.android.util.Config.API_INFINITY;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class TypeConvertersInstantTest {

    @Parameterized.Parameters
    public static Collection<Object[]> input() {
        return Arrays.asList(new Object[][] {
                { Instant.now() },
                { Instant.EPOCH },
                { API_INFINITY },
        });
    }

    private final Instant input;

    private final TypeConverters uut;

    public TypeConvertersInstantTest(Instant input) {
        this.input = input;
        this.uut = new TypeConverters();
    }

    @Test
    public void converterPreservesIdentity() {
        assertEquals(input, uut.dbToInstant(uut.instantToDb(input)));
    }
}
