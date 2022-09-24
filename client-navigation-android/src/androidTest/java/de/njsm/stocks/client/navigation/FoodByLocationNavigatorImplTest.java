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
import androidx.navigation.ActionOnlyNavDirections;
import de.njsm.stocks.client.business.entities.Identifiable;
import de.njsm.stocks.client.business.entities.Location;
import de.njsm.stocks.client.fragment.emptyfood.EmptyFoodFragmentDirections;
import de.njsm.stocks.client.fragment.foodinlocation.FoodInLocationFragmentDirections;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FoodByLocationNavigatorImplTest extends NavigationTest {

    private FoodByLocationNavigator uut;

    @Before
    public void setUp() {
        uut = new FoodByLocationNavigatorImpl(navigationArgConsumer);
    }

    @Test
    public void argumentIsExtracted() {
        Bundle input = new Bundle();
        int expected = 42;
        input.putInt("id", expected);

        Identifiable<Location> actual = uut.getId(input);

        assertEquals(expected, actual.id());
    }

    @Test
    public void showingFoodContentBindsCorrectly() {
        int expected = 42;

        uut.showFood(expected);

        FoodInLocationFragmentDirections.ActionNavFragmentLocationContentToNavFragmentFoodItemList actual = navigationArgConsumer.getLastArgument(FoodInLocationFragmentDirections.ActionNavFragmentLocationContentToNavFragmentFoodItemList.class);
        assertEquals(actual.getId(), expected);
    }

    @Test
    public void editingFoodContentBindsCorrectly() {
        int expected = 42;

        uut.editFood(expected);

        FoodInLocationFragmentDirections.ActionNavFragmentLocationContentToNavFragmentEditFood actual = navigationArgConsumer.getLastArgument(FoodInLocationFragmentDirections.ActionNavFragmentLocationContentToNavFragmentEditFood.class);
        assertEquals(actual.getId(), expected);
    }

    @Test
    public void addingFoodContentBindsCorrectly() {
        uut.addFood();

        ActionOnlyNavDirections actual = navigationArgConsumer.getLastArgument(ActionOnlyNavDirections.class);
        assertEquals(actual.getActionId(), R.id.action_nav_fragment_location_content_to_nav_fragment_add_food);
    }
}