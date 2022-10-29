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

import de.njsm.stocks.client.business.entities.FoodItemForListingData;
import io.reactivex.rxjava3.core.Observable;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.njsm.stocks.client.database.util.Util.test;
import static de.njsm.stocks.client.database.util.Util.testList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class FoodItemListRepositoryImplTest extends DbTestCase {

    private FoodItemListRepositoryImpl uut;

    private UnitDbEntity unit;
    private ScaledUnitDbEntity scaledUnit;
    private LocationDbEntity location;
    private UserDbEntity user;
    private UserDeviceDbEntity device;
    private FoodDbEntity food;

    @Before
    public void setup() {
        uut = new FoodItemListRepositoryImpl(stocksDatabase.foodDao(), stocksDatabase.foodItemDao());

        unit = standardEntities.unitDbEntity();
        stocksDatabase.synchronisationDao().writeUnits(singletonList(unit));
        scaledUnit = standardEntities.scaledUnitDbEntityBuilder()
                .unit(unit.id())
                .build();
        stocksDatabase.synchronisationDao().writeScaledUnits(singletonList(scaledUnit));
        location = standardEntities.locationDbEntity();
        stocksDatabase.synchronisationDao().writeLocations(singletonList(location));
        user = standardEntities.userDbEntity();
        stocksDatabase.synchronisationDao().writeUsers(singletonList(user));
        device = standardEntities.userDeviceDbEntityBuilder()
                .belongsTo(user.id())
                .build();
        stocksDatabase.synchronisationDao().writeUserDevices(singletonList(device));
        food = standardEntities.foodDbEntity();
        stocksDatabase.synchronisationDao().writeFood(singletonList(food));
    }

    @Test
    public void foodItemIsReturned() {
        FoodItemDbEntity item = standardEntities.foodItemDbEntityBuilder()
                .ofType(food.id())
                .storedIn(location.id())
                .registers(device.id())
                .unit(scaledUnit.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(singletonList(item));

        Observable<List<FoodItemForListingData>> actual = uut.get(food::id);

        testList(actual).assertValue(singletonList(FoodItemForListingData.create(
                item.id(),
                scaledUnit.scale(),
                unit.abbreviation(),
                location.name(),
                item.eatBy(),
                user.name(),
                device.name()
        )));
    }

    @Test
    public void foodItemOfDifferentFoodIsIgnored() {
        FoodItemDbEntity item = standardEntities.foodItemDbEntityBuilder()
                .storedIn(location.id())
                .registers(device.id())
                .unit(scaledUnit.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(singletonList(item));

        Observable<List<FoodItemForListingData>> actual = uut.get(food::id);

        test(actual).assertValue(emptyList());
    }

    @Test
    public void gettingVersionOfFoodItemWorks() {
        FoodItemDbEntity expected = standardEntities.foodItemDbEntityBuilder()
                .ofType(food.id())
                .storedIn(location.id())
                .registers(device.id())
                .unit(scaledUnit.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(singletonList(expected));

        var actual = uut.getEntityForDeletion(expected::id);

        assertThat(actual.version(), is(equalTo(expected.version())));
    }
}