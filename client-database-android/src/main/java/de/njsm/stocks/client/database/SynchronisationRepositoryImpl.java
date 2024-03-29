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

import de.njsm.stocks.client.business.SynchronisationRepository;
import de.njsm.stocks.client.business.entities.*;

import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.toList;

class SynchronisationRepositoryImpl implements SynchronisationRepository {

    private final SynchronisationDao synchronisationDao;

    @Inject
    SynchronisationRepositoryImpl(SynchronisationDao synchronisationDao) {
        this.synchronisationDao = synchronisationDao;
    }

    @Override
    public List<Update> getUpdates() {
        return synchronisationDao.getAll().stream().map(DataMapper::map).collect(toList());
    }

    @Override
    public void writeUpdates(List<Update> updates) {
        synchronisationDao.writeUpdates(updates.stream().map(DataMapper::map).collect(toList()));
    }

    @Override
    public void writeLocations(List<LocationForSynchronisation> locations) {
        synchronisationDao.writeLocations(locations.stream().map(DataMapper::map).collect(toList()));
    }

    @Override
    public void initialiseLocations(List<LocationForSynchronisation> locations) {
        synchronisationDao.synchroniseLocations(locations.stream().map(DataMapper::map).collect(toList()));
    }

    @Override
    public void writeUsers(List<UserForSynchronisation> users) {
        synchronisationDao.writeUsers(users.stream().map(DataMapper::map).collect(toList()));
    }

    @Override
    public void initialiseUsers(List<UserForSynchronisation> users) {
        synchronisationDao.synchroniseUsers(users.stream().map(DataMapper::map).collect(toList()));
    }

    @Override
    public void writeUserDevices(List<UserDeviceForSynchronisation> userDevices) {
        synchronisationDao.writeUserDevices(userDevices.stream().map(DataMapper::map).collect(toList()));
    }

    @Override
    public void initialiseUserDevices(List<UserDeviceForSynchronisation> userDevices) {
        synchronisationDao.synchroniseUserDevices(userDevices.stream().map(DataMapper::map).collect(toList()));
    }

    @Override
    public void writeFood(List<FoodForSynchronisation> food) {
        synchronisationDao.writeFood(food.stream().map(DataMapper::map).collect(toList()));
    }

    @Override
    public void initialiseFood(List<FoodForSynchronisation> food) {
        synchronisationDao.synchroniseFood(food.stream().map(DataMapper::map).collect(toList()));
    }

    @Override
    public void writeEanNumbers(List<EanNumberForSynchronisation> eanNumbers) {
        synchronisationDao.writeEanNumbers(eanNumbers.stream().map(DataMapper::map).collect(toList()));
    }

    @Override
    public void initialiseEanNumbers(List<EanNumberForSynchronisation> eanNumbers) {
        synchronisationDao.synchroniseEanNumbers(eanNumbers.stream().map(DataMapper::map).collect(toList()));
    }

    @Override
    public void writeFoodItems(List<FoodItemForSynchronisation> foodItems) {
        synchronisationDao.writeFoodItems(foodItems.stream().map(DataMapper::map).collect(toList()));
    }

    @Override
    public void initialiseFoodItems(List<FoodItemForSynchronisation> foodItems) {
        synchronisationDao.synchroniseFoodItems(foodItems.stream().map(DataMapper::map).collect(toList()));
    }

    @Override
    public void writeUnits(List<UnitForSynchronisation> units) {
        synchronisationDao.writeUnits(units.stream().map(DataMapper::map).collect(toList()));
    }

    @Override
    public void initialiseUnits(List<UnitForSynchronisation> units) {
        synchronisationDao.synchroniseUnits(units.stream().map(DataMapper::map).collect(toList()));
    }

    @Override
    public void writeScaledUnits(List<ScaledUnitForSynchronisation> scaledUnits) {
        synchronisationDao.writeScaledUnits(scaledUnits.stream().map(DataMapper::map).collect(toList()));
    }

    @Override
    public void initialiseScaledUnits(List<ScaledUnitForSynchronisation> scaledUnits) {
        synchronisationDao.synchroniseScaledUnits(scaledUnits.stream().map(DataMapper::map).collect(toList()));
    }

    @Override
    public void writeRecipes(List<RecipeForSynchronisation> recipes) {
        synchronisationDao.writeRecipes(recipes.stream().map(DataMapper::map).collect(toList()));
    }

    @Override
    public void initialiseRecipes(List<RecipeForSynchronisation> recipes) {
        synchronisationDao.synchroniseRecipes(recipes.stream().map(DataMapper::map).collect(toList()));
    }

    @Override
    public void writeRecipeIngredients(List<RecipeIngredientForSynchronisation> recipeIngredients) {
        synchronisationDao.writeRecipeIngredients(recipeIngredients.stream().map(DataMapper::map).collect(toList()));
    }

    @Override
    public void initialiseRecipeIngredients(List<RecipeIngredientForSynchronisation> recipeIngredients) {
        synchronisationDao.synchroniseRecipeIngredients(recipeIngredients.stream().map(DataMapper::map).collect(toList()));
    }

    @Override
    public void writeRecipeProducts(List<RecipeProductForSynchronisation> recipeProducts) {
        synchronisationDao.writeRecipeProducts(recipeProducts.stream().map(DataMapper::map).collect(toList()));
    }

    @Override
    public void initialiseRecipeProducts(List<RecipeProductForSynchronisation> recipeProducts) {
        synchronisationDao.synchroniseRecipeProducts(recipeProducts.stream().map(DataMapper::map).collect(toList()));
    }
}
