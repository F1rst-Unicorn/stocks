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

import de.njsm.stocks.client.business.event.*;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static de.njsm.stocks.client.business.Constants.INFINITY;
import static de.njsm.stocks.client.database.BitemporalOperations.currentDelete;
import static de.njsm.stocks.client.database.BitemporalOperations.sequencedDeleteOfEntireTime;
import static de.njsm.stocks.client.database.util.Util.testList;

public class EventRepositoryEventLoadingTest extends DbTestCase {

    private EventRepository uut;

    private UserDbEntity initiatorOwner;

    private UserDeviceDbEntity initiator;

    @Before
    public void setup() {
        uut = new EventRepositoryImpl(stocksDatabase.eventDao());
        initiatorOwner = standardEntities.userDbEntity();
        stocksDatabase.synchronisationDao().writeUsers(List.of(initiatorOwner));
        initiator = standardEntities.userDeviceDbEntityBuilder()
                .belongsTo(initiatorOwner.id())
                .build();
        stocksDatabase.synchronisationDao().writeUserDevices(List.of(initiator));
    }

    @Test
    public void gettingLocationEventsWorks() {
        var location = standardEntities.locationDbEntityBuilder()
                .initiates(initiator.id())
                .build();
        stocksDatabase.synchronisationDao().writeLocations(List.of(location));
        Instant updateTime = Instant.EPOCH.plusSeconds(1);
        List<LocationDbEntity> updated = BitemporalOperations.<LocationDbEntity, LocationDbEntity.Builder>
                currentUpdate(location, b -> b.name("newName"), updateTime);
        stocksDatabase.synchronisationDao().writeLocations(updated);
        Instant deleteTime = Instant.EPOCH.plusSeconds(2);
        stocksDatabase.synchronisationDao().writeLocations(currentDelete(updated.get(2), deleteTime));

        var actual = uut.getLocationFeed(Instant.EPOCH);

        testList(actual).assertValue(List.of(
                        LocationEventFeedItem.create(location.id(), deleteTime, deleteTime, initiatorOwner.name(), updated.get(2).name(), location.description()),
                        LocationEventFeedItem.create(location.id(), updateTime, updateTime, initiatorOwner.name(), location.name(), location.description()),
                        LocationEventFeedItem.create(location.id(), INFINITY, updateTime, initiatorOwner.name(), updated.get(2).name(), location.description()),
                        LocationEventFeedItem.create(location.id(), INFINITY, Instant.EPOCH, initiatorOwner.name(), location.name(), location.description()))
                );
    }

    @Test
    public void gettingLocationEventWithDeletedInitiatorWorks() {
        var location = standardEntities.locationDbEntityBuilder()
                .initiates(initiator.id())
                .build();
        stocksDatabase.synchronisationDao().writeLocations(List.of(location));
        stocksDatabase.synchronisationDao().writeUserDevices(currentDelete(initiator, Instant.EPOCH.plusSeconds(3)));

        var actual = uut.getLocationFeed(Instant.EPOCH);

        testList(actual).assertValue(List.of(
                        LocationEventFeedItem.create(location.id(), INFINITY, Instant.EPOCH, initiatorOwner.name(), location.name(), location.description()))
                );
    }

    @Test
    public void gettingLocationEventWithSequencedDeletedInitiatorWorks() {
        var location = standardEntities.locationDbEntityBuilder()
                .initiates(initiator.id())
                .build();
        stocksDatabase.synchronisationDao().writeLocations(List.of(location));
        stocksDatabase.synchronisationDao().writeUserDevices(sequencedDeleteOfEntireTime(initiator, Instant.EPOCH.plusSeconds(3)));

        var actual = uut.getLocationFeed(Instant.EPOCH);

        testList(actual).assertValue(List.of(
                        LocationEventFeedItem.create(location.id(), INFINITY, Instant.EPOCH, initiatorOwner.name(), location.name(), location.description()))
                );
    }

    @Test
    public void gettingUnitEventsWorks() {
        var unit = standardEntities.unitDbEntityBuilder()
                .initiates(initiator.id())
                .build();
        stocksDatabase.synchronisationDao().writeUnits(List.of(unit));
        Instant updateTime = Instant.EPOCH.plusSeconds(1);
        List<UnitDbEntity> updated = BitemporalOperations.<UnitDbEntity, UnitDbEntity.Builder>
                currentUpdate(unit, b -> b.name("newName"), updateTime);
        stocksDatabase.synchronisationDao().writeUnits(updated);
        Instant deleteTime = Instant.EPOCH.plusSeconds(2);
        stocksDatabase.synchronisationDao().writeUnits(currentDelete(updated.get(2), deleteTime));

        var actual = uut.getUnitFeed(Instant.EPOCH);

        testList(actual).assertValue(List.of(
                        UnitEventFeedItem.create(unit.id(), deleteTime, deleteTime, initiatorOwner.name(), updated.get(2).name(), unit.abbreviation()),
                        UnitEventFeedItem.create(unit.id(), updateTime, updateTime, initiatorOwner.name(), unit.name(), unit.abbreviation()),
                        UnitEventFeedItem.create(unit.id(), INFINITY, updateTime, initiatorOwner.name(), updated.get(2).name(), unit.abbreviation()),
                        UnitEventFeedItem.create(unit.id(), INFINITY, Instant.EPOCH, initiatorOwner.name(), unit.name(), unit.abbreviation()))
                );
    }

    @Test
    public void gettingUserEventsWorks() {
        var user = standardEntities.userDbEntityBuilder()
                .initiates(initiator.id())
                .build();
        stocksDatabase.synchronisationDao().writeUsers(List.of(user));
        Instant deleteTime = Instant.EPOCH.plusSeconds(2);
        stocksDatabase.synchronisationDao().writeUsers(currentDelete(user, deleteTime));

        var actual = uut.getUserFeed(Instant.EPOCH);

        testList(actual).assertValue(List.of(
                        UserEventFeedItem.create(user.id(), deleteTime, deleteTime, initiatorOwner.name(), user.name()),
                        UserEventFeedItem.create(user.id(), user.validTimeEnd(), user.transactionTimeStart(), initiatorOwner.name(), user.name()))
                );
    }

    @Test
    public void gettingDeviceEventsWorks() {
        Instant inputDay = Instant.EPOCH.plus(1, ChronoUnit.DAYS);
        var newDeviceOwner = standardEntities.userDbEntityBuilder()
                .name("newDeviceOwner")
                .build();
        stocksDatabase.synchronisationDao().writeUsers(List.of(newDeviceOwner));
        var newDevice = standardEntities.userDeviceDbEntityBuilder()
                .belongsTo(newDeviceOwner.id())
                .initiates(initiator.id())
                .transactionTimeStart(inputDay)
                .build();
        stocksDatabase.synchronisationDao().writeUserDevices(List.of(newDevice));
        Instant deleteTime = inputDay.plusSeconds(2);
        stocksDatabase.synchronisationDao().writeUserDevices(currentDelete(newDevice, deleteTime));

        var actual = uut.getUserDeviceFeed(inputDay);

        testList(actual).assertValue(List.of(
                UserDeviceEventFeedItem.create(newDevice.id(),
                        deleteTime,
                        deleteTime,
                        initiatorOwner.name(),
                        newDevice.name(),
                        newDeviceOwner.name(),
                        newDeviceOwner.id()),
                UserDeviceEventFeedItem.create(newDevice.id(),
                        newDevice.validTimeEnd(),
                        newDevice.transactionTimeStart(),
                        initiatorOwner.name(),
                        newDevice.name(),
                        newDeviceOwner.name(),
                        newDeviceOwner.id())
        ));
    }

    @Test
    public void gettingScaledUnitEventsWorks() {
        var unit = standardEntities.unitDbEntity();
        stocksDatabase.synchronisationDao().writeUnits(List.of(unit));
        var scaledUnit = standardEntities.scaledUnitDbEntityBuilder()
                .initiates(initiator.id())
                .unit(unit.id())
                .build();
        stocksDatabase.synchronisationDao().writeScaledUnits(List.of(scaledUnit));
        Instant updateTime = Instant.EPOCH.plusSeconds(1);
        var updatedScale = scaledUnit.scale().add(BigDecimal.ONE);
        List<ScaledUnitDbEntity> updated = BitemporalOperations.<ScaledUnitDbEntity, ScaledUnitDbEntity.Builder>
                currentUpdate(scaledUnit, b -> b.scale(updatedScale), updateTime);
        stocksDatabase.synchronisationDao().writeScaledUnits(updated);
        Instant deleteTime = Instant.EPOCH.plusSeconds(2);
        stocksDatabase.synchronisationDao().writeScaledUnits(currentDelete(updated.get(2), deleteTime));

        var actual = uut.getScaledUnitFeed(Instant.EPOCH);

        testList(actual).assertValue(List.of(
                ScaledUnitEventFeedItem.create(scaledUnit.id(), deleteTime, deleteTime, initiatorOwner.name(), updatedScale, unit.name(), unit.abbreviation()),
                ScaledUnitEventFeedItem.create(scaledUnit.id(), updateTime, updateTime, initiatorOwner.name(), scaledUnit.scale(), unit.name(), unit.abbreviation()),
                ScaledUnitEventFeedItem.create(scaledUnit.id(), INFINITY, updateTime, initiatorOwner.name(), updatedScale, unit.name(), unit.abbreviation()),
                ScaledUnitEventFeedItem.create(scaledUnit.id(), INFINITY, Instant.EPOCH, initiatorOwner.name(), scaledUnit.scale(), unit.name(), unit.abbreviation()))
        );
    }

    @Test
    public void gettingFoodEventsWorks() {
        var unit = standardEntities.unitDbEntity();
        stocksDatabase.synchronisationDao().writeUnits(List.of(unit));
        var scaledUnit = standardEntities.scaledUnitDbEntityBuilder()
                .unit(unit.id())
                .build();
        stocksDatabase.synchronisationDao().writeScaledUnits(List.of(scaledUnit));
        var location = standardEntities.locationDbEntity();
        stocksDatabase.synchronisationDao().writeLocations(List.of(location));
        var food = standardEntities.foodDbEntityBuilder()
                .initiates(initiator.id())
                .storeUnit(scaledUnit.id())
                .location(null)
                .build();
        stocksDatabase.synchronisationDao().writeFood(List.of(food));

        Instant updateTime = Instant.EPOCH.plusSeconds(1);
        List<FoodDbEntity> updated = BitemporalOperations.<FoodDbEntity, FoodDbEntity.Builder>
                currentUpdate(food, b -> b.name("newName").location(location.id()), updateTime);
        FoodDbEntity updatedFood = updated.get(2);
        stocksDatabase.synchronisationDao().writeFood(updated);
        Instant deleteTime = Instant.EPOCH.plusSeconds(2);
        stocksDatabase.synchronisationDao().writeFood(currentDelete(updatedFood, deleteTime));

        var actual = uut.getFoodFeed(Instant.EPOCH);

        testList(actual).assertValue(List.of(
                FoodEventFeedItem.create(food.id(), deleteTime, deleteTime, initiatorOwner.name(), updatedFood.name(), food.toBuy(), food.expirationOffset(), scaledUnit.scale(), unit.abbreviation(), location.name(), food.description()),
                FoodEventFeedItem.create(food.id(), updateTime, updateTime, initiatorOwner.name(), food.name(), food.toBuy(), food.expirationOffset(), scaledUnit.scale(), unit.abbreviation(), null, food.description()),
                FoodEventFeedItem.create(food.id(), INFINITY, updateTime, initiatorOwner.name(), updatedFood.name(), food.toBuy(), food.expirationOffset(), scaledUnit.scale(), unit.abbreviation(), location.name(), food.description()),
                FoodEventFeedItem.create(food.id(), INFINITY, Instant.EPOCH, initiatorOwner.name(), food.name(), food.toBuy(), food.expirationOffset(), scaledUnit.scale(), unit.abbreviation(), null, food.description()))
        );
    }

    @Test
    public void gettingFoodItemEventsWorks() {
        var unit = standardEntities.unitDbEntity();
        stocksDatabase.synchronisationDao().writeUnits(List.of(unit));
        var scaledUnit = standardEntities.scaledUnitDbEntityBuilder()
                .unit(unit.id())
                .build();
        stocksDatabase.synchronisationDao().writeScaledUnits(List.of(scaledUnit));
        var location = standardEntities.locationDbEntity();
        stocksDatabase.synchronisationDao().writeLocations(List.of(location));
        var food = standardEntities.foodDbEntity();
        stocksDatabase.synchronisationDao().writeFood(List.of(food));
        var foodItem = standardEntities.foodItemDbEntityBuilder()
                .initiates(initiator.id())
                .storedIn(location.id())
                .ofType(food.id())
                .unit(scaledUnit.id())
                .buys(initiatorOwner.id())
                .registers(initiator.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(List.of(foodItem));

        Instant updateTime = Instant.EPOCH.plusSeconds(1);
        List<FoodItemDbEntity> updated = BitemporalOperations.<FoodItemDbEntity, FoodItemDbEntity.Builder>
                currentUpdate(foodItem, b -> b.eatBy(foodItem.eatBy().plus(1, ChronoUnit.DAYS)), updateTime);
        FoodItemDbEntity updatedFoodItem = updated.get(2);
        stocksDatabase.synchronisationDao().writeFoodItems(updated);
        Instant deleteTime = Instant.EPOCH.plusSeconds(2);
        stocksDatabase.synchronisationDao().writeFoodItems(currentDelete(updatedFoodItem, deleteTime));

        var actual = uut.getFoodItemFeed(Instant.EPOCH);

        testList(actual).assertValue(List.of(
                FoodItemEventFeedItem.create(foodItem.id(), deleteTime, deleteTime, initiatorOwner.name(), food.name(), updatedFoodItem.eatBy(), scaledUnit.scale(), unit.abbreviation(), location.name(), initiatorOwner.name(), initiator.name(), food.id()),
                FoodItemEventFeedItem.create(foodItem.id(), updateTime, updateTime, initiatorOwner.name(), food.name(), foodItem.eatBy(), scaledUnit.scale(), unit.abbreviation(), location.name(), initiatorOwner.name(), initiator.name(), food.id()),
                FoodItemEventFeedItem.create(foodItem.id(), INFINITY, updateTime, initiatorOwner.name(), food.name(), updatedFoodItem.eatBy(), scaledUnit.scale(), unit.abbreviation(), location.name(), initiatorOwner.name(), initiator.name(), food.id()),
                FoodItemEventFeedItem.create(foodItem.id(), INFINITY, Instant.EPOCH, initiatorOwner.name(), food.name(), foodItem.eatBy(), scaledUnit.scale(), unit.abbreviation(), location.name(), initiatorOwner.name(), initiator.name(), food.id())
        ));
    }

    @Test
    public void gettingEanNumberEventsWorks() {
        var food = standardEntities.foodDbEntity();
        stocksDatabase.synchronisationDao().writeFood(List.of(food));
        var eanNumber = standardEntities.eanNumberDbEntityBuilder()
                .initiates(initiator.id())
                .identifies(food.id())
                .build();
        stocksDatabase.synchronisationDao().writeEanNumbers(List.of(eanNumber));

        Instant deleteTime = Instant.EPOCH.plusSeconds(2);
        stocksDatabase.synchronisationDao().writeEanNumbers(currentDelete(eanNumber, deleteTime));

        var actual = uut.getEanNumberFeed(Instant.EPOCH);

        testList(actual).assertValue(List.of(
                EanNumberEventFeedItem.create(eanNumber.id(), deleteTime, deleteTime, initiatorOwner.name(), food.name(), eanNumber.number(), food.id()),
                EanNumberEventFeedItem.create(eanNumber.id(), INFINITY, Instant.EPOCH, initiatorOwner.name(), food.name(), eanNumber.number(), food.id())
        ));
    }
}
