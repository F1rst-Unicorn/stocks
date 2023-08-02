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

import de.njsm.stocks.client.business.ErrorRepository;
import de.njsm.stocks.client.business.Localiser;
import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.database.*;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

import static de.njsm.stocks.client.database.DataMapper.map;
import static java.util.stream.Collectors.toList;

public class ErrorRepositoryImpl implements ErrorRepository, ErrorEntity.ActionVisitor<Long, ErrorDetails> {

    private final ErrorDao errorDao;

    private final Localiser localiser;

    @Inject
    ErrorRepositoryImpl(ErrorDao errorDao, Localiser localiser) {
        this.errorDao = errorDao;
        this.localiser = localiser;
    }

    @Override
    public Observable<Integer> getNumberOfErrors() {
        return errorDao.getNumberOfErrors();
    }

    @Override
    public void deleteError(ErrorDescription input) {
        ErrorEntity error = errorDao.getError(input.id());
        errorDao.deleteError(input.id());
        new ExceptionDeleter(errorDao).visit(error.exceptionType(), error.exceptionId());
        new DataDeleter(errorDao).visit(error.action(), error.dataId());
    }

    @Override
    public Observable<List<ErrorDescription>> getErrors() {
        return errorDao.observeErrors()
                .distinctUntilChanged()
                .map(v -> v.stream().map(this::resolveData).collect(toList()));
    }

    @Override
    public Observable<ErrorDescription> getError(long id) {
        return errorDao.observeError(id).map(this::resolveData);
    }

    private ErrorDescription resolveData(ErrorEntity errorEntity) {
        ErrorDetails errorDetails = visit(errorEntity.action(), errorEntity.dataId());
        StatusCode statusCode = new ExceptionStatusCodeLoader(errorDao).visit(errorEntity.exceptionType(), errorEntity.exceptionId());
        SubsystemExceptionEntityFields textFields = new ExceptionTextLoader(errorDao).visit(errorEntity.exceptionType(), errorEntity.exceptionId());

        return ErrorDescription.create(
                errorEntity.id(),
                statusCode,
                textFields.stacktrace(),
                textFields.message(),
                errorDetails);
    }

    @Override
    public ErrorDetails synchronisation(ErrorEntity.Action action, Long input) {
        return SynchronisationErrorDetails.create();
    }

    @Override
    public ErrorDetails addLocation(ErrorEntity.Action action, Long input) {
        return map(errorDao.getLocationAdd(input));
    }

    @Override
    public ErrorDetails deleteLocation(ErrorEntity.Action action, Long input) {
        LocationDeleteEntity locationDeleteEntity = errorDao.getLocationDelete(input);
        LocationDbEntity location = errorDao.getLocationByValidOrTransactionTime(locationDeleteEntity.location());
        return LocationDeleteErrorDetails.create(location.id(), location.name());
    }

    @Override
    public ErrorDetails editLocation(ErrorEntity.Action action, Long input) {
        LocationEditEntity locationEditEntity = errorDao.getLocationEdit(input);
        return LocationEditErrorDetails.create(
                IdImpl.create(locationEditEntity.location().id()),
                locationEditEntity.version(),
                locationEditEntity.name(),
                locationEditEntity.description());
    }

    @Override
    public ErrorDetails addUnit(ErrorEntity.Action action, Long input) {
        UnitAddEntity data = errorDao.getUnitAdd(input);
        return UnitAddForm.create(data.name(), data.abbreviation());
    }

    @Override
    public ErrorDetails deleteUnit(ErrorEntity.Action action, Long input) {
        UnitDeleteEntity unitDeleteEntity = errorDao.getUnitDelete(input);
        UnitDbEntity unit = errorDao.getUnitByValidOrTransactionTime(unitDeleteEntity.unit());
        return UnitDeleteErrorDetails.create(unit.id(), unit.name(), unit.abbreviation());
    }

    @Override
    public ErrorDetails editUnit(ErrorEntity.Action action, Long input) {
        UnitEditEntity unitEditEntity = errorDao.getUnitEdit(input);
        return UnitEditErrorDetails.create(unitEditEntity.unit().id(), unitEditEntity.name(), unitEditEntity.abbreviation());
    }

    @Override
    public ErrorDetails addScaledUnit(ErrorEntity.Action action, Long input) {
        ScaledUnitAddEntity scaledUnitAddEntity = errorDao.getScaledUnitAdd(input);
        UnitDbEntity unit = errorDao.getUnitByValidOrTransactionTime(scaledUnitAddEntity.unit());
        return ScaledUnitAddErrorDetails.create(scaledUnitAddEntity.scale(), scaledUnitAddEntity.unit().id(), unit.name(), unit.abbreviation());
    }

    @Override
    public ErrorDetails editScaledUnit(ErrorEntity.Action action, Long input) {
        ScaledUnitEditEntity entity = errorDao.getScaledUnitEdit(input);
        UnitDbEntity unit = errorDao.getUnitByValidOrTransactionTime(entity.unit());
        return ScaledUnitEditErrorDetails.create(entity.scaledUnit().id(), entity.scale(), unit.id(), unit.name(), unit.abbreviation());
    }

    @Override
    public ErrorDetails deleteScaledUnit(ErrorEntity.Action action, Long input) {
        ScaledUnitDeleteEntity entity = errorDao.getScaledUnitDelete(input);
        ScaledUnitDbEntity scaledUnit = errorDao.getScaledUnitByValidOrTransactionTime(entity.scaledUnit());
        UnitDbEntity unit = errorDao.getUnitByValidOrTransactionTime(PreservedId.create(scaledUnit.unit(), entity.scaledUnit().transactionTime()));
        return ScaledUnitDeleteErrorDetails.create(scaledUnit.id(), scaledUnit.scale(), unit.name(), unit.abbreviation());
    }

    @Override
    public ErrorDetails addFood(ErrorEntity.Action action, Long input) {
        FoodAddEntity entity = errorDao.getFoodAdd(input);
        Optional<LocationDbEntity> location = entity.location().maybe().map(errorDao::getLocationByValidOrTransactionTime);
        ScaledUnitDbEntity scaledUnit = errorDao.getScaledUnitByValidOrTransactionTime(entity.storeUnit());
        UnitDbEntity unit = errorDao.getUnitByValidOrTransactionTime(PreservedId.create(scaledUnit.unit(), entity.storeUnit().transactionTime()));
        return FoodAddErrorDetails.create(
                entity.name(),
                entity.toBuy(),
                entity.expirationOffset(),
                entity.location().id(),
                entity.storeUnit().id(),
                entity.description(),
                location.map(LocationDbEntity::name).orElse(""),
                FoodAddErrorDetails.StoreUnit.create(scaledUnit.scale(), unit.abbreviation()));
    }

    @Override
    public ErrorDetails deleteFood(ErrorEntity.Action action, Long input) {
        FoodDeleteEntity entity = errorDao.getFoodDelete(input);
        FoodDbEntity food = errorDao.getFoodByValidOrTransactionTime(entity.food());
        return FoodDeleteErrorDetails.create(food.id(), food.name());
    }

    @Override
    public ErrorDetails editFood(ErrorEntity.Action action, Long input) {
        FoodEditEntity entity = errorDao.getFoodEdit(input);
        return FoodEditErrorDetails.create(entity.food().id(), entity.name(), entity.toBuy(), entity.expirationOffset(), entity.location().maybe().map(PreservedId::id), entity.storeUnit().id(), entity.description());
    }

    @Override
    public ErrorDetails addFoodItem(ErrorEntity.Action action, Long input) {
        FoodItemAddEntity entity = errorDao.getFoodItemAdd(input);
        FoodDbEntity food = errorDao.getFoodByValidOrTransactionTime(entity.ofType());
        LocationDbEntity location = errorDao.getLocationByValidOrTransactionTime(entity.storedIn());
        ScaledUnitDbEntity scaledUnit = errorDao.getScaledUnitByValidOrTransactionTime(entity.unit());
        UnitDbEntity unit = errorDao.getUnitByValidOrTransactionTime(PreservedId.create(scaledUnit.unit(), entity.unit().transactionTime()));
        return FoodItemAddErrorDetails.create(
                localiser.toLocalDate(entity.eatBy()),
                entity.ofType().id(),
                entity.storedIn().id(),
                scaledUnit.id(),
                FoodItemAddErrorDetails.Unit.create(scaledUnit.scale(), unit.abbreviation()),
                food.name(),
                location.name()
        );
    }

    @Override
    public ErrorDetails deleteFoodItem(ErrorEntity.Action action, Long input) {
        FoodItemDeleteEntity entity = errorDao.getFoodItemDelete(input);
        FoodItemDbEntity foodItem = errorDao.getFoodItemByValidOrTransactionTime(entity.foodItem());
        FoodDbEntity food = errorDao.getFoodByValidOrTransactionTime(PreservedId.create(foodItem.ofType(), entity.foodItem().transactionTime()));
        ScaledUnitDbEntity scaledUnit = errorDao.getScaledUnitByValidOrTransactionTime(PreservedId.create(foodItem.unit(), entity.foodItem().transactionTime()));
        UnitDbEntity unit = errorDao.getUnitByValidOrTransactionTime(PreservedId.create(scaledUnit.unit(), entity.foodItem().transactionTime()));
        return FoodItemDeleteErrorDetails.create(
                foodItem.id(),
                food.name(),
                FoodItemDeleteErrorDetails.Unit.create(scaledUnit.scale(), unit.abbreviation())
        );
    }

    @Override
    public ErrorDetails editFoodItem(ErrorEntity.Action action, Long input) {
        FoodItemEditEntity entity = errorDao.getFoodItemEdit(input);
        FoodItemDbEntity foodItem = errorDao.getFoodItemByValidOrTransactionTime(entity.foodItem());
        FoodDbEntity food = errorDao.getFoodByValidOrTransactionTime(PreservedId.create(foodItem.ofType(), entity.foodItem().transactionTime()));
        return FoodItemEditErrorDetails.create(entity.foodItem().id(), food.name(), localiser.toLocalDate(entity.eatBy()), entity.storedIn().id(), entity.unit().id());
    }

    @Override
    public ErrorDetails addEanNumber(ErrorEntity.Action action, Long input) {
        EanNumberAddEntity eanNumberAddEntity = errorDao.getEanNumberAdd(input);
        var food = errorDao.getFoodByValidOrTransactionTime(eanNumberAddEntity.identifies());
        return EanNumberAddErrorDetails.create(food.id(), food.name(), eanNumberAddEntity.eanNumber());
    }

    @Override
    public ErrorDetails deleteEanNumber(ErrorEntity.Action action, Long input) {
        EanNumberDeleteEntity entity = errorDao.getEanNumberDelete(input);
        EanNumberDbEntity eanNumber = errorDao.getEanNumberByValidOrTransactionTime(entity.eanNumber());
        FoodDbEntity food = errorDao.getFoodByValidOrTransactionTime(PreservedId.create(eanNumber.identifies(), entity.eanNumber().transactionTime()));
        return EanNumberDeleteErrorDetails.create(
                eanNumber.id(),
                food.name(),
                eanNumber.number()
        );
    }

    @Override
    public ErrorDetails deleteUserDevice(ErrorEntity.Action action, Long input) {
        UserDeviceDeleteEntity entity = errorDao.getUserDeviceDelete(input);
        UserDeviceDbEntity userDevice = errorDao.getUserDeviceByValidOrTransactionTime(entity.userDevice());
        UserDbEntity user = errorDao.getUserByValidOrTransactionTime(PreservedId.create(userDevice.belongsTo(), entity.userDevice().transactionTime()));
        return UserDeviceDeleteErrorDetails.create(
                userDevice.id(),
                user.name(),
                userDevice.name()
        );
    }

    @Override
    public ErrorDetails deleteUser(ErrorEntity.Action action, Long input) {
        UserDeleteEntity entity = errorDao.getUserDelete(input);
        UserDbEntity user = errorDao.getUserByValidOrTransactionTime(entity.user());
        return UserDeleteErrorDetails.create(
                user.id(),
                user.name()
        );
    }

    @Override
    public ErrorDetails addRecipe(ErrorEntity.Action action, Long input) {
        RecipeAddEntity recipe = errorDao.getRecipeAdd(input);
        var ingredients = errorDao.getRecipeIngredientAdd(recipe.id());
        var products = errorDao.getRecipeProductAdd(recipe.id());
        return RecipeAddForm.create(recipe.name(), recipe.instructions(), recipe.duration(),
                ingredients.stream()
                        .map(v -> RecipeIngredientToAdd.create(v.amount(), v.ingredient().id(), v.unit().id()))
                        .collect(toList()),
                products.stream()
                        .map(v -> RecipeProductToAdd.create(v.amount(), v.product().id(), v.unit().id()))
                        .collect(toList())
        );
    }

    @Override
    public ErrorDetails foodShopping(ErrorEntity.Action action, Long input) {
        return errorDao.getFoodToBuy(input);
    }

    @Override
    public ErrorDetails addUser(ErrorEntity.Action action, Long input) {
        return errorDao.getUserToAdd(input);
    }

    @Override
    public ErrorDetails addUserDevice(ErrorEntity.Action action, Long input) {
        var userDevice = errorDao.getUserDeviceToAdd(input);
        var user = errorDao.getUserByValidOrTransactionTime(userDevice.belongsTo());
        return UserDeviceAddErrorDetails.create(userDevice.name(), user::id, user.name());
    }

    @Override
    public ErrorDetails deleteRecipe(ErrorEntity.Action action, Long id) {
        var recipeDelete = errorDao.getRecipeDelete(id);
        var recipe = errorDao.getRecipeByValidOrTransactionTime(recipeDelete.recipe());
        return RecipeDeleteErrorDetails.create(recipe.id(), recipe.name());
    }

    @Override
    public ErrorDetails editRecipe(ErrorEntity.Action action, Long input) {
        RecipeEditEntity recipe = errorDao.getRecipeEdit(input);
        var ingredients = errorDao.getRecipeIngredientEdit(recipe.id());
        var products = errorDao.getRecipeProductEdit(recipe.id());
        return RecipeEditForm.create(RecipeEditBaseData.create(recipe.recipe().id(), recipe.name(), recipe.instructions(), recipe.duration()),
                ingredients.stream()
                        .map(v -> RecipeIngredientEditFormData.create(v.recipeIngredient().id(), v.amount(), -1, IdImpl.create(v.unit().id()), -1, IdImpl.create(v.ingredient().id())))
                        .collect(toList()),
                products.stream()
                        .map(v -> RecipeProductEditFormData.create(v.recipeProduct().id(), v.amount(), -1, IdImpl.create(v.unit().id()), -1, IdImpl.create(v.product().id())))
                        .collect(toList())
        );
    }
}
