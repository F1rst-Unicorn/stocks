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

import androidx.lifecycle.LiveData;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import de.njsm.stocks.android.db.dao.ScaledUnitDao;
import de.njsm.stocks.android.db.entities.ScaledUnit;
import de.njsm.stocks.android.util.Config;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.threeten.bp.Instant;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static java.math.BigDecimal.ONE;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ScaledUnitDaoTest extends DbTestCase {

    private ScaledUnitDao uut;

    @Before
    public void setup() {
        uut = stocksDatabase.scaledUnitDao();
    }

    @Test
    public void insertionWorks() {
        ScaledUnit data = new ScaledUnit(1, Instant.EPOCH, Config.DATABASE_INFINITY, Instant.EPOCH, Config.DATABASE_INFINITY, 0, 1, ONE, 1);

        uut.insert(new ScaledUnit[]{data});

        LiveData<List<ScaledUnit>> actual = uut.getAll();
        actual.observeForever(v -> assertEquals(Collections.singletonList(data), v));
    }

    @Test
    public void insertionWithConflictReplaces() {
        ScaledUnit dataToBeTerminated = new ScaledUnit(1, Instant.EPOCH, Config.DATABASE_INFINITY, Instant.EPOCH, Config.DATABASE_INFINITY, 0, 1, ONE, 1);
        uut.insert(new ScaledUnit[]{dataToBeTerminated});
        Instant now = Instant.now();
        dataToBeTerminated.transactionTimeEnd = now;
        ScaledUnit terminatedData = new ScaledUnit(1, Instant.EPOCH, now, Instant.EPOCH, Config.DATABASE_INFINITY, 0, 1, ONE, 1);

        uut.insert(new ScaledUnit[] {dataToBeTerminated, terminatedData});

        setArtificialDbNow(now);
        LiveData<List<ScaledUnit>> actual = uut.getAll();
        actual.observeForever(v -> assertEquals(Collections.emptyList(), v));
    }

    @Test
    public void synchronisingWorks() {
        ScaledUnit data = new ScaledUnit(1, Instant.EPOCH, Config.DATABASE_INFINITY, Instant.EPOCH, Config.DATABASE_INFINITY, 0, 1, ONE, 1);
        uut.insert(new ScaledUnit[]{data});
        data.scale = BigDecimal.TEN;

        uut.synchronise(new ScaledUnit[]{data});

        LiveData<List<ScaledUnit>> actual = uut.getAll();
        actual.observeForever(v -> assertEquals(Collections.singletonList(data), v));
    }
}
