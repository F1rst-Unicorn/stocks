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

package de.njsm.stocks.android.repo;

import de.njsm.stocks.android.db.dao.*;
import de.njsm.stocks.android.db.entities.Update;
import de.njsm.stocks.android.network.server.ServerClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.threeten.bp.Instant;

import java.util.concurrent.Executor;

public class SynchroniserTest {

    private Synchroniser uut;

    private ServerClient serverClient;

    private UserDao userDao;

    private UserDeviceDao userDeviceDao;

    private LocationDao locationDao;

    private FoodDao foodDao;

    private FoodItemDao foodItemDao;

    private EanNumberDao eanNumberDao;

    private UpdateDao updateDao;

    private Executor executor;

    @Before
    public void setup() {
        serverClient = Mockito.mock(ServerClient.class);
        userDao = Mockito.mock(UserDao.class);
        userDeviceDao = Mockito.mock(UserDeviceDao.class);
        updateDao = Mockito.mock(UpdateDao.class);
        locationDao = Mockito.mock(LocationDao.class);
        foodDao = Mockito.mock(FoodDao.class);
        foodItemDao = Mockito.mock(FoodItemDao.class);
        eanNumberDao = Mockito.mock(EanNumberDao.class);
        executor = Mockito.mock(Executor.class);

        uut = new Synchroniser(serverClient, userDao, userDeviceDao, locationDao, foodDao, foodItemDao, eanNumberDao, updateDao, executor);
    }

    @Test
    public void unknownTableIsIgnoredMixedOrder() throws Exception {
        Update[] serverUpdates = new Update[] {
            new Update(0, "User", Instant.EPOCH),
            new Update(0, "Food", Instant.EPOCH),
        };
        Update[] localUpdates = new Update[] {
            new Update(0, "Food", Instant.EPOCH),
            serverUpdates[1],
        };

        uut.refreshOutdatedTables(serverUpdates, localUpdates);
    }

    @Test
    public void unknownTableIsIgnored() throws Exception {
        Update[] serverUpdates = new Update[] {
                new Update(0, "Food", Instant.EPOCH),
                new Update(0, "User", Instant.EPOCH),
        };
        Update[] localUpdates = new Update[] {
                new Update(0, "Food", Instant.EPOCH),
                serverUpdates[1],
        };

        uut.refreshOutdatedTables(serverUpdates, localUpdates);
    }
}