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

package de.njsm.stocks.server.v2.business.data.visitor;

import de.njsm.stocks.common.api.*;
import de.njsm.stocks.common.api.visitor.InsertableVisitor;
import de.njsm.stocks.server.util.Principals;
import org.jooq.InsertOnDuplicateStep;
import org.jooq.InsertSetStep;
import org.jooq.TableRecord;
import org.jooq.impl.DSL;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static de.njsm.stocks.server.v2.db.jooq.Tables.*;
import static de.njsm.stocks.server.v2.db.jooq.Tables.EAN_NUMBER;
import static de.njsm.stocks.server.v2.db.jooq.tables.User.USER;
import static de.njsm.stocks.server.v2.db.jooq.tables.UserDevice.USER_DEVICE;

public class JooqInsertionVisitor<R extends TableRecord<R>> implements InsertableVisitor<JooqInsertionVisitor.Input<R>, InsertOnDuplicateStep<R>> {


    @Override
    public InsertOnDuplicateStep<R> eanNumberForInsertion(EanNumberForInsertion eanNumberForInsertion, Input<R> input) {
        return input.getInsertSetStep().columns(EAN_NUMBER.NUMBER, EAN_NUMBER.IDENTIFIES, EAN_NUMBER.INITIATES)
                .values(eanNumberForInsertion.eanNumber(), eanNumberForInsertion.identifiesFood(), input.getPrincipals().getDid());
    }

    @Override
    public InsertOnDuplicateStep<R> foodForInsertion(FoodForInsertion foodForInsertion, Input<R> input) {
        if (foodForInsertion.storeUnit().isEmpty())
            return input.getInsertSetStep().columns(FOOD.NAME, FOOD.INITIATES, FOOD.STORE_UNIT)
                    .select(DSL.select(DSL.inline(foodForInsertion.name()), DSL.inline(input.getPrincipals().getDid()), DSL.min(SCALED_UNIT.ID))
                            .from(SCALED_UNIT));

        else
            return input.getInsertSetStep().columns(FOOD.NAME, FOOD.INITIATES, FOOD.STORE_UNIT)
                    .values(foodForInsertion.name(), input.getPrincipals().getDid(), foodForInsertion.storeUnit().get());
    }

    @Override
    public InsertOnDuplicateStep<R> foodItemForInsertion(FoodItemForInsertion foodItemForInsertion, Input<R> input) {
        if (foodItemForInsertion.unit().isEmpty())
            return input.getInsertSetStep().columns(FOOD_ITEM.EAT_BY,
                            FOOD_ITEM.STORED_IN,
                            FOOD_ITEM.OF_TYPE,
                            FOOD_ITEM.REGISTERS,
                            FOOD_ITEM.BUYS,
                            FOOD_ITEM.INITIATES,
                            FOOD_ITEM.UNIT)
                    .select(DSL.select(
                                    DSL.inline(OffsetDateTime.from(foodItemForInsertion.eatByDate().atOffset(ZoneOffset.UTC))),
                                    DSL.inline(foodItemForInsertion.storedIn()),
                                    DSL.inline(foodItemForInsertion.ofType()),
                                    DSL.inline(foodItemForInsertion.registers()),
                                    DSL.inline(foodItemForInsertion.buys()),
                                    DSL.inline(input.getPrincipals().getDid()),
                                    DSL.min(SCALED_UNIT.ID))
                            .from(SCALED_UNIT));
        else
            return input.getInsertSetStep().columns(FOOD_ITEM.EAT_BY,
                            FOOD_ITEM.STORED_IN,
                            FOOD_ITEM.OF_TYPE,
                            FOOD_ITEM.REGISTERS,
                            FOOD_ITEM.BUYS,
                            FOOD_ITEM.INITIATES,
                            FOOD_ITEM.UNIT)
                    .values(OffsetDateTime.from(foodItemForInsertion.eatByDate().atOffset(ZoneOffset.UTC)),
                            foodItemForInsertion.storedIn(),
                            foodItemForInsertion.ofType(),
                            foodItemForInsertion.registers(),
                            foodItemForInsertion.buys(),
                            input.getPrincipals().getDid(),
                            foodItemForInsertion.unit().get());
    }

    @Override
    public InsertOnDuplicateStep<R> locationForInsertion(LocationForInsertion locationForInsertion, Input<R> input) {
        return input.getInsertSetStep().columns(LOCATION.NAME, LOCATION.INITIATES)
                .values(locationForInsertion.getName(), input.getPrincipals().getDid());
    }

    @Override
    public InsertOnDuplicateStep<R> recipeForInsertion(RecipeForInsertion recipeForInsertion, Input<R> input) {
        return input.getInsertSetStep().columns(RECIPE.NAME, RECIPE.INSTRUCTIONS, RECIPE.DURATION, RECIPE.INITIATES)
                .values(recipeForInsertion.name(), recipeForInsertion.instructions(), recipeForInsertion.duration(), input.getPrincipals().getDid());
    }

    @Override
    public InsertOnDuplicateStep<R> scaledUnitForInsertion(ScaledUnitForInsertion scaledUnitForInsertion, Input<R> input) {
        return input.getInsertSetStep().columns(SCALED_UNIT.SCALE, SCALED_UNIT.UNIT, SCALED_UNIT.INITIATES)
                .values(scaledUnitForInsertion.getScale(), scaledUnitForInsertion.getUnit(), input.getPrincipals().getDid());
    }

    @Override
    public InsertOnDuplicateStep<R> unitForInsertion(UnitForInsertion unitForInsertion, Input<R> input) {
        return input.getInsertSetStep().columns(UNIT.NAME, UNIT.ABBREVIATION, UNIT.INITIATES)
                .values(unitForInsertion.getName(), unitForInsertion.getAbbreviation(), input.getPrincipals().getDid());
    }

    @Override
    public InsertOnDuplicateStep<R> userDeviceForInsertion(UserDeviceForInsertion userDeviceForInsertion, Input<R> input) {
        return input.getInsertSetStep().columns(USER_DEVICE.NAME, USER_DEVICE.BELONGS_TO, USER_DEVICE.INITIATES)
                .values(userDeviceForInsertion.getName(), userDeviceForInsertion.getBelongsTo(), input.getPrincipals().getDid());
    }

    @Override
    public InsertOnDuplicateStep<R> userForInsertion(UserForInsertion userForInsertion, Input<R> input) {
        return input.getInsertSetStep().columns(USER.NAME, USER.INITIATES)
                .values(userForInsertion.getName(), input.getPrincipals().getDid());
    }

    @Override
    public InsertOnDuplicateStep<R> recipeIngredientWithIdForInsertion(RecipeIngredientWithIdForInsertion recipeIngredientWithIdForInsertion, Input<R> input) {
        return input.getInsertSetStep().columns(RECIPE_INGREDIENT.AMOUNT, RECIPE_INGREDIENT.INGREDIENT, RECIPE_INGREDIENT.RECIPE, RECIPE_INGREDIENT.UNIT, RECIPE_INGREDIENT.INITIATES)
                .values(recipeIngredientWithIdForInsertion.amount(), recipeIngredientWithIdForInsertion.ingredient(), recipeIngredientWithIdForInsertion.recipe(), recipeIngredientWithIdForInsertion.unit(), input.getPrincipals().getDid());
    }

    @Override
    public InsertOnDuplicateStep<R> recipeProductWithIdForInsertion(RecipeProductWithIdForInsertion recipeProductWithIdForInsertion, Input<R> input) {
        return input.getInsertSetStep().columns(RECIPE_PRODUCT.AMOUNT, RECIPE_PRODUCT.PRODUCT, RECIPE_PRODUCT.RECIPE, RECIPE_PRODUCT.UNIT, RECIPE_PRODUCT.INITIATES)
                .values(recipeProductWithIdForInsertion.amount(), recipeProductWithIdForInsertion.product(), recipeProductWithIdForInsertion.recipe(), recipeProductWithIdForInsertion.unit(), input.getPrincipals().getDid());
    }

    public static class Input<R extends TableRecord<R>> {

        private final InsertSetStep<R> insertSetStep;

        private final Principals principals;

        public Input(InsertSetStep<R> insertSetStep, Principals principals) {
            this.insertSetStep = insertSetStep;
            this.principals = principals;
        }

        public InsertSetStep<R> getInsertSetStep() {
            return insertSetStep;
        }

        public Principals getPrincipals() {
            return principals;
        }
    }
}
