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

import android.app.SearchManager;
import android.database.Cursor;
import com.google.auto.value.AutoValue;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static de.njsm.stocks.client.database.SearchRepositorySuggestionsTest.Row.row;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

public class SearchRepositorySuggestionsTest extends DbTestCase {

    private SearchRepositoryImpl uut;

    private int lastSearchedCounter = 0;

    @Before
    public void setUp() {
        uut = new SearchRepositoryImpl(stocksDatabase.searchDao(), this);
    }

    @Test
    public void searchingForNothingListsFood() {
        insertFoods("Banana");

        List<Row> list = search("");

        assertThat(list, contains(withName("Banana")));
    }

    @Test
    public void foodNotContainingLetterIsFiltered() {
        insertFoods("Banana");
        insertSearchTerms("Apple");
        insertRecentlySearchedFood("Pear");

        var list = search("x");

        assertThat(list, is(empty()));
    }

    @Test
    public void longerNamesAreSortedFirst() {
        insertFoods("Orange", "Orange Juice");

        List<Row> list = search("");

        assertThat(list, contains(
                withName("Orange Juice"),
                withName("Orange")
        ));
    }

    @Test
    public void fullSearchTakesPrecedence() {
        insertFoods("Orange", "Orange Juice");

        List<Row> list = search("Orange");

        assertThat(list, contains(
                withName("Orange"),
                withName("Orange Juice")
        ));
    }

    @Test
    public void fullSearchTakesPrecedenceCaseInsensitive() {
        insertFoods("Orange", "Orange Juice");

        List<Row> list = search("orange");

        assertThat(list, contains(
                withName("Orange"),
                withName("Orange Juice")
        ));
    }

    @Test
    public void searchingForSubstringWorks() {
        insertFoods("Orange");

        List<Row> list = search("ra");

        assertThat(list, contains(
                withName("Orange")
        ));
    }

    @Test
    public void searchingForSubsequenceWorks() {
        insertFoods("Orange");

        List<Row> list = search("rg");

        assertThat(list, contains(
                withName("Orange")
        ));
    }

    @Test
    public void substringTakesPrecedenceBeforeSubsequence() {
        insertFoods("Apple", "Pear");

        List<Row> list = search("pe");

        assertThat(list, contains(
                withName("Pear"),
                withName("Apple")
        ));
    }

    @Test
    public void fullFoodPrecedenceExample() {
        insertFoods("baab", "cbaabc", "cbacabc");

        List<Row> list = search("baab");

        assertThat(list, contains(
                withName("baab"),
                withName("cbaabc"),
                withName("cbacabc")
        ));
    }

    @Test
    public void recentSearchesAreSortedByLastSearched() {
        insertSearchTerms("Pear", "Banana", "Apple");

        var list = search("");

        assertThat(list, contains(
                withName("Apple"),
                withName("Banana"),
                withName("Pear")
        ));
    }

    @Test
    public void searchedFoodAndTermsAreInterspersedByLastSearched() {
        insertSearchTerms("Pear");
        insertRecentlySearchedFood("Apple");
        insertSearchTerms("Banana");
        insertRecentlySearchedFood("Orange");

        var list = search("");

        assertThat(list, contains(
                withName("Orange"),
                withName("Banana"),
                withName("Apple"),
                withName("Pear")
        ));
    }

    @Test
    public void searchedFoodSubsequenceIsBeforeSearchTermsSubsequence() {
        insertRecentlySearchedFood("abc");
        insertSearchTerms("aec");

        var list = search("ac");

        assertThat(list, contains(
                withName("abc"),
                withName("aec")
        ));
    }

    @Test
    public void fullSortOrderTest() {
        insertFoods("cf", "Chilif", "Chilifa", "cfera", "cferaa");
        insertSearchTerms("cof");
        insertRecentlySearchedFood("cef");
        insertSearchTerms("cfdsa");
        insertRecentlySearchedFood("acfdsa");

        var list = search("cf");

        assertThat(list, contains(
                withName("cf"),         // exact match
                withName("acfdsa"),     // searched food substring
                withName("cfdsa"),      // search term substring
                withName("cef"),        // searched food subsequence
                withName("cof"),        // search term subsequence
                withName("cferaa"),     // non-searched food substring
                withName("cfera"),      // non-searched food substring
                withName("Chilifa"),    // non-searched food subsequence
                withName("Chilif")      // non-searched food subsequence
        ));
    }

    private void insertFoods(String... names) {
        var food = Arrays.stream(names)
                .map(v -> standardEntities.foodDbEntityBuilder()
                        .name(v)
                        .build())
                .collect(Collectors.toList());
        stocksDatabase.synchronisationDao().writeFood(food);
    }

    private void insertSearchTerms(String... terms) {
        Arrays.stream(terms)
                .map(v -> RecentSearchDbEntity.create(
                        v,
                        Instant.ofEpochMilli(lastSearchedCounter++)
                ))
                .forEach(stocksDatabase.searchDao()::store);
    }

    private void insertRecentlySearchedFood(String... names) {
        var foods = Arrays.stream(names)
                .map(v -> standardEntities.foodDbEntityBuilder()
                        .name(v)
                        .build())
                .collect(Collectors.toList());
        stocksDatabase.synchronisationDao().writeFood(foods);
        foods.stream()
                .map(v -> SearchedFoodDbEntity.create(
                        v.id(),
                        Instant.ofEpochMilli(lastSearchedCounter++)
                ))
                .forEach(stocksDatabase.searchDao()::store);
    }

    private List<Row> search(String query) {
        Cursor cursor = uut.search(query);
        return readCursor(cursor);
    }

    private List<Row> readCursor(Cursor actual) {
        ArrayList<Row> result = new ArrayList<>();
        while (actual.moveToNext()) {
            result.add(row(
                    actual.getInt(actual.getColumnIndex(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID)),
                    actual.getString(actual.getColumnIndex(SearchManager.SUGGEST_COLUMN_QUERY))
            ));
        }
        return result;
    }

    private static Matcher<Row> withName(String name) {
        return new TypeSafeMatcher<>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("with name '" + name + "'");
            }

            @Override
            public boolean matchesSafely(Row item) {
                return item.name().equals(name);
            }
        };
    }

    @AutoValue
    static abstract class Row {
        abstract int id();

        abstract String name();

        static Row row(int id, String name) {
            return new AutoValue_SearchRepositorySuggestionsTest_Row(id, name);
        }
    }
}
