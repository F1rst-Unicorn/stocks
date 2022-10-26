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

package de.njsm.stocks.client.database.error;

import de.njsm.stocks.client.business.StatusCodeException;
import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.database.*;
import org.junit.Test;

import java.time.Instant;
import java.util.List;

import static de.njsm.stocks.client.database.BitemporalOperations.currentDelete;
import static de.njsm.stocks.client.database.BitemporalOperations.sequencedDeleteOfEntireTime;
import static de.njsm.stocks.client.database.util.Util.test;
import static de.njsm.stocks.client.database.util.Util.testList;
import static java.util.Collections.singletonList;

public class FoodItemDeleteErrorRepositoryImplTest extends AbstractErrorRepositoryImplTest {

    ErrorDetails recordError(StatusCodeException e) {
        FoodDbEntity food = standardEntities.foodDbEntity();
        stocksDatabase.synchronisationDao().writeFood(singletonList(food));
        UnitDbEntity unit = standardEntities.unitDbEntity();
        stocksDatabase.synchronisationDao().writeUnits(singletonList(unit));
        ScaledUnitDbEntity scaledUnit = standardEntities.scaledUnitDbEntityBuilder()
                .unit(unit.id())
                .build();
        stocksDatabase.synchronisationDao().writeScaledUnits(singletonList(scaledUnit));
        FoodItemDbEntity foodItem = standardEntities.foodItemDbEntityBuilder()
                .ofType(food.id())
                .unit(scaledUnit.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(singletonList(foodItem));
        FoodItemForDeletion data = FoodItemForDeletion.create(foodItem.id(), foodItem.version());
        ErrorDetails errorDetails = FoodItemDeleteErrorDetails.create(foodItem.id(), food.name(),
                FoodItemDeleteErrorDetails.Unit.create(scaledUnit.scale(), unit.abbreviation()));
        errorRecorder.recordFoodItemDeleteError(e, data);
        return errorDetails;
    }

    @Override
    List<?> getErrorDetails() {
        return stocksDatabase.errorDao().getFoodItemDeletes();
    }

    @Test
    public void gettingErrorOfDeletedEntityWorks() {
        FoodDbEntity food = standardEntities.foodDbEntity();
        stocksDatabase.synchronisationDao().writeFood(singletonList(food));
        UnitDbEntity unit = standardEntities.unitDbEntity();
        stocksDatabase.synchronisationDao().writeUnits(singletonList(unit));
        ScaledUnitDbEntity scaledUnit = standardEntities.scaledUnitDbEntityBuilder()
                .unit(unit.id())
                .build();
        stocksDatabase.synchronisationDao().writeScaledUnits(singletonList(scaledUnit));

        Instant editTime = Instant.EPOCH.plusSeconds(5);
        StatusCode statusCode = StatusCode.DATABASE_UNREACHABLE;
        StatusCodeException exception = new StatusCodeException(statusCode);
        FoodItemDbEntity foodItem = standardEntities.foodItemDbEntityBuilder()
                .ofType(food.id())
                .unit(scaledUnit.id())
                .build();
        FoodItemForDeletion data = FoodItemForDeletion.create(foodItem.id(), foodItem.version());
        stocksDatabase.synchronisationDao().writeFoodItems(singletonList(foodItem));
        stocksDatabase.synchronisationDao().writeFoodItems(currentDelete(foodItem, editTime));
        errorRecorder.recordFoodItemDeleteError(exception, data);
        ErrorDetails errorDetails = FoodItemDeleteErrorDetails.create(foodItem.id(), food.name(),
                FoodItemDeleteErrorDetails.Unit.create(scaledUnit.scale(), unit.abbreviation()));

        test(uut.getNumberOfErrors()).assertValue(1);
        testList(uut.getErrors()).assertValue(v -> v.get(0).statusCode() == statusCode);
        testList(uut.getErrors()).assertValue(v -> v.get(0).errorDetails().equals(errorDetails));
        testList(uut.getErrors()).assertValue(v -> v.get(0).errorMessage().equals(exception.getMessage()));
    }

    @Test
    public void gettingErrorOfInvalidatedEntityWorks() {
        FoodDbEntity food = standardEntities.foodDbEntity();
        stocksDatabase.synchronisationDao().writeFood(singletonList(food));
        UnitDbEntity unit = standardEntities.unitDbEntity();
        stocksDatabase.synchronisationDao().writeUnits(singletonList(unit));
        ScaledUnitDbEntity scaledUnit = standardEntities.scaledUnitDbEntityBuilder()
                .unit(unit.id())
                .build();
        stocksDatabase.synchronisationDao().writeScaledUnits(singletonList(scaledUnit));

        Instant editTime = Instant.EPOCH.plusSeconds(5);
        StatusCode statusCode = StatusCode.DATABASE_UNREACHABLE;
        StatusCodeException exception = new StatusCodeException(statusCode);
        FoodItemDbEntity foodItem = standardEntities.foodItemDbEntityBuilder()
                .ofType(food.id())
                .unit(scaledUnit.id())
                .build();
        FoodItemForDeletion data = FoodItemForDeletion.create(foodItem.id(), foodItem.version());
        stocksDatabase.synchronisationDao().writeFoodItems(singletonList(foodItem));
        stocksDatabase.synchronisationDao().writeFoodItems(sequencedDeleteOfEntireTime(foodItem, editTime));
        stocksDatabase.synchronisationDao().insert(singletonList(UpdateDbEntity.create(EntityType.FOOD_ITEM, Instant.EPOCH)));
        errorRecorder.recordFoodItemDeleteError(exception, data);
        ErrorDetails errorDetails = FoodItemDeleteErrorDetails.create(foodItem.id(), food.name(),
                FoodItemDeleteErrorDetails.Unit.create(scaledUnit.scale(), unit.abbreviation()));

        test(uut.getNumberOfErrors()).assertValue(1);
        testList(uut.getErrors()).assertValue(v -> v.get(0).statusCode() == statusCode);
        testList(uut.getErrors()).assertValue(v -> v.get(0).errorDetails().equals(errorDetails));
        testList(uut.getErrors()).assertValue(v -> v.get(0).errorMessage().equals(exception.getMessage()));
    }
}
