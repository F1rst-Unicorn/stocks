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

import de.njsm.stocks.client.business.event.EventRepository;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EventRepositoryOldestEventTest extends DbTestCase {

    private EventRepository uut;

    @Before
    public void setup() {
        uut = new EventRepositoryImpl(stocksDatabase.eventDao());
    }

    @Test
    public void gettingOldestEventWorks() {
        Instant expected = Instant.EPOCH.plusSeconds(2);
        var unit = standardEntities.unitDbEntityBuilder()
                .transactionTimeStart(expected)
                .build();
        stocksDatabase.synchronisationDao().writeUnits(List.of(unit));
        var food = standardEntities.foodDbEntityBuilder()
                .transactionTimeStart(Instant.EPOCH.plusSeconds(3))
                .build();
        stocksDatabase.synchronisationDao().writeFood(List.of(food));

        var actual = uut.getOldestEventTime();

        actual.test().awaitDone(3, TimeUnit.SECONDS).assertValue(expected);
    }
}
