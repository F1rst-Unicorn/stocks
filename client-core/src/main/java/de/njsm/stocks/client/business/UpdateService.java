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

    List<LocationForSynchronisation> getLocations(Instant startingFrom, Instant upUntil);

    List<UserForSynchronisation> getUsers(Instant startingFrom, Instant upUntil);

    List<UserDeviceForSynchronisation> getUserDevices(Instant startingFrom, Instant upUntil);

    List<FoodForSynchronisation> getFood(Instant startingFrom, Instant upUntil);

    List<EanNumberForSynchronisation> getEanNumbers(Instant startingFrom, Instant upUntil);

    List<FoodItemForSynchronisation> getFoodItems(Instant startingFrom, Instant upUntil);

    List<UnitForSynchronisation> getUnits(Instant startingFrom, Instant upUntil);

    List<ScaledUnitForSynchronisation> getScaledUnits(Instant startingFrom, Instant upUntil);

    List<RecipeForSynchronisation> getRecipes(Instant startingFrom, Instant upUntil);

    List<RecipeIngredientForSynchronisation> getRecipeIngredients(Instant startingFrom, Instant upUntil);

    List<RecipeProductForSynchronisation> getRecipeProducts(Instant startingFrom, Instant upUntil);
}
