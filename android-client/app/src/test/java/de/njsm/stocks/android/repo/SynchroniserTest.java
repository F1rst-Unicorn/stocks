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

import androidx.lifecycle.MutableLiveData;
import de.njsm.stocks.android.db.dao.*;
import de.njsm.stocks.android.db.entities.*;
import de.njsm.stocks.android.network.server.ServerClient;
import de.njsm.stocks.android.network.server.StatusCode;
import de.njsm.stocks.android.network.server.data.ListResponse;
import de.njsm.stocks.android.util.Config;
import de.njsm.stocks.android.util.idling.IdlingResource;
import de.njsm.stocks.android.util.idling.NullIdlingResource;
import okhttp3.Request;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.threeten.bp.Instant;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.concurrent.Executor;

import static org.mockito.ArgumentMatchers.eq;

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

    private UnitDao unitDao;

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
        unitDao = Mockito.mock(UnitDao.class);
        executor = Mockito.mock(Executor.class);
        IdlingResource idlingResource = new NullIdlingResource();

        uut = new Synchroniser(serverClient, userDao, userDeviceDao, locationDao, foodDao, foodItemDao, eanNumberDao, unitDao, updateDao, executor, idlingResource);
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

    @Test
    public void gettingUnitsWorks() {
        Update[] localUpdates = new Update[] {
                new Update(1, "unit", Instant.EPOCH)
        };
        Update[] remoteUpdates = new Update[] {
                new Update(1, "unit", Instant.MAX)
        };
        Mockito.when(updateDao.getAll()).thenReturn(localUpdates);

        Mockito.when(serverClient.getUpdates()).thenReturn(createMockCall(remoteUpdates));
        Unit[] data = new Unit[]{};
        Mockito.when(serverClient.getUnits(1, Config.API_DATE_FORMAT.format(localUpdates[0].lastUpdate)))
                .thenReturn(createMockCall(data));
        MutableLiveData<StatusCode> result = new MutableLiveData<>();

        uut.synchroniseInThread(false, result);

        Mockito.verify(unitDao).insert(data);
    }


    @Test
    public void gettingAllSynchronisesUnits() {
        Update[] localUpdates = new Update[] {
        };
        Update[] remoteUpdates = new Update[] {
                new Update(1, "unit", Instant.MAX)
        };
        Mockito.when(updateDao.getAll()).thenReturn(localUpdates);

        Mockito.when(serverClient.getUpdates()).thenReturn(createMockCall(remoteUpdates));
        mockAllEntityResponses();
        MutableLiveData<StatusCode> result = new MutableLiveData<>();

        uut.synchroniseInThread(false, result);

        Mockito.verify(unitDao).synchronise(eq(new Unit[]{}));
    }

    private void mockAllEntityResponses() {
        Mockito.when(serverClient.getUsers(1, null)).thenReturn(createMockCall(new User[]{}));
        Mockito.when(serverClient.getDevices(1, null)).thenReturn(createMockCall(new UserDevice[]{}));
        Mockito.when(serverClient.getFood(1, null)).thenReturn(createMockCall(new Food[]{}));
        Mockito.when(serverClient.getFoodItems(1, null)).thenReturn(createMockCall(new FoodItem[]{}));
        Mockito.when(serverClient.getLocations(1, null)).thenReturn(createMockCall(new Location[]{}));
        Mockito.when(serverClient.getEanNumbers(1, null)).thenReturn(createMockCall(new EanNumber[]{}));
        Mockito.when(serverClient.getUnits(1, null)).thenReturn(createMockCall(new Unit[]{}));
    }

    private <T> Call<ListResponse<T>> createMockCall(T[] input) {
        return new Call<ListResponse<T>>() {
            @Override
            public Response<ListResponse<T>> execute() {
                return Response.success(new ListResponse<T>(StatusCode.SUCCESS, input));
            }

            @Override
            public void enqueue(Callback<ListResponse<T>> callback) {

            }

            @Override
            public boolean isExecuted() {
                return true;
            }

            @Override
            public void cancel() {

            }

            @Override
            public boolean isCanceled() {
                return false;
            }

            @Override
            public Call<ListResponse<T>> clone() {
                return null;
            }

            @Override
            public Request request() {
                return null;
            }
        };
    }
}
