/*
 * stocks is client-server program to manage a household's food stock
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
 *
 */

package de.njsm.stocks.client.database;

import android.content.Context;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory;
import org.junit.After;
import org.junit.Before;

import java.time.Instant;

import static java.util.Collections.singletonList;

public class DbTestCase {

    StocksDatabase stocksDatabase;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        stocksDatabase = Room.inMemoryDatabaseBuilder(context, StocksDatabase.class)
                .openHelperFactory(new RequerySQLiteOpenHelperFactory())
                .build();
    }

    @After
    public void closeDb() {
        stocksDatabase.close();
    }


    /**
     * Sets the expression used as "NOW" in all DB queries to a specified value
     *
     * @see de.njsm.stocks.client.database.StocksDatabase.NOW
     */
    public void setArtificialDbNow(Instant now) {
        UpdateDbEntity update = new UpdateDbEntity(99999, "artificial_db_now", now);

        stocksDatabase.synchronisationDao().synchronise(singletonList(update));
    }
}
