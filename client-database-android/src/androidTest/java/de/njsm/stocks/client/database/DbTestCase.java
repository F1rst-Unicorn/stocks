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
import de.njsm.stocks.client.business.entities.EntityType;
import de.njsm.stocks.client.database.util.RandomnessProvider;
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import java.time.Instant;

import static java.util.Collections.singletonList;

public class DbTestCase implements Clock {

    protected StocksDatabase stocksDatabase;

    private Instant now;

    @Rule
    public RandomnessProvider randomnessProvider = new RandomnessProvider();

    protected StandardEntities standardEntities = new StandardEntities(randomnessProvider);

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        stocksDatabase = Room.inMemoryDatabaseBuilder(context, StocksDatabase.class)
                .openHelperFactory(new RequerySQLiteOpenHelperFactory())
                .build();
        setNow(Instant.EPOCH);
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
        UpdateDbEntity update = UpdateDbEntity.create(EntityType.LOCATION, now);

        stocksDatabase.synchronisationDao().writeUpdates(singletonList(update));
    }

    @Override
    public Instant get() {
        return getNow();
    }

    public Instant getNow() {
        return now;
    }

    public void setNow(Instant now) {
        this.now = now;
    }
}
