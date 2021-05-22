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

import java.lang.reflect.Array;
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
    public void gettingUsersWorks() {
        User[] data = new User[]{};
        Mockito.when(serverClient.getUsers(1, Config.API_DATE_FORMAT.format(Instant.EPOCH))).thenReturn(createMockCall(data));
        gettingEntityDataWorks("user", userDao, data);
    }

    @Test
    public void gettingUserDevicesWorks() {
        UserDevice[] data = new UserDevice[]{};
        Mockito.when(serverClient.getDevices(1, Config.API_DATE_FORMAT.format(Instant.EPOCH))).thenReturn(createMockCall(data));
        gettingEntityDataWorks("User_device", userDeviceDao, data);
    }

    @Test
    public void gettingLocationsWorks() {
        Location[] data = new Location[]{};
        Mockito.when(serverClient.getLocations(1, Config.API_DATE_FORMAT.format(Instant.EPOCH))).thenReturn(createMockCall(data));
        gettingEntityDataWorks("Location", locationDao, data);
    }

    @Test
    public void gettingFoodsWorks() {
        Food[] data = new Food[]{};
        Mockito.when(serverClient.getFood(1, Config.API_DATE_FORMAT.format(Instant.EPOCH))).thenReturn(createMockCall(data));
        gettingEntityDataWorks("Food", foodDao, data);
    }

    @Test
    public void gettingFoodItemsWorks() {
        FoodItem[] data = new FoodItem[]{};
        Mockito.when(serverClient.getFoodItems(1, Config.API_DATE_FORMAT.format(Instant.EPOCH))).thenReturn(createMockCall(data));
        gettingEntityDataWorks("Food_item", foodItemDao, data);
    }

    @Test
    public void gettingEanNumbersWorks() {
        EanNumber[] data = new EanNumber[]{};
        Mockito.when(serverClient.getEanNumbers(1, Config.API_DATE_FORMAT.format(Instant.EPOCH))).thenReturn(createMockCall(data));
        gettingEntityDataWorks("EAN_number", eanNumberDao, data);
    }

    @Test
    public void gettingUnitsWorks() {
        Unit[] data = new Unit[]{};
        Mockito.when(serverClient.getUnits(1, Config.API_DATE_FORMAT.format(Instant.EPOCH))).thenReturn(createMockCall(data));
        gettingEntityDataWorks("unit", unitDao, data);
    }

    @Test
    public void gettingScaledUnitsWorks() {
        ScaledUnit[] data = new ScaledUnit[]{};
        Mockito.when(serverClient.getScaledUnits(1, Config.API_DATE_FORMAT.format(Instant.EPOCH))).thenReturn(createMockCall(data));
        gettingEntityDataWorks("scaled_unit", scaledUnitDao, data);
    }

    @Test
    public void gettingRecipesWorks() {
        Recipe[] data = new Recipe[]{};
        Mockito.when(serverClient.getRecipes(1, Config.API_DATE_FORMAT.format(Instant.EPOCH))).thenReturn(createMockCall(data));
        gettingEntityDataWorks("recipe", recipeDao, data);
    }

    @Test
    public void gettingRecipeIngredientsWorks() {
        RecipeIngredient[] data = new RecipeIngredient[]{};
        Mockito.when(serverClient.getRecipeIngredients(1, Config.API_DATE_FORMAT.format(Instant.EPOCH))).thenReturn(createMockCall(data));
        gettingEntityDataWorks("recipe_ingredient", recipeIngredientDao, data);
    }

    @Test
    public void gettingRecipeProductWorks() {
        RecipeProduct[] data = new RecipeProduct[]{};
        Mockito.when(serverClient.getRecipeProducts(1, Config.API_DATE_FORMAT.format(Instant.EPOCH))).thenReturn(createMockCall(data));
        gettingEntityDataWorks("recipe_product", recipeProductDao, data);
    }

    @Test
    public void gettingAllSynchronisesUsers() {
        gettingAllSynchronises(userDao, User.class);
    }

    @Test
    public void gettingAllSynchronisesUserDevices() {
        gettingAllSynchronises(userDeviceDao, UserDevice.class);
    }

    @Test
    public void gettingAllSynchronisesLocations() {
        gettingAllSynchronises(locationDao, Location.class);
    }

    @Test
    public void gettingAllSynchronisesFoods() {
        gettingAllSynchronises(foodDao, Food.class);
    }

    @Test
    public void gettingAllSynchronisesFoodItems() {
        gettingAllSynchronises(foodItemDao, FoodItem.class);
    }

    @Test
    public void gettingAllSynchronisesEanNumbers() {
        gettingAllSynchronises(eanNumberDao, EanNumber.class);
    }

    @Test
    public void gettingAllSynchronisesUnits() {
        gettingAllSynchronises(unitDao, Unit.class);
    }

    @Test
    public void gettingAllSynchronisesScaledUnits() {
        gettingAllSynchronises(scaledUnitDao, ScaledUnit.class);
    }

    @Test
    public void gettingAllSynchronisesRecipes() {
        gettingAllSynchronises(recipeDao, Recipe.class);
    }

    @Test
    public void gettingAllSynchronisesRecipeIngredients() {
        gettingAllSynchronises(recipeIngredientDao, RecipeIngredient.class);
    }

    @Test
    public void gettingAllSynchronisesRecipeProducts() {
        gettingAllSynchronises(recipeProductDao, RecipeProduct.class);
    }

    public <T> void gettingEntityDataWorks(String entityName, Inserter<T> dao, T[] data) {
        Update[] localUpdates = new Update[] {
                new Update(1, entityName, Instant.EPOCH)
        };
        Update[] remoteUpdates = new Update[] {
                new Update(1, entityName, Instant.MAX)
        };
        Mockito.when(updateDao.getAll()).thenReturn(localUpdates);

        Mockito.when(serverClient.getUpdates()).thenReturn(createMockCall(remoteUpdates));
        MutableLiveData<StatusCode> result = new MutableLiveData<>();

        uut.synchroniseInThread(false, result);

        Mockito.verify(dao).insert(data);
    }

    public <T> void gettingAllSynchronises(Inserter<T> dao, Class<T> clazz) {
        Update[] emptyUpdates = new Update[] {};
        Update[] remoteUpdates = new Update[] {
                new Update(1, "", Instant.MAX)
        };

        Mockito.when(updateDao.getAll()).thenReturn(emptyUpdates);
        Mockito.when(serverClient.getUpdates()).thenReturn(createMockCall(remoteUpdates));
        mockAllEntityResponses();
        MutableLiveData<StatusCode> result = new MutableLiveData<>();

        uut.synchroniseInThread(false, result);

        Mockito.verify(dao).synchronise((T[]) Array.newInstance(clazz, 0));
    }

    private void mockAllEntityResponses() {
        Mockito.when(serverClient.getUsers(1, null)).thenReturn(createMockCall(new User[]{}));
        Mockito.when(serverClient.getDevices(1, null)).thenReturn(createMockCall(new UserDevice[]{}));
        Mockito.when(serverClient.getFood(1, null)).thenReturn(createMockCall(new Food[]{}));
        Mockito.when(serverClient.getFoodItems(1, null)).thenReturn(createMockCall(new FoodItem[]{}));
        Mockito.when(serverClient.getLocations(1, null)).thenReturn(createMockCall(new Location[]{}));
        Mockito.when(serverClient.getEanNumbers(1, null)).thenReturn(createMockCall(new EanNumber[]{}));
        Mockito.when(serverClient.getUnits(1, null)).thenReturn(createMockCall(new Unit[]{}));
        Mockito.when(serverClient.getScaledUnits(1, null)).thenReturn(createMockCall(new ScaledUnit[]{}));
        Mockito.when(serverClient.getRecipes(1, null)).thenReturn(createMockCall(new Recipe[]{}));
        Mockito.when(serverClient.getRecipeIngredients(1, null)).thenReturn(createMockCall(new RecipeIngredient[]{}));
        Mockito.when(serverClient.getRecipeProducts(1, null)).thenReturn(createMockCall(new RecipeProduct[]{}));
    }

    private <T> Call<ListResponse<T>> createMockCall(T[] input) {
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
