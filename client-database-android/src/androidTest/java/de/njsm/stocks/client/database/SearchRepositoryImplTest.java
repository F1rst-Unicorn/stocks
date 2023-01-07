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

import de.njsm.stocks.client.business.entities.SearchedFoodForListingBaseData;
import de.njsm.stocks.client.business.entities.StoredFoodAmount;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.List;

import static de.njsm.stocks.client.database.util.Util.testList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

public class SearchRepositoryImplTest extends DbTestCase {

    private SearchRepositoryImpl uut;

    @Before
    public void setUp() {
        uut = new SearchRepositoryImpl(stocksDatabase.searchDao(), this);
    }

    @Test
    public void storingSearchSuggestionWorks() {
        RecentSearchDbEntity expected = RecentSearchDbEntity.create("term", this.get());

        uut.storeRecentSearch(expected.term());

        var actual = stocksDatabase.searchDao().getRecentSearches();
        assertThat(actual, contains(is(expected)));
    }

    @Test
    public void storingAlreadyStoredSuggestionUpdatesTimestamp() {
        RecentSearchDbEntity expected = RecentSearchDbEntity.create("term", Instant.EPOCH.plusSeconds(3));
        uut.storeRecentSearch(expected.term());
        setNow(Instant.EPOCH.plusSeconds(3));

        uut.storeRecentSearch(expected.term());

        var actual = stocksDatabase.searchDao().getRecentSearches();
        assertThat(actual, contains(is(expected)));
    }

    @Test
    public void storingSearchedFoodWorks() {
        SearchedFoodDbEntity expected = SearchedFoodDbEntity.create(1, get());

        uut.storeFoundFood(expected::food);

        var actual = stocksDatabase.searchDao().getSearchedFood();
        assertThat(actual, contains(is(expected)));
    }

    @Test
    public void storingAlreadyStoredFoodUpdatesTimestamp() {
        SearchedFoodDbEntity expected = SearchedFoodDbEntity.create(1, Instant.EPOCH.plusSeconds(3));
        uut.storeFoundFood(expected::food);
        setNow(Instant.EPOCH.plusSeconds(3));

        uut.storeFoundFood(expected::food);

        var actual = stocksDatabase.searchDao().getSearchedFood();
        assertThat(actual, contains(is(expected)));
    }

    @Test
    public void gettingFoodBySearchQueryWorks() {
        var foundFood = standardEntities.foodDbEntityBuilder()
                .name("found")
                .build();
        var filteredFOod = standardEntities.foodDbEntityBuilder()
                .name("filtered")
                .build();
        stocksDatabase.synchronisationDao().writeFood(List.of(foundFood, filteredFOod));

        var actual = uut.getFoodBy("oun");

        testList(actual).assertValue(List.of(
                SearchedFoodForListingBaseData.create(
                        foundFood.id(),
                        foundFood.name(),
                        foundFood.toBuy()
                )
        ));
    }

    @Test
    public void gettingFoodItemAmountsBySearchQueryWorks() {
        var unit = standardEntities.unitDbEntity();
        stocksDatabase.synchronisationDao().writeUnits(List.of(unit));
        var scaledUnit = standardEntities.scaledUnitDbEntityBuilder()
                .unit(unit.id())
                .build();
        stocksDatabase.synchronisationDao().writeScaledUnits(List.of(scaledUnit));
        var foundFood = standardEntities.foodDbEntityBuilder()
                .name("found")
                .storeUnit(scaledUnit.id())
                .build();
        var filteredFood = standardEntities.foodDbEntityBuilder()
                .name("filtered")
                .storeUnit(scaledUnit.id())
                .build();
        stocksDatabase.synchronisationDao().writeFood(List.of(foundFood, filteredFood));
        var foundFoodItem = standardEntities.foodItemDbEntityBuilder()
                .ofType(foundFood.id())
                .unit(scaledUnit.id())
                .build();
        var filteredFoodItem = standardEntities.foodItemDbEntityBuilder()
                .ofType(filteredFood.id())
                .unit(scaledUnit.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(List.of(foundFoodItem, filteredFoodItem));

        var actual = uut.getFoodAmountsIn("oun");

        testList(actual).assertValue(List.of(
                StoredFoodAmount.create(
                        foundFood.id(),
                        scaledUnit.id(),
                        unit.id(),
                        scaledUnit.scale(),
                        unit.abbreviation(),
                        1
                )
        ));
    }

    @Test
    public void gettingFoodAmountsOfAbsentFoodBySearchQueryWorks() {
        var unit = standardEntities.unitDbEntity();
        stocksDatabase.synchronisationDao().writeUnits(List.of(unit));
        var scaledUnit = standardEntities.scaledUnitDbEntityBuilder()
                .unit(unit.id())
                .build();
        stocksDatabase.synchronisationDao().writeScaledUnits(List.of(scaledUnit));
        var foundFood = standardEntities.foodDbEntityBuilder()
                .name("found")
                .storeUnit(scaledUnit.id())
                .build();
        var filteredFood = standardEntities.foodDbEntityBuilder()
                .name("filtered")
                .storeUnit(scaledUnit.id())
                .build();
        stocksDatabase.synchronisationDao().writeFood(List.of(foundFood, filteredFood));

        var actual = uut.getFoodDefaultUnitOfFoodWithoutItems("oun");

        testList(actual).assertValue(List.of(
                StoredFoodAmount.create(
                        foundFood.id(),
                        scaledUnit.id(),
                        unit.id(),
                        scaledUnit.scale(),
                        unit.abbreviation(),
                        0
                )
        ));
    }
}
