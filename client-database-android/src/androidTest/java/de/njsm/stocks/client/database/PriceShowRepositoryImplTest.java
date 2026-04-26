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

import de.njsm.stocks.client.business.PriceShowRepository;
import de.njsm.stocks.client.business.entities.PriceForTableListingData;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static de.njsm.stocks.client.database.BitemporalOperations.currentDelete;
import static de.njsm.stocks.client.database.util.Util.testList;
import static java.util.Collections.singletonList;

public class PriceShowRepositoryImplTest extends DbTestCase {

    private PriceShowRepository uut;
    private UnitDbEntity unit;
    private ScaledUnitDbEntity scaledUnit;
    private FoodDbEntity food;
    private GroceryStoreDbEntity groceryStore;
    private GroceryChainDbEntity groceryChain;

    @Before
    public void setUp() {
        uut = new PriceShowRepositoryImpl(stocksDatabase.priceDao());

        unit = standardEntities.unitDbEntity();
        stocksDatabase.synchronisationDao().writeUnits(List.of(unit));
        scaledUnit = standardEntities.scaledUnitDbEntityBuilder()
                .unit(unit.id())
                .build();
        stocksDatabase.synchronisationDao().writeScaledUnits(List.of(scaledUnit));
        food = standardEntities.foodDbEntity();
        stocksDatabase.synchronisationDao().writeFood(List.of(food));
        groceryChain = standardEntities.groceryChainDbEntity();
        stocksDatabase.synchronisationDao().writeGroceryChains(List.of(groceryChain));
        groceryStore = standardEntities.groceryStoreDbEntityBuilder()
                .groceryChain(groceryChain.id())
                .build();
        stocksDatabase.synchronisationDao().writeGroceryStores(List.of(groceryStore));
    }

    @Test
    public void currentPriceCanBeLoaded() {
        PriceDbEntity price = standardEntities.priceDbEntityBuilder()
                .food(food.id())
                .groceryStore(groceryStore.id())
                .scaledUnit(scaledUnit.id())
                .build();
        stocksDatabase.synchronisationDao().writePrices(List.of(price));

        var actual = uut.getPricesForTable(food::id);

        testList(actual).assertValue(singletonList(PriceForTableListingData.create(
                Instant.EPOCH,
                groceryStore.name(),
                groceryChain.name(),
                price.price(),
                price.scale(),
                scaledUnit.scale(),
                unit.abbreviation()
        )));
    }

    @Test
    public void fromTwoPricesTheLatterIsLoaded() {
        PriceDbEntity former = standardEntities.priceDbEntityBuilder()
                .food(food.id())
                .groceryStore(groceryStore.id())
                .scaledUnit(scaledUnit.id())
                .build();
        PriceDbEntity latter = standardEntities.priceDbEntityBuilder()
                .validTimeStart(Instant.EPOCH.plusSeconds(2))
                .validTimeEnd(Instant.EPOCH.plusSeconds(3))
                .price(former.price().add(BigDecimal.ONE))
                .food(food.id())
                .groceryStore(groceryStore.id())
                .scaledUnit(scaledUnit.id())
                .build();
        stocksDatabase.synchronisationDao().writePrices(List.of(former, latter));

        var actual = uut.getPricesForTable(food::id);

        testList(actual).assertValue(singletonList(PriceForTableListingData.create(
                latter.validTimeStart(),
                groceryStore.name(),
                groceryChain.name(),
                latter.price(),
                latter.scale(),
                scaledUnit.scale(),
                unit.abbreviation()
        )));
    }

    @Test
    public void priceOfCurrentlyDeletedStoreIsLoaded() {
        PriceDbEntity price = standardEntities.priceDbEntityBuilder()
                .food(food.id())
                .groceryStore(groceryStore.id())
                .scaledUnit(scaledUnit.id())
                .build();
        stocksDatabase.synchronisationDao().writePrices(List.of(price));
        Instant deleteTime = Instant.EPOCH.plusSeconds(2);
        stocksDatabase.synchronisationDao().writeGroceryStores(currentDelete(groceryStore, deleteTime));
        setArtificialDbNow(deleteTime.plusSeconds(1));

        var actual = uut.getPricesForTable(food::id);

        testList(actual).assertValue(singletonList(PriceForTableListingData.create(
                Instant.EPOCH,
                groceryStore.name(),
                groceryChain.name(),
                price.price(),
                price.scale(),
                scaledUnit.scale(),
                unit.abbreviation()
        )));
    }

    @Test
    public void priceOfUpdatedStoreShowsLatestStoreName() {
        PriceDbEntity price = standardEntities.priceDbEntityBuilder()
                .food(food.id())
                .groceryStore(groceryStore.id())
                .scaledUnit(scaledUnit.id())
                .build();
        stocksDatabase.synchronisationDao().writePrices(List.of(price));
        Instant updateTime = Instant.EPOCH.plusSeconds(2);
        List<GroceryStoreDbEntity> updated = BitemporalOperations.<GroceryStoreDbEntity, GroceryStoreDbEntity.Builder>
                currentUpdate(
                groceryStore,
                e -> e.name(groceryStore.name() + " updated"),
                updateTime);
        GroceryStoreDbEntity updatedEntity = updated.get(2);
        stocksDatabase.synchronisationDao().writeGroceryStores(updated);
        setArtificialDbNow(updateTime.plusSeconds(1));

        var actual = uut.getPricesForTable(food::id);

        testList(actual).assertValue(singletonList(PriceForTableListingData.create(
                Instant.EPOCH,
                updatedEntity.name(),
                groceryChain.name(),
                price.price(),
                price.scale(),
                scaledUnit.scale(),
                unit.abbreviation()
        )));
    }

    @Test
    public void pricesFromDifferentStoresAreNotGrouped() {
        var otherGroceryStore = standardEntities.groceryStoreDbEntityBuilder()
                .name("City")
                .groceryChain(groceryChain.id())
                .build();
        stocksDatabase.synchronisationDao().writeGroceryStores(List.of(otherGroceryStore));
        PriceDbEntity price = standardEntities.priceDbEntityBuilder()
                .food(food.id())
                .groceryStore(groceryStore.id())
                .scaledUnit(scaledUnit.id())
                .build();
        PriceDbEntity priceFromOtherStore = standardEntities.priceDbEntityBuilder()
                .validTimeStart(Instant.EPOCH.plusSeconds(2))
                .validTimeEnd(Instant.EPOCH.plusSeconds(3))
                .food(food.id())
                .groceryStore(otherGroceryStore.id())
                .scaledUnit(scaledUnit.id())
                .build();
        stocksDatabase.synchronisationDao().writePrices(List.of(price, priceFromOtherStore));

        var actual = uut.getPricesForTable(food::id);

        testList(actual).assertValue(List.of(PriceForTableListingData.create(
                priceFromOtherStore.validTimeStart(),
                otherGroceryStore.name(),
                groceryChain.name(),
                priceFromOtherStore.price(),
                priceFromOtherStore.scale(),
                scaledUnit.scale(),
                unit.abbreviation()
        ), PriceForTableListingData.create(
                price.validTimeStart(),
                groceryStore.name(),
                groceryChain.name(),
                price.price(),
                price.scale(),
                scaledUnit.scale(),
                unit.abbreviation()
        )));
    }
}