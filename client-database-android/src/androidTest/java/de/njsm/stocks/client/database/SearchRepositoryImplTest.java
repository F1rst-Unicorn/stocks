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
        var foundFoodBySameName = standardEntities.foodDbEntityBuilder()
                .name("oun")
                .build();
        var foundFoodByContiguousRange = standardEntities.foodDbEntityBuilder()
                .name("xxounxx")
                .build();
        var foundFoodBySubsequence = standardEntities.foodDbEntityBuilder()
                .name("xoxuxnx")
                .build();
        var filteredFood = standardEntities.foodDbEntityBuilder()
                .name("filtered")
                .build();
        stocksDatabase.synchronisationDao().writeFood(List.of(
                foundFoodBySameName,
                foundFoodByContiguousRange,
                foundFoodBySubsequence,
                filteredFood));

        var actual = uut.getFoodBy("oun");

        testList(actual).assertValue(List.of(
                fromEntity(foundFoodBySameName),
                fromEntity(foundFoodByContiguousRange),
                fromEntity(foundFoodBySubsequence)
        ));
    }

    private static SearchedFoodForListingBaseData fromEntity(FoodDbEntity entity) {
        return SearchedFoodForListingBaseData.create(
                entity.id(),
                entity.name(),
                entity.toBuy()
        );
    }

    @Test
    public void gettingFoodItemAmountsBySearchQueryWorks() {
        var unit = standardEntities.unitDbEntity();
        stocksDatabase.synchronisationDao().writeUnits(List.of(unit));
        var scaledUnit = standardEntities.scaledUnitDbEntityBuilder()
                .unit(unit.id())
                .build();
        stocksDatabase.synchronisationDao().writeScaledUnits(List.of(scaledUnit));
        var foundFoodBySameName = standardEntities.foodDbEntityBuilder()
                .name("oun")
                .storeUnit(scaledUnit.id())
                .build();
        var foundFoodByContiguousRange = standardEntities.foodDbEntityBuilder()
                .name("xxounxx")
                .storeUnit(scaledUnit.id())
                .build();
        var foundFoodBySubsequence = standardEntities.foodDbEntityBuilder()
                .name("xoxuxnx")
                .storeUnit(scaledUnit.id())
                .build();
        var filteredFood = standardEntities.foodDbEntityBuilder()
                .name("filtered")
                .storeUnit(scaledUnit.id())
                .build();
        stocksDatabase.synchronisationDao().writeFood(List.of(
                foundFoodBySameName,
                foundFoodByContiguousRange,
                foundFoodBySubsequence,
                filteredFood));
        var foundFoodItemBySameName = standardEntities.foodItemDbEntityBuilder()
                .ofType(foundFoodBySameName.id())
                .unit(scaledUnit.id())
                .build();
        var foundFoodItemByContiguous = standardEntities.foodItemDbEntityBuilder()
                .ofType(foundFoodByContiguousRange.id())
                .unit(scaledUnit.id())
                .build();
        var foundFoodItemBySubsequence = standardEntities.foodItemDbEntityBuilder()
                .ofType(foundFoodBySubsequence.id())
                .unit(scaledUnit.id())
                .build();
        var filteredFoodItem = standardEntities.foodItemDbEntityBuilder()
                .ofType(filteredFood.id())
                .unit(scaledUnit.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(List.of(
                foundFoodItemBySameName,
                foundFoodItemByContiguous,
                foundFoodItemBySubsequence,
                filteredFoodItem));

        var actualObservable = uut.getFoodAmountsIn("oun");

        testList(actualObservable).assertValue(actual ->
                actual.size() == 3 &&
                actual.contains(
                        StoredFoodAmount.create(
                                foundFoodBySameName.id(),
                                scaledUnit.id(),
                                unit.id(),
                                scaledUnit.scale(),
                                unit.abbreviation(),
                                1
                        )
                ) &&
                actual.contains(
                        StoredFoodAmount.create(
                                foundFoodByContiguousRange.id(),
                                scaledUnit.id(),
                                unit.id(),
                                scaledUnit.scale(),
                                unit.abbreviation(),
                                1
                        )) &&
                actual.contains(
                        StoredFoodAmount.create(
                                foundFoodBySubsequence.id(),
                                scaledUnit.id(),
                                unit.id(),
                                scaledUnit.scale(),
                                unit.abbreviation(),
                                1
                        ))
        );
    }

    @Test
    public void gettingFoodAmountsOfAbsentFoodBySearchQueryWorks() {
        var unit = standardEntities.unitDbEntity();
        stocksDatabase.synchronisationDao().writeUnits(List.of(unit));
        var scaledUnit = standardEntities.scaledUnitDbEntityBuilder()
                .unit(unit.id())
                .build();
        stocksDatabase.synchronisationDao().writeScaledUnits(List.of(scaledUnit));
        var foundFoodBySameName = standardEntities.foodDbEntityBuilder()
                .name("oun")
                .storeUnit(scaledUnit.id())
                .build();
        var foundFoodByContiguousRange = standardEntities.foodDbEntityBuilder()
                .name("xxounxx")
                .storeUnit(scaledUnit.id())
                .build();
        var foundFoodBySubsequence = standardEntities.foodDbEntityBuilder()
                .name("xoxuxnx")
                .storeUnit(scaledUnit.id())
                .build();
        var filteredFood = standardEntities.foodDbEntityBuilder()
                .name("filtered")
                .storeUnit(scaledUnit.id())
                .build();
        stocksDatabase.synchronisationDao().writeFood(List.of(
                foundFoodBySameName,
                foundFoodByContiguousRange,
                foundFoodBySubsequence,
                filteredFood));

        var actualObservable = uut.getFoodDefaultUnitOfFoodWithoutItems("oun");

        testList(actualObservable).assertValue(actual ->
                actual.size() == 3 &&
                actual.contains(
                        StoredFoodAmount.create(
                                foundFoodBySameName.id(),
                                scaledUnit.id(),
                                unit.id(),
                                scaledUnit.scale(),
                                unit.abbreviation(),
                                0
                        )) &&
                actual.contains(
                        StoredFoodAmount.create(
                                foundFoodByContiguousRange.id(),
                                scaledUnit.id(),
                                unit.id(),
                                scaledUnit.scale(),
                                unit.abbreviation(),
                                0
                        )) &&
                actual.contains(
                        StoredFoodAmount.create(
                                foundFoodBySubsequence.id(),
                                scaledUnit.id(),
                                unit.id(),
                                scaledUnit.scale(),
                                unit.abbreviation(),
                                0
                        ))
        );
    }
}
