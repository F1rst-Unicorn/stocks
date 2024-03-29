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

public interface ErrorRecorder {

    void recordSynchronisationError(SubsystemException exception);

    void recordLocationAddError(SubsystemException exception, LocationAddForm form);

    void recordLocationDeleteError(SubsystemException exception, Versionable<Location> locationForDeletion);

    void recordLocationEditError(SubsystemException exception, LocationForEditing locationForEditing);

    void recordUnitAddError(SubsystemException exception, UnitAddForm input);

    void recordUnitDeleteError(SubsystemException exception, Versionable<Unit> outputToService);

    void recordUnitEditError(SubsystemException exception, UnitForEditing unitForEditing);

    void recordScaledUnitAddError(SubsystemException e, ScaledUnitAddForm form);

    void recordScaledUnitEditError(SubsystemException e, ScaledUnitForEditing scaledUnitForEditing);

    void recordScaledUnitDeleteError(SubsystemException exception, Versionable<ScaledUnit> scaledUnitForDeletion);

    void recordFoodAddError(SubsystemException exception, FoodAddForm input);

    void recordFoodDeleteError(SubsystemException exception, Versionable<Food> input);

    void recordFoodEditError(SubsystemException exception, FoodForEditing expected);

    void recordFoodItemAddError(SubsystemException e, FoodItemAddFormForErrorRecording item);

    void recordFoodItemDeleteError(SubsystemException exception, Versionable<FoodItem> outputToService);

    void recordFoodItemEditError(SubsystemException exception, FoodItemForEditing item);

    void recordEanNumberAddError(SubsystemException e, EanNumberAddForm eanNumberAddForm);

    void recordEanNumberDeleteError(SubsystemException e, Versionable<EanNumber> data);

    void recordUserDeviceDeleteError(SubsystemException e, Versionable<UserDevice> data);

    void recordUserDeleteError(SubsystemException e, Versionable<User> data);

    void recordRecipeAddError(SubsystemException e, RecipeAddForm input);

    void recordFoodToBuyError(SubsystemException e, FoodForBuying networkData);

    void recordUserAddError(SubsystemException e, UserAddForm form);

    void recordUserDeviceAddError(SubsystemException e, UserDeviceAddForm form);

    void recordRecipeDeleteError(SubsystemException e, Id<Recipe> data);

    void recordRecipeEditError(SubsystemException e, RecipeEditForm form);
}
