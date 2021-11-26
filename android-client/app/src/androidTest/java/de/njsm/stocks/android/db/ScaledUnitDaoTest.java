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

import androidx.test.ext.junit.runners.AndroidJUnit4;
import de.njsm.stocks.android.db.dao.Inserter;
import de.njsm.stocks.android.db.dao.ScaledUnitDao;
import de.njsm.stocks.android.db.entities.ScaledUnit;
import de.njsm.stocks.android.util.Config;
import org.junit.Before;
import org.junit.runner.RunWith;
import java.time.Instant;

import java.math.BigDecimal;

import static java.math.BigDecimal.ONE;

@RunWith(AndroidJUnit4.class)
public class ScaledUnitDaoTest extends InsertionTest<ScaledUnit> {

    private ScaledUnitDao uut;

    @Before
    public void setup() {
        uut = stocksDatabase.scaledUnitDao();
    }

    @Override
    Inserter<ScaledUnit> getDao() {
        return uut;
    }

    @Override
    ScaledUnit getDto() {
        return new ScaledUnit(1, Instant.EPOCH, Config.API_INFINITY, Instant.EPOCH, Config.API_INFINITY, 0, 1, ONE, 1);
    }

    @Override
    void alterDto(ScaledUnit data) {
        data.scale = BigDecimal.TEN;
    }

}
