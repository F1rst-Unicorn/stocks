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

package de.njsm.stocks.client.business;

import de.njsm.stocks.client.business.entities.*;

import java.time.Instant;
import java.util.List;

public interface UpdateService {

    List<Update> getUpdates();

    List<LocationForSynchronisation> getLocations(Instant startingFrom);

    List<UserForSynchronisation> getUsers(Instant startingFrom);

    List<UserDeviceForSynchronisation> getUserDevices(Instant startingFrom);

    List<FoodForSynchronisation> getFood(Instant startingFrom);

    List<EanNumberForSynchronisation> getEanNumbers(Instant startingFrom);

    List<FoodItemForSynchronisation> getFoodItems(Instant startingFrom);

    List<UnitForSynchronisation> getUnits(Instant startingFrom);

    List<ScaledUnitForSynchronisation> getScaledUnits(Instant startingFrom);

    List<RecipeForSynchronisation> getRecipes(Instant startingFrom);

    List<RecipeIngredientForSynchronisation> getRecipeIngredients(Instant startingFrom);

    List<RecipeProductForSynchronisation> getRecipeProducts(Instant startingFrom);
}
