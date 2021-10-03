/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2021  The stocks developers
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

package de.njsm.stocks.android.repo;

import de.njsm.stocks.android.db.entities.EanNumber;
import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.db.entities.FoodItem;
import de.njsm.stocks.android.db.entities.Location;
import de.njsm.stocks.android.db.entities.Recipe;
import de.njsm.stocks.android.db.entities.RecipeIngredient;
import de.njsm.stocks.android.db.entities.RecipeProduct;
import de.njsm.stocks.android.db.entities.ScaledUnit;
import de.njsm.stocks.android.db.entities.Unit;
import de.njsm.stocks.android.db.entities.User;
import de.njsm.stocks.android.db.entities.UserDevice;
import de.njsm.stocks.android.db.entities.VersionedData;
import de.njsm.stocks.common.api.*;
import de.njsm.stocks.common.api.visitor.BitemporalVisitor;

public class BitemporalEntityMapperVisitor implements BitemporalVisitor<Void, VersionedData> {

    public EanNumber bitemporalEanNumber(BitemporalEanNumber bitemporalEanNumber) {
        return bitemporalEanNumber(bitemporalEanNumber, null);
    }

    @Override
    public EanNumber bitemporalEanNumber(BitemporalEanNumber data, Void unused) {
        return new EanNumber(
                data.id(),
                data.validTimeStart(),
                data.validTimeEnd(),
                data.transactionTimeStart(),
                data.transactionTimeEnd(),
                data.version(),
                data.initiates(),
                data.eanNumber(),
                data.identifiesFood()
        );
    }
    public Food bitemporalFood(BitemporalFood bitemporalFood) {
        return bitemporalFood(bitemporalFood, null);
    }

    @Override
    public Food bitemporalFood(BitemporalFood data, Void unused) {
        return new Food(
                data.id(),
                data.validTimeStart(),
                data.validTimeEnd(),
                data.transactionTimeStart(),
                data.transactionTimeEnd(),
                data.version(),
                data.initiates(),
                data.name(),
                data.toBuy(),
                data.expirationOffset().getDays(),
                data.location() != null ? data.location() : 0,
                data.description(),
                data.storeUnit()
        );
    }

    public FoodItem bitemporalFoodItem(BitemporalFoodItem bitemporalFoodItem) {
        return bitemporalFoodItem(bitemporalFoodItem, null);
    }

    @Override
    public FoodItem bitemporalFoodItem(BitemporalFoodItem data, Void unused) {
        return new FoodItem(
                data.id(),
                data.validTimeStart(),
                data.validTimeEnd(),
                data.transactionTimeStart(),
                data.transactionTimeEnd(),
                data.version(),
                data.initiates(),
                data.eatByDate(),
                data.ofType(),
                data.storedIn(),
                data.registers(),
                data.buys(),
                data.unit()
        );
    }

    public Location bitemporalLocation(BitemporalLocation bitemporalLocation) {
        return bitemporalLocation(bitemporalLocation, null);
    }

    @Override
    public Location bitemporalLocation(BitemporalLocation data, Void unused) {
        return new Location(
                data.id(),
                data.validTimeStart(),
                data.validTimeEnd(),
                data.transactionTimeStart(),
                data.transactionTimeEnd(),
                data.version(),
                data.initiates(),
                data.name(),
                data.description()
        );
    }

    public Recipe bitemporalRecipe(BitemporalRecipe bitemporalRecipe) {
        return bitemporalRecipe(bitemporalRecipe, null);
    }

    @Override
    public Recipe bitemporalRecipe(BitemporalRecipe data, Void unused) {
        return new Recipe(
                data.id(),
                data.validTimeStart(),
                data.validTimeEnd(),
                data.transactionTimeStart(),
                data.transactionTimeEnd(),
                data.version(),
                data.initiates(),
                data.name(),
                data.instructions(),
                data.duration()
        );
    }

    public ScaledUnit bitemporalScaledUnit(BitemporalScaledUnit bitemporalScaledUnit) {
        return bitemporalScaledUnit(bitemporalScaledUnit, null);
    }

    @Override
    public ScaledUnit bitemporalScaledUnit(BitemporalScaledUnit data, Void unused) {
        return new ScaledUnit(
                data.id(),
                data.validTimeStart(),
                data.validTimeEnd(),
                data.transactionTimeStart(),
                data.transactionTimeEnd(),
                data.version(),
                data.initiates(),
                data.scale(),
                data.unit()
        );
    }

    public Unit bitemporalUnit(BitemporalUnit bitemporalUnit) {
        return bitemporalUnit(bitemporalUnit, null);
    }

    @Override
    public Unit bitemporalUnit(BitemporalUnit data, Void unused) {
        return new Unit(
                data.id(),
                data.validTimeStart(),
                data.validTimeEnd(),
                data.transactionTimeStart(),
                data.transactionTimeEnd(),
                data.version(),
                data.initiates(),
                data.name(),
                data.abbreviation()
        );
    }

    public UserDevice bitemporalUserDevice(BitemporalUserDevice bitemporalUserDevice) {
        return bitemporalUserDevice(bitemporalUserDevice, null);
    }

    @Override
    public UserDevice bitemporalUserDevice(BitemporalUserDevice data, Void unused) {
        return new UserDevice(
                data.id(),
                data.validTimeStart(),
                data.validTimeEnd(),
                data.transactionTimeStart(),
                data.transactionTimeEnd(),
                data.version(),
                data.initiates(),
                data.name(),
                data.belongsTo()
        );
    }

    public User bitemporalUser(BitemporalUser bitemporalUser) {
        return bitemporalUser(bitemporalUser, null);
    }

    @Override
    public User bitemporalUser(BitemporalUser data, Void unused) {
        return new User(
                data.id(),
                data.validTimeStart(),
                data.validTimeEnd(),
                data.transactionTimeStart(),
                data.transactionTimeEnd(),
                data.version(),
                data.initiates(),
                data.name()
        );
    }
    public RecipeIngredient bitemporalRecipeIngredient(BitemporalRecipeIngredient bitemporalRecipeIngredient) {
        return bitemporalRecipeIngredient(bitemporalRecipeIngredient, null);
    }

    @Override
    public RecipeIngredient bitemporalRecipeIngredient(BitemporalRecipeIngredient data, Void unused) {
        return new RecipeIngredient(
                data.id(),
                data.validTimeStart(),
                data.validTimeEnd(),
                data.transactionTimeStart(),
                data.transactionTimeEnd(),
                data.version(),
                data.initiates(),
                data.amount(),
                data.ingredient(),
                data.recipe(),
                data.unit()
        );
    }

    public RecipeProduct bitemporalRecipeProduct(BitemporalRecipeProduct bitemporalRecipeProduct) {
        return bitemporalRecipeProduct(bitemporalRecipeProduct, null);
    }

    @Override
    public RecipeProduct bitemporalRecipeProduct(BitemporalRecipeProduct data, Void unused) {
        return new RecipeProduct(
                data.id(),
                data.validTimeStart(),
                data.validTimeEnd(),
                data.transactionTimeStart(),
                data.transactionTimeEnd(),
                data.version(),
                data.initiates(),
                data.amount(),
                data.product(),
                data.recipe(),
                data.unit()
        );
    }
}
