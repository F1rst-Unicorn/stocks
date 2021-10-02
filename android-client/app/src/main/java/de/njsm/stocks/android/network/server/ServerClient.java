/* stocks is client-server program to manage a household's food stock
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
 */

package de.njsm.stocks.android.network.server;


import de.njsm.stocks.android.frontend.device.ServerTicket;
import de.njsm.stocks.common.api.*;
import retrofit2.Call;

public class ServerClient {

    private final Api api;

    public ServerClient(Api api) {
        this.api = api;
    }

    public Call<ListResponse<BitemporalUser>> getUsers(String startingFrom) {
        return api.getUsers(1, startingFrom);
    }

    public Call<ListResponse<BitemporalUserDevice>> getDevices(String startingFrom) {
        return api.getDevices(1, startingFrom);
    }

    public Call<ListResponse<BitemporalLocation>> getLocations(String startingFrom) {
        return api.getLocations(1, startingFrom);
    }

    public Call<ListResponse<BitemporalFood>> getFood(String startingFrom) {
        return api.getFood(1, startingFrom);
    }


    public Call<ListResponse<BitemporalFoodItem>> getFoodItems(String startingFrom) {
        return api.getFoodItems(1, startingFrom);
    }

    public Call<ListResponse<BitemporalEanNumber>> getEanNumbers(String startingFrom) {
        return api.getEanNumbers(1, startingFrom);
    }

    public Call<ListResponse<BitemporalUnit>> getUnits(String startingFrom) {
        return api.getUnits(1, startingFrom);
    }

    public Call<ListResponse<BitemporalScaledUnit>> getScaledUnits(String startingFrom) {
        return api.getScaledUnits(1, startingFrom);
    }

    public Call<ListResponse<BitemporalRecipe>> getRecipes(String startingFrom) {
        return api.getRecipes(1, startingFrom);
    }

    public Call<ListResponse<BitemporalRecipeIngredient>> getRecipeIngredients(String startingFrom) {
        return api.getRecipeIngredients(1, startingFrom);
    }

    public Call<ListResponse<BitemporalRecipeProduct>> getRecipeProducts(String startingFrom) {
        return api.getRecipeProducts(1, startingFrom);
    }

    public Call<Response> addUser(String name) {
        return api.addUser(name);
    }

    public Call<Response> deleteUser(int id, int version) {
        return api.deleteUser(id, version);
    }

    public Call<DataResponse<ServerTicket>> addDevice(String name, int uid) {
        return api.addDevice(name, uid);
    }

    public Call<Response> deleteDevice(int id, int version) {
        return api.deleteDevice(id, version);
    }

    public Call<ListResponse<Update>> getUpdates() {
        return api.getUpdates();
    }

    public Call<Response> addLocation(String name) {
        return api.addLocation(name);
    }

    public Call<Response> renameLocation(int id, int version, String newName) {
        return api.renameLocation(id, version, newName);
    }

    public Call<Response> setLocationDescription(int id, int version, String description) {
        return api.setLocationDescription(id, version, description);
    }

    public Call<Response> deleteLocation(int id, int version, int cascade) {
        return api.deleteLocation(id, version, cascade);
    }

    public Call<Response> addFood(String name) {
        return api.addFood(name);
    }

    public Call<Response> editFood(int id, int version, String newName, int expirationOffset, int location, String description, int storeUnit) {
        return api.editFood(id, version, newName, expirationOffset, location, description, storeUnit);
    }

    public Call<Response> setToBuyStatus(int id, int version, int toBuy) {
        return api.setToBuyStatus(id, version, toBuy);
    }

    public Call<Response> deleteFood(int id, int version) {
        return api.deleteFood(id, version);
    }

    public Call<Response> addFoodItem(String eatByDate, int storedIn, int ofType, int unit) {
        return api.addFoodItem(eatByDate, storedIn, ofType, unit);
    }

    public Call<Response> editFoodItem(int id, int version, String eatByDate, int storedIn, int unit) {
        return api.editFoodItem(id, version, eatByDate, storedIn, unit);
    }

    public Call<Response> deleteFoodItem(int id, int version) {
        return api.deleteFoodItem(id, version);
    }

    public Call<Response> addEanNumber(String code, int identifies) {
        return api.addEanNumber(code, identifies);
    }

    public Call<Response> deleteEanNumber(int id, int version) {
        return api.deleteEanNumber(id, version);
    }

    public Call<Response> deleteUnit(int id, int version) {
        return api.deleteUnit(id, version);
    }

    public Call<Response> editUnit(int id, int version, String newName, String newAbbreviation) {
        return api.editUnit(id, version, newName, newAbbreviation);
    }

    public Call<Response> addUnit(String name, String abbreviation) {
        return api.addUnit(name, abbreviation);
    }

    public Call<Response> deleteScaledUnit(int id, int version) {
        return api.deleteScaledUnit(id, version);
    }

    public Call<Response> editScaledUnit(int id, int version, String scale, int unit) {
        return api.editScaledUnit(id, version, scale, unit);
    }

    public Call<Response> addScaledUnit(String scale, int unit) {
        return api.addScaledUnit(scale, unit);
    }

    public Call<Response> addRecipe(FullRecipeForInsertion recipe) {
        return api.addRecipe(recipe);
    }

    public Call<Response> deleteRecipe(FullRecipeForDeletion recipe) {
        return api.deleteRecipe(recipe);
    }

    public Call<Response> editRecipe(FullRecipeForEditing recipe) {
        return api.editRecipe(recipe);
    }
}
