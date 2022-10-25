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
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.Identifiable;
import de.njsm.stocks.client.fragment.fooditemlist.FoodItemListFragmentDirections;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FoodItemListNavigatorImplTest extends NavigationTest {

    private FoodItemListNavigator uut;

    @Before
    public void setup() {
        uut = new FoodItemListNavigatorImpl(navigationArgConsumer);
    }

    @Test
    public void argumentIsExtracted() {
        Bundle input = new Bundle();
        int expected = 42;
        input.putInt("foodId", expected);

        Identifiable<Food> actual = uut.getFoodId(input);

        assertEquals(expected, actual.id());
    }

    @Test
    public void editingContentBindsCorrectly() {
        int expected = 42;

        uut.edit(expected);

        FoodItemListFragmentDirections.ActionNavFragmentFoodItemListToNavFragmentFoodItemEdit actual = navigationArgConsumer.getLastArgument(FoodItemListFragmentDirections.ActionNavFragmentFoodItemListToNavFragmentFoodItemEdit.class);
        assertEquals(actual.getId(), expected);
    }

    @Test
    public void addingContentBindsCorrectly() {
        int expected = 42;

        uut.add(() -> expected);

        FoodItemListFragmentDirections.ActionNavFragmentFoodItemListToNavFragmentFoodItemAdd actual = navigationArgConsumer.getLastArgument(FoodItemListFragmentDirections.ActionNavFragmentFoodItemListToNavFragmentFoodItemAdd.class);
        assertEquals(actual.getFoodId(), expected);
    }
}