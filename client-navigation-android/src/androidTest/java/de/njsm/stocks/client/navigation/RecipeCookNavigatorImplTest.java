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
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.IdImpl;
import de.njsm.stocks.client.business.entities.Recipe;
import de.njsm.stocks.client.fragment.recipecook.RecipeCookFragmentDirections;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RecipeCookNavigatorImplTest extends NavigationTest {

    private RecipeCookNavigatorImpl uut;

    @Before
    public void setup() {
        uut = new RecipeCookNavigatorImpl(navigationArgConsumer);
    }

    @Test
    public void argumentIsExtracted() {
        Bundle input = new Bundle();
        int expected = 42;
        input.putInt("id", expected);

        Id<Recipe> actual = uut.getRecipe(input);

        assertEquals(expected, actual.id());
    }

    @Test
    public void showingFoodContentBindsCorrectly() {
        IdImpl<Food> expected = IdImpl.create(42);

        uut.showFood(expected);

        RecipeCookFragmentDirections.ActionNavFragmentRecipeCookToNavFragmentFoodItemList actual = navigationArgConsumer.getLastArgument(RecipeCookFragmentDirections.ActionNavFragmentRecipeCookToNavFragmentFoodItemList.class);
        assertEquals(expected.id(), actual.getFoodId());
    }
}
