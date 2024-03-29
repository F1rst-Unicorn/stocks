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

import java.util.List;

public interface SynchronisationRepository {

    List<Update> getUpdates();

    void writeUpdates(List<Update> updates);

    void writeLocations(List<LocationForSynchronisation> locations);

    void initialiseLocations(List<LocationForSynchronisation> locations);

    void writeUsers(List<UserForSynchronisation> users);

    void initialiseUsers(List<UserForSynchronisation> users);

    void writeUserDevices(List<UserDeviceForSynchronisation> userDevices);

    void initialiseUserDevices(List<UserDeviceForSynchronisation> userDevices);

    void writeFood(List<FoodForSynchronisation> entities);

    void initialiseFood(List<FoodForSynchronisation> food);

    void writeEanNumbers(List<EanNumberForSynchronisation> entities);

    void initialiseEanNumbers(List<EanNumberForSynchronisation> entities);

    void writeFoodItems(List<FoodItemForSynchronisation> entities);

    void initialiseFoodItems(List<FoodItemForSynchronisation> entities);

    void writeUnits(List<UnitForSynchronisation> entities);

    void initialiseUnits(List<UnitForSynchronisation> entities);

    void writeScaledUnits(List<ScaledUnitForSynchronisation> entities);

    void initialiseScaledUnits(List<ScaledUnitForSynchronisation> entities);

    void writeRecipes(List<RecipeForSynchronisation> entities);

    void initialiseRecipes(List<RecipeForSynchronisation> entities);

    void writeRecipeIngredients(List<RecipeIngredientForSynchronisation> entities);

    void initialiseRecipeIngredients(List<RecipeIngredientForSynchronisation> entities);

    void writeRecipeProducts(List<RecipeProductForSynchronisation> entities);

    void initialiseRecipeProducts(List<RecipeProductForSynchronisation> entities);
}
