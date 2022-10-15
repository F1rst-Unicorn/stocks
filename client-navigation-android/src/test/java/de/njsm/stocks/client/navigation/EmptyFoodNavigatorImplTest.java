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

import androidx.navigation.ActionOnlyNavDirections;
import de.njsm.stocks.client.fragment.emptyfood.EmptyFoodFragmentDirections;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class EmptyFoodNavigatorImplTest extends NavigationTest {

    private EmptyFoodNavigator uut;

    @Before
    public void setUp() {
        uut = new EmptyFoodNavigatorImpl(navigationArgConsumer);
    }

    @Test
    public void showingFoodContentBindsCorrectly() {
        int expected = 42;

        uut.showFood(expected);

        EmptyFoodFragmentDirections.ActionNavFragmentEmptyFoodToNavFragmentFoodItemList actual = navigationArgConsumer.getLastArgument(EmptyFoodFragmentDirections.ActionNavFragmentEmptyFoodToNavFragmentFoodItemList.class);
        assertThat(actual.getFoodId(), is(expected));
    }

    @Test
    public void editingFoodContentBindsCorrectly() {
        int expected = 42;

        uut.editFood(expected);

        EmptyFoodFragmentDirections.ActionNavFragmentEmptyFoodToNavFragmentEditFood actual = navigationArgConsumer.getLastArgument(EmptyFoodFragmentDirections.ActionNavFragmentEmptyFoodToNavFragmentEditFood.class);
        assertThat(actual.getId(), is(expected));
    }

    @Test
    public void addingFoodContentBindsCorrectly() {
        uut.addFood();

        ActionOnlyNavDirections actual = navigationArgConsumer.getLastArgument(ActionOnlyNavDirections.class);
        assertThat(actual.getActionId(), is(R.id.action_nav_fragment_empty_food_to_nav_fragment_add_food));
    }
}