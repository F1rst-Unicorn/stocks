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

import de.njsm.stocks.client.business.entities.FoodForItemCreation;
import de.njsm.stocks.client.business.entities.Identifiable;
import de.njsm.stocks.client.business.entities.Location;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.Period;
import java.util.List;

import static de.njsm.stocks.client.database.BitemporalOperations.currentDelete;
import static de.njsm.stocks.client.database.BitemporalOperations.currentUpdate;
import static de.njsm.stocks.client.database.util.Util.test;
import static java.time.Instant.EPOCH;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class FoodItemAddRepositoryImplTest extends DbTestCase {

    private FoodItemAddRepositoryImpl uut;

    @Before
    public void setup() {
        uut = new FoodItemAddRepositoryImpl(null, null, stocksDatabase.foodItemDao(), stocksDatabase.foodDao());
    }

    @Test
    public void foodCanBeRetrieved() {
        FoodDbEntity food = standardEntities.foodDbEntity();
        stocksDatabase.synchronisationDao().writeFood(singletonList(food));

        Observable<FoodForItemCreation> actual = uut.getFood(food::id);

        test(actual).assertValue(v ->
                v.id() == food.id() &&
                v.name().equals(food.name()) &&
                v.expirationOffset().equals(food.expirationOffset()) &&
                v.location().map(Identifiable::id).orElse(-1).equals(food.location()) &&
                v.unit().id() == food.storeUnit());
    }

    @Test
    public void foodWithoutLocationCanBeRetrieved() {
        FoodDbEntity food = standardEntities.foodDbEntityBuilder()
                .location(null)
                .build();
        stocksDatabase.synchronisationDao().writeFood(singletonList(food));

        Observable<FoodForItemCreation> actual = uut.getFood(food::id);

        test(actual).assertValue(v ->
                v.id() == food.id() &&
                v.name().equals(food.name()) &&
                v.expirationOffset().equals(food.expirationOffset()) &&
                !v.location().isPresent() &&
                v.unit().id() == food.storeUnit());
    }

    @Test
    public void missingFoodItemsAffectPresentPrediction() {
        FoodDbEntity food = standardEntities.foodDbEntity();
        stocksDatabase.synchronisationDao().writeFood(singletonList(food));

        Maybe<Instant> actual = uut.getMaxEatByOfPresentItemsOf(food::id);

        actual.test().assertComplete().assertNoValues();
    }

    @Test
    public void presentFoodItemsAffectPresentPrediction() {
        FoodDbEntity food = standardEntities.foodDbEntity();
        stocksDatabase.synchronisationDao().writeFood(singletonList(food));
        FoodItemDbEntity decidingFoodItem = standardEntities.foodItemDbEntityBuilder()
                .eatBy(EPOCH.plus(Period.ofDays(2)))
                .ofType(food.id())
                .build();
        FoodItemDbEntity earlierFoodItem = standardEntities.foodItemDbEntityBuilder()
                .eatBy(EPOCH.plus(Period.ofDays(1)))
                .ofType(food.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(asList(decidingFoodItem, earlierFoodItem));

        Maybe<Instant> actual = uut.getMaxEatByOfPresentItemsOf(food::id);

        test(actual).assertValue(decidingFoodItem.eatBy());
    }

    @Test
    public void itemOfDifferentFoodDoesntAffectPresentPrediction() {
        FoodDbEntity food = standardEntities.foodDbEntity();
        stocksDatabase.synchronisationDao().writeFood(singletonList(food));
        FoodItemDbEntity decidingFoodItem = standardEntities.foodItemDbEntityBuilder()
                .eatBy(EPOCH.plus(Period.ofDays(2)))
                .ofType(food.id())
                .build();
        FoodItemDbEntity laterItemOfDifferentFood = standardEntities.foodItemDbEntityBuilder()
                .eatBy(EPOCH.plus(Period.ofDays(10)))
                .ofType(randomnessProvider.getId("different food id"))
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(asList(decidingFoodItem, laterItemOfDifferentFood));

        Maybe<Instant> actual = uut.getMaxEatByOfPresentItemsOf(food::id);

        test(actual).assertValue(decidingFoodItem.eatBy());
    }

    @Test
    public void deletedLargerFoodItemDoesntAffectPresentPrediction() {
        FoodDbEntity food = standardEntities.foodDbEntity();
        Instant now = EPOCH.plus(Period.ofDays(2));
        setArtificialDbNow(now);
        stocksDatabase.synchronisationDao().writeFood(singletonList(food));
        FoodItemDbEntity decidingFoodItem = standardEntities.foodItemDbEntityBuilder()
                .eatBy(EPOCH.plus(Period.ofDays(2)))
                .ofType(food.id())
                .build();
        FoodItemDbEntity deletedFoodItem = standardEntities.foodItemDbEntityBuilder()
                .eatBy(EPOCH.plus(Period.ofDays(10)))
                .ofType(food.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(asList(decidingFoodItem, deletedFoodItem));
        stocksDatabase.synchronisationDao().writeFoodItems(currentDelete(deletedFoodItem, now.minus(Period.ofDays(1))));

        Maybe<Instant> actual = uut.getMaxEatByOfPresentItemsOf(food::id);

        test(actual).assertValue(decidingFoodItem.eatBy());
    }

    @Test
    public void missingFoodItemsAffectEverPrediction() {
        FoodDbEntity food = standardEntities.foodDbEntity();
        stocksDatabase.synchronisationDao().writeFood(singletonList(food));

        Maybe<Instant> actual = uut.getMaxEatByEverOf(food::id);

        actual.test().assertComplete().assertNoValues();
    }

    @Test
    public void deletedLargerFoodItemAffectsEverPrediction() {
        FoodDbEntity food = standardEntities.foodDbEntity();
        Instant now = EPOCH.plus(Period.ofDays(2));
        setArtificialDbNow(now);
        stocksDatabase.synchronisationDao().writeFood(singletonList(food));
        FoodItemDbEntity presentEarlierItem = standardEntities.foodItemDbEntityBuilder()
                .eatBy(EPOCH.plus(Period.ofDays(2)))
                .ofType(food.id())
                .build();
        FoodItemDbEntity deletedDecidingFoodItem = standardEntities.foodItemDbEntityBuilder()
                .eatBy(EPOCH.plus(Period.ofDays(10)))
                .ofType(food.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(asList(presentEarlierItem, deletedDecidingFoodItem));
        stocksDatabase.synchronisationDao().writeFoodItems(currentDelete(deletedDecidingFoodItem, now.minus(Period.ofDays(1))));

        Maybe<Instant> actual = uut.getMaxEatByEverOf(food::id);

        test(actual).assertValue(deletedDecidingFoodItem.eatBy());
    }

    @Test
    public void earlierVersionOfDeletedFoodItemDoesntAffectEverPrediction() {
        FoodDbEntity food = standardEntities.foodDbEntity();
        stocksDatabase.synchronisationDao().writeFood(singletonList(food));
        FoodItemDbEntity originalItem = standardEntities.foodItemDbEntityBuilder()
                .eatBy(EPOCH.plus(Period.ofDays(1)))
                .ofType(food.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(singletonList(originalItem));
        Instant firstUpdateTime = EPOCH.plus(Period.ofDays(2));
        List<FoodItemDbEntity> update = currentUpdate(originalItem, (BitemporalOperations.EntityEditor<FoodItemDbEntity, FoodItemDbEntity.Builder>) builder ->
                builder.eatBy(EPOCH.plus(Period.ofDays(100))), firstUpdateTime);
        stocksDatabase.synchronisationDao().writeFoodItems(update);
        Instant secondUpdateTime = EPOCH.plus(Period.ofDays(3));
        Instant expectedEatByPrediction = EPOCH.plus(Period.ofDays(10));
        update = currentUpdate(update.get(2), (BitemporalOperations.EntityEditor<FoodItemDbEntity, FoodItemDbEntity.Builder>) builder ->
                builder.eatBy(expectedEatByPrediction), secondUpdateTime);
        stocksDatabase.synchronisationDao().writeFoodItems(update);
        Instant deleteTime = EPOCH.plus(Period.ofDays(4));
        stocksDatabase.synchronisationDao().writeFoodItems(currentDelete(update.get(2), deleteTime));
        setArtificialDbNow(deleteTime);

        Maybe<Instant> actual = uut.getMaxEatByEverOf(food::id);

        test(actual).assertValue(expectedEatByPrediction);
    }

    @Test
    public void missingFoodItemsAffectLocationPrediction() {
        FoodDbEntity food = standardEntities.foodDbEntity();
        stocksDatabase.synchronisationDao().writeFood(singletonList(food));

        Maybe<Identifiable<Location>> actual = uut.getLocationWithMostItemsOfType(food::id);

        actual.test().assertComplete().assertNoValues();
    }

    @Test
    public void severalFoodItemsAffectLocationPrediction() {
        FoodDbEntity food = standardEntities.foodDbEntity();
        stocksDatabase.synchronisationDao().writeFood(singletonList(food));
        LocationDbEntity location = standardEntities.locationDbEntity();
        stocksDatabase.synchronisationDao().writeLocations(singletonList(location));
        FoodItemDbEntity item1 = standardEntities.foodItemDbEntityBuilder()
                .ofType(food.id())
                .storedIn(location.id())
                .build();
        FoodItemDbEntity item2 = standardEntities.foodItemDbEntityBuilder()
                .ofType(food.id())
                .storedIn(location.id())
                .build();
        FoodItemDbEntity itemInDifferentLocation = standardEntities.foodItemDbEntityBuilder()
                .ofType(food.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(asList(item1, item2, itemInDifferentLocation));

        Maybe<Identifiable<Location>> actual = uut.getLocationWithMostItemsOfType(food::id);

        test(actual).assertValue(v -> v.id() == location.id());
    }
}