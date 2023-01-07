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

package de.njsm.stocks.client.navigation;

import android.os.Bundle;
import de.njsm.stocks.client.fragment.searchedfood.SearchedFoodFragmentDirections;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SearchedFoodNavigatorImplTest extends NavigationTest {

    private SearchedFoodNavigator uut;

    @Before
    public void setUp() {
        uut = new SearchedFoodNavigatorImpl(navigationArgConsumer);
    }

    @Test
    public void argumentIsExtracted() {
        Bundle input = new Bundle();
        String expected = "fdsa";
        input.putString("query", expected);

        String actual = uut.getQuery(input);

        assertEquals(expected, actual);
    }

    @Test
    public void showingFoodContentBindsCorrectly() {
        int expected = 42;

        uut.showFood(expected);

        var actual = navigationArgConsumer.getLastArgument(SearchedFoodFragmentDirections.ActionNavFragmentSearchResultsToNavFragmentFoodItemTabs.class);
        assertEquals(actual.getFoodId(), expected);
    }

    @Test
    public void editingFoodContentBindsCorrectly() {
        int expected = 42;

        uut.editFood(expected);

        var actual = navigationArgConsumer.getLastArgument(SearchedFoodFragmentDirections.ActionNavFragmentSearchResultsToNavFragmentEditFood.class);
        assertEquals(actual.getId(), expected);
    }
}