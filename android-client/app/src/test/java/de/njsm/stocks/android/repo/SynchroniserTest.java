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
import com.google.common.collect.Lists;
import de.njsm.stocks.android.db.dao.*;
import de.njsm.stocks.android.db.entities.Update;
import de.njsm.stocks.android.network.server.ServerClient;
import de.njsm.stocks.android.util.Config;
import de.njsm.stocks.android.util.idling.IdlingResource;
import de.njsm.stocks.android.util.idling.NullIdlingResource;
import de.njsm.stocks.common.api.ListResponse;
import de.njsm.stocks.common.api.StatusCode;
import okhttp3.Request;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    private UnitDao unitDao;

    private ScaledUnitDao scaledUnitDao;

    private RecipeDao recipeDao;

    private RecipeIngredientDao recipeIngredientDao;

    private RecipeProductDao recipeProductDao;

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
        scaledUnitDao = Mockito.mock(ScaledUnitDao.class);
        recipeDao = Mockito.mock(RecipeDao.class);
        recipeIngredientDao = Mockito.mock(RecipeIngredientDao.class);
        recipeProductDao = Mockito.mock(RecipeProductDao.class);
        executor = Mockito.mock(Executor.class);
        IdlingResource idlingResource = new NullIdlingResource();

        uut = new Synchroniser(serverClient, userDao, userDeviceDao, locationDao, foodDao, foodItemDao, eanNumberDao, unitDao, scaledUnitDao, recipeDao, recipeIngredientDao, recipeProductDao, updateDao, executor, idlingResource);
    }

    @Test
    public void tablesInMixedOrderAreHandled() throws Exception {
        List<Update> serverUpdates = Lists.newArrayList(
            new Update(0, "User", Instant.EPOCH),
            new Update(0, "Food", Instant.EPOCH)
        );
        List<Update> localUpdates = Lists.newArrayList(
            serverUpdates.get(0)
        );

        uut.refreshOutdatedTables(serverUpdates, localUpdates);
    }

    @Test
    public void unknownTableIsIgnored() throws Exception {
        List<Update> serverUpdates = Lists.newArrayList(
                new Update(0, "Food", Instant.EPOCH),
                new Update(0, "User", Instant.EPOCH)
        );
        List<Update> localUpdates = Lists.newArrayList(
                serverUpdates.get(1)
        );

        uut.refreshOutdatedTables(serverUpdates, localUpdates);
    }

    @Test
    public void gettingUsersWorks() {
        Mockito.when(serverClient.getUsers(Config.API_DATE_FORMAT.format(Instant.EPOCH))).thenReturn(createMockCall(Lists.newArrayList()));
        gettingEntityDataWorks("user", userDao);
    }

    @Test
    public void gettingUserDevicesWorks() {
        Mockito.when(serverClient.getDevices(Config.API_DATE_FORMAT.format(Instant.EPOCH))).thenReturn(createMockCall(Lists.newArrayList()));
        gettingEntityDataWorks("User_device", userDeviceDao);
    }

    @Test
    public void gettingLocationsWorks() {
        Mockito.when(serverClient.getLocations(Config.API_DATE_FORMAT.format(Instant.EPOCH))).thenReturn(createMockCall(Lists.newArrayList()));
        gettingEntityDataWorks("Location", locationDao);
    }

    @Test
    public void gettingFoodsWorks() {
        Mockito.when(serverClient.getFood(Config.API_DATE_FORMAT.format(Instant.EPOCH))).thenReturn(createMockCall(Lists.newArrayList()));
        gettingEntityDataWorks("Food", foodDao);
    }

    @Test
    public void gettingFoodItemsWorks() {
        Mockito.when(serverClient.getFoodItems(Config.API_DATE_FORMAT.format(Instant.EPOCH))).thenReturn(createMockCall(Lists.newArrayList()));
        gettingEntityDataWorks("Food_item", foodItemDao);
    }

    @Test
    public void gettingEanNumbersWorks() {
        Mockito.when(serverClient.getEanNumbers(Config.API_DATE_FORMAT.format(Instant.EPOCH))).thenReturn(createMockCall(Lists.newArrayList()));
        gettingEntityDataWorks("EAN_number", eanNumberDao);
    }

    @Test
    public void gettingUnitsWorks() {
        Mockito.when(serverClient.getUnits(Config.API_DATE_FORMAT.format(Instant.EPOCH))).thenReturn(createMockCall(Lists.newArrayList()));
        gettingEntityDataWorks("unit", unitDao);
    }

    @Test
    public void gettingScaledUnitsWorks() {
        Mockito.when(serverClient.getScaledUnits(Config.API_DATE_FORMAT.format(Instant.EPOCH))).thenReturn(createMockCall(Lists.newArrayList()));
        gettingEntityDataWorks("scaled_unit", scaledUnitDao);
    }

    @Test
    public void gettingRecipesWorks() {
        Mockito.when(serverClient.getRecipes(Config.API_DATE_FORMAT.format(Instant.EPOCH))).thenReturn(createMockCall(Lists.newArrayList()));
        gettingEntityDataWorks("recipe", recipeDao);
    }

    @Test
    public void gettingRecipeIngredientsWorks() {
        Mockito.when(serverClient.getRecipeIngredients(Config.API_DATE_FORMAT.format(Instant.EPOCH))).thenReturn(createMockCall(Lists.newArrayList()));
        gettingEntityDataWorks("recipe_ingredient", recipeIngredientDao);
    }

    @Test
    public void gettingRecipeProductWorks() {
        Mockito.when(serverClient.getRecipeProducts(Config.API_DATE_FORMAT.format(Instant.EPOCH))).thenReturn(createMockCall(Lists.newArrayList()));
        gettingEntityDataWorks("recipe_product", recipeProductDao);
    }

    @Test
    public void gettingAllSynchronisesUsers() {
        gettingAllSynchronises(userDao);
    }

    @Test
    public void gettingAllSynchronisesUserDevices() {
        gettingAllSynchronises(userDeviceDao);
    }

    @Test
    public void gettingAllSynchronisesLocations() {
        gettingAllSynchronises(locationDao);
    }

    @Test
    public void gettingAllSynchronisesFoods() {
        gettingAllSynchronises(foodDao);
    }

    @Test
    public void gettingAllSynchronisesFoodItems() {
        gettingAllSynchronises(foodItemDao);
    }

    @Test
    public void gettingAllSynchronisesEanNumbers() {
        gettingAllSynchronises(eanNumberDao);
    }

    @Test
    public void gettingAllSynchronisesUnits() {
        gettingAllSynchronises(unitDao);
    }

    @Test
    public void gettingAllSynchronisesScaledUnits() {
        gettingAllSynchronises(scaledUnitDao);
    }

    @Test
    public void gettingAllSynchronisesRecipes() {
        gettingAllSynchronises(recipeDao);
    }

    @Test
    public void gettingAllSynchronisesRecipeIngredients() {
        gettingAllSynchronises(recipeIngredientDao);
    }

    @Test
    public void gettingAllSynchronisesRecipeProducts() {
        gettingAllSynchronises(recipeProductDao);
    }

    public <T> void gettingEntityDataWorks(String entityName, Inserter<T> dao) {
        Update[] localUpdates = new Update[] {
                new Update(1, entityName, Instant.EPOCH)
        };
        ArrayList<de.njsm.stocks.common.api.Update> remoteUpdates = Lists.newArrayList(
                de.njsm.stocks.common.api.Update.builder()
                        .table(entityName)
                        .lastUpdate(Instant.MAX)
                        .build()
        );
        Mockito.when(updateDao.getAll()).thenReturn(Arrays.asList(localUpdates));

        Mockito.when(serverClient.getUpdates()).thenReturn(createMockCall(remoteUpdates));
        MutableLiveData<StatusCode> result = new MutableLiveData<>();

        uut.synchroniseInThread(false, result);

        Mockito.verify(dao).insert(Lists.newArrayList());
    }

    public <T> void gettingAllSynchronises(Inserter<T> dao) {
        ArrayList<de.njsm.stocks.common.api.Update> remoteUpdates = Lists.newArrayList(
                de.njsm.stocks.common.api.Update.builder()
                        .table("")
                        .lastUpdate(Instant.MAX)
                        .build()
        );

        Mockito.when(updateDao.getAll()).thenReturn(Lists.newArrayList());
        Mockito.when(serverClient.getUpdates()).thenReturn(createMockCall(remoteUpdates));
        mockAllEntityResponses();
        MutableLiveData<StatusCode> result = new MutableLiveData<>();

        uut.synchroniseInThread(false, result);

        Mockito.verify(dao).synchronise(Lists.newArrayList());
    }

    private void mockAllEntityResponses() {
        Mockito.when(serverClient.getUsers(null)).thenReturn(createMockCall(Lists.newArrayList()));
        Mockito.when(serverClient.getDevices(null)).thenReturn(createMockCall(Lists.newArrayList()));
        Mockito.when(serverClient.getFood(null)).thenReturn(createMockCall(Lists.newArrayList()));
        Mockito.when(serverClient.getFoodItems(null)).thenReturn(createMockCall(Lists.newArrayList()));
        Mockito.when(serverClient.getLocations(null)).thenReturn(createMockCall(Lists.newArrayList()));
        Mockito.when(serverClient.getEanNumbers(null)).thenReturn(createMockCall(Lists.newArrayList()));
        Mockito.when(serverClient.getUnits(null)).thenReturn(createMockCall(Lists.newArrayList()));
        Mockito.when(serverClient.getScaledUnits(null)).thenReturn(createMockCall(Lists.newArrayList()));
        Mockito.when(serverClient.getRecipes(null)).thenReturn(createMockCall(Lists.newArrayList()));
        Mockito.when(serverClient.getRecipeIngredients(null)).thenReturn(createMockCall(Lists.newArrayList()));
        Mockito.when(serverClient.getRecipeProducts(null)).thenReturn(createMockCall(Lists.newArrayList()));
    }

    private <T> Call<ListResponse<T>> createMockCall(List<T> input) {
        return new Call<ListResponse<T>>() {
            @Override
            public Response<ListResponse<T>> execute() {
                return Response.success(new ListResponse<>(StatusCode.SUCCESS, input));
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
