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

package de.njsm.stocks.client.database.error;

import javax.inject.Inject;

class DataDeleter implements ErrorEntity.ActionVisitor<Long, Void> {

    private final ErrorDao errorDao;

    @Inject
    DataDeleter(ErrorDao errorDao) {
        this.errorDao = errorDao;
    }


    @Override
    public Void synchronisation(ErrorEntity.Action action, Long id) {
        return null;
    }

    @Override
    public Void addLocation(ErrorEntity.Action action, Long id) {
        errorDao.deleteLocationAdd(id);
        return null;
    }

    @Override
    public Void deleteLocation(ErrorEntity.Action action, Long id) {
        errorDao.deleteLocationDelete(id);
        return null;
    }

    @Override
    public Void editLocation(ErrorEntity.Action action, Long id) {
        errorDao.deleteLocationEdit(id);
        return null;
    }

    @Override
    public Void addUnit(ErrorEntity.Action action, Long id) {
        errorDao.deleteUnitAdd(id);
        return null;
    }

    @Override
    public Void deleteUnit(ErrorEntity.Action action, Long id) {
        errorDao.deleteUnitDelete(id);
        return null;
    }

    @Override
    public Void editUnit(ErrorEntity.Action action, Long id) {
        errorDao.deleteUnitEdit(id);
        return null;
    }

    @Override
    public Void addScaledUnit(ErrorEntity.Action action, Long id) {
        errorDao.deleteScaledUnitAdd(id);
        return null;
    }

    @Override
    public Void editScaledUnit(ErrorEntity.Action action, Long id) {
        errorDao.deleteScaledUnitEdit(id);
        return null;
    }

    @Override
    public Void deleteScaledUnit(ErrorEntity.Action action, Long id) {
        errorDao.deleteScaledUnitDelete(id);
        return null;
    }

    @Override
    public Void addFood(ErrorEntity.Action action, Long id) {
        errorDao.deleteFoodAdd(id);
        return null;
    }

    @Override
    public Void deleteFood(ErrorEntity.Action action, Long id) {
        errorDao.deleteFoodDelete(id);
        return null;
    }

    @Override
    public Void editFood(ErrorEntity.Action action, Long id) {
        errorDao.deleteFoodEdit(id);
        return null;
    }

    @Override
    public Void addFoodItem(ErrorEntity.Action action, Long id) {
        errorDao.deleteFoodItemAdd(id);
        return null;
    }

    @Override
    public Void deleteFoodItem(ErrorEntity.Action action, Long id) {
        errorDao.deleteFoodItemDelete(id);
        return null;
    }

    @Override
    public Void editFoodItem(ErrorEntity.Action action, Long id) {
        errorDao.deleteFoodItemEdit(id);
        return null;
    }

    @Override
    public Void addEanNumber(ErrorEntity.Action action, Long id) {
        errorDao.deleteEanNumberAdd(id);
        return null;
    }

    @Override
    public Void deleteEanNumber(ErrorEntity.Action action, Long input) {
        errorDao.deleteEanNumberDelete(input);
        return null;
    }

    @Override
    public Void deleteUserDevice(ErrorEntity.Action action, Long input) {
        errorDao.deleteUserDeviceDelete(input);
        return null;
    }

    @Override
    public Void deleteUser(ErrorEntity.Action action, Long input) {
        errorDao.deleteUserDelete(input);
        return null;
    }

    @Override
    public Void addRecipe(ErrorEntity.Action action, Long input) {
        errorDao.deleteRecipeAdd(input);
        errorDao.deleteRecipeIngredientAdd(input);
        errorDao.deleteRecipeProductAdd(input);
        return null;
    }

    @Override
    public Void foodShopping(ErrorEntity.Action action, Long input) {
        errorDao.deleteFoodToBuy(input);
        return null;
    }

    @Override
    public Void addUser(ErrorEntity.Action action, Long input) {
        errorDao.deleteUserToAdd(input);
        return null;
    }

    @Override
    public Void addUserDevice(ErrorEntity.Action action, Long input) {
        errorDao.deleteUserDeviceToAdd(input);
        return null;
    }
}
