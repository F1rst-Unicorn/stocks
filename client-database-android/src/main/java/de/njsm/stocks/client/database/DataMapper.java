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

import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.database.error.LocationAddEntity;

public class DataMapper {

    static Update map(UpdateDbEntity input) {
        return Update.create(input.table(), input.lastUpdate());
    }

    static UpdateDbEntity map(Update input) {
        return UpdateDbEntity.create(input.table(), input.lastUpdate());
    }

    static LocationDbEntity map(LocationForSynchronisation location) {
        return LocationDbEntity.create(
                location.id(),
                location.version(),
                location.validTimeStart(),
                location.validTimeEnd(),
                location.transactionTimeStart(),
                location.transactionTimeEnd(),
                location.initiates(),
                location.name(),
                location.description()
        );
    }

    static UserDbEntity map(UserForSynchronisation user) {
        return UserDbEntity.create(
                user.id(),
                user.version(),
                user.validTimeStart(),
                user.validTimeEnd(),
                user.transactionTimeStart(),
                user.transactionTimeEnd(),
                user.initiates(),
                user.name()
        );
    }

    static UserDeviceDbEntity map(UserDeviceForSynchronisation userDevice) {
        return UserDeviceDbEntity.create(
                userDevice.id(),
                userDevice.version(),
                userDevice.validTimeStart(),
                userDevice.validTimeEnd(),
                userDevice.transactionTimeStart(),
                userDevice.transactionTimeEnd(),
                userDevice.initiates(),
                userDevice.name(),
                userDevice.belongsTo()
        );
    }

    static FoodDbEntity map(FoodForSynchronisation food) {
        return FoodDbEntity.create(
                food.id(),
                food.version(),
                food.validTimeStart(),
                food.validTimeEnd(),
                food.transactionTimeStart(),
                food.transactionTimeEnd(),
                food.initiates(),
                food.name(),
                food.toBuy(),
                food.expirationOffset(),
                food.location().orElse(null),
                food.storeUnit(),
                food.description()
        );
    }

    static EanNumberDbEntity map(EanNumberForSynchronisation eanNumber) {
        return EanNumberDbEntity.create(
                eanNumber.id(),
                eanNumber.version(),
                eanNumber.validTimeStart(),
                eanNumber.validTimeEnd(),
                eanNumber.transactionTimeStart(),
                eanNumber.transactionTimeEnd(),
                eanNumber.initiates(),
                eanNumber.number(),
                eanNumber.identifies()
        );
    }

    static FoodItemDbEntity map(FoodItemForSynchronisation foodItem) {
        return FoodItemDbEntity.create(
                foodItem.id(),
                foodItem.version(),
                foodItem.validTimeStart(),
                foodItem.validTimeEnd(),
                foodItem.transactionTimeStart(),
                foodItem.transactionTimeEnd(),
                foodItem.initiates(),
                foodItem.eatBy(),
                foodItem.ofType(),
                foodItem.storedIn(),
                foodItem.buys(),
                foodItem.registers(),
                foodItem.unit()
        );
    }

    static UnitDbEntity map(UnitForSynchronisation unit) {
        return UnitDbEntity.create(
                unit.id(),
                unit.version(),
                unit.validTimeStart(),
                unit.validTimeEnd(),
                unit.transactionTimeStart(),
                unit.transactionTimeEnd(),
                unit.initiates(),
                unit.name(),
                unit.abbreviation()
        );
    }

    static ScaledUnitDbEntity map(ScaledUnitForSynchronisation scaledUnit) {
        return ScaledUnitDbEntity.create(
                scaledUnit.id(),
                scaledUnit.version(),
                scaledUnit.validTimeStart(),
                scaledUnit.validTimeEnd(),
                scaledUnit.transactionTimeStart(),
                scaledUnit.transactionTimeEnd(),
                scaledUnit.initiates(),
                scaledUnit.scale(),
                scaledUnit.unit()
        );
    }

    static RecipeDbEntity map(RecipeForSynchronisation recipe) {
        return RecipeDbEntity.create(
                recipe.id(),
                recipe.version(),
                recipe.validTimeStart(),
                recipe.validTimeEnd(),
                recipe.transactionTimeStart(),
                recipe.transactionTimeEnd(),
                recipe.initiates(),
                recipe.name(),
                recipe.instructions(),
                recipe.duration()
        );
    }

    static RecipeIngredientDbEntity map(RecipeIngredientForSynchronisation recipeIngredient) {
        return RecipeIngredientDbEntity.create(
                recipeIngredient.id(),
                recipeIngredient.version(),
                recipeIngredient.validTimeStart(),
                recipeIngredient.validTimeEnd(),
                recipeIngredient.transactionTimeStart(),
                recipeIngredient.transactionTimeEnd(),
                recipeIngredient.initiates(),
                recipeIngredient.amount(),
                recipeIngredient.ingredient(),
                recipeIngredient.unit(),
                recipeIngredient.recipe()
        );
    }

    static RecipeProductDbEntity map(RecipeProductForSynchronisation recipeProduct) {
        return RecipeProductDbEntity.create(
                recipeProduct.id(),
                recipeProduct.version(),
                recipeProduct.validTimeStart(),
                recipeProduct.validTimeEnd(),
                recipeProduct.transactionTimeStart(),
                recipeProduct.transactionTimeEnd(),
                recipeProduct.initiates(),
                recipeProduct.amount(),
                recipeProduct.product(),
                recipeProduct.unit(),
                recipeProduct.recipe()
        );
    }

    static LocationForListing map(LocationDbEntity input) {
        return LocationForListing.create(input.id(), input.name());
    }

    static LocationForDeletion mapForDeletion(LocationDbEntity input) {
        return LocationForDeletion.builder()
                .id(input.id())
                .version(input.version())
                .build();
    }

    public static LocationAddForm map(LocationAddEntity input) {
        return LocationAddForm.create(input.name(), input.description());
    }

    public static LocationAddEntity map(LocationAddForm input) {
        return LocationAddEntity.create(input.name(), input.description());
    }

    public static LocationEditFormData mapToEdit(LocationDbEntity location) {
        return LocationEditFormData.create(
                IdImpl.create(location.id()),
                location.version(),
                location.name(),
                location.description());
    }

    public static UnitForListing map(UnitDbEntity unitDbEntity) {
        return UnitForListing.create(unitDbEntity.id(), unitDbEntity.name(), unitDbEntity.abbreviation());
    }
}
