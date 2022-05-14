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

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.Period;
import java.util.Arrays;
import java.util.List;

import static de.njsm.stocks.client.business.Constants.INFINITY;
import static java.time.Instant.EPOCH;

public class StandardEntities {

    public static LocationDbEntity locationDbEntity() {
        return locationDbEntityBuilder().build();
    }

    static LocationDbEntity.Builder locationDbEntityBuilder() {
        return initialiseBuilder(LocationDbEntity.builder())
                .name("name")
                .description("description");
    }

    static UserDbEntity.Builder userDbEntityBuilder() {
        return initialiseBuilder(UserDbEntity.builder())
                .name("name");
    }

    static UserDeviceDbEntity.Builder userDeviceDbEntityBuilder() {
        return initialiseBuilder(UserDeviceDbEntity.builder())
                .name("name")
                .belongsTo(4);
    }

    static FoodDbEntity.Builder foodDbEntityBuilder() {
        return initialiseBuilder(FoodDbEntity.builder())
                .name("name")
                .toBuy(true)
                .expirationOffset(Period.ofDays(4))
                .location(5)
                .storeUnit(6)
                .description("description");
    }

    static FoodItemDbEntity.Builder foodItemDbEntityBuilder() {
        return initialiseBuilder(FoodItemDbEntity.builder())
                .eatBy(EPOCH)
                .ofType(4)
                .storedIn(5)
                .buys(6)
                .registers(7)
                .unit(8);
    }

    public static UnitDbEntity unitDbEntity() {
        return unitDbEntityBuilder().build();
    }

    static UnitDbEntity.Builder unitDbEntityBuilder() {
        return initialiseBuilder(UnitDbEntity.builder())
                .name("name")
                .abbreviation("abbreviation");
    }

    static ScaledUnitDbEntity.Builder scaledUnitDbEntityBuilder() {
        return initialiseBuilder(ScaledUnitDbEntity.builder())
                .scale(BigDecimal.TEN)
                .unit(4);
    }

    static RecipeDbEntity.Builder recipeDbEntityBuilder() {
        return initialiseBuilder(RecipeDbEntity.builder())
                .name("name")
                .instructions("instructions")
                .duration(Duration.ofDays(4));
    }

    static RecipeIngredientDbEntity.Builder recipeIngredientDbEntityBuilder() {
        return initialiseBuilder(RecipeIngredientDbEntity.builder())
                .amount(4)
                .ingredient(5)
                .unit(6)
                .recipe(7);
    }

    static RecipeProductDbEntity.Builder recipeProductDbEntityBuilder() {
        return initialiseBuilder(RecipeProductDbEntity.builder())
                .amount(4)
                .product(5)
                .unit(6)
                .recipe(7);
    }

    static EanNumberDbEntity.Builder eanNumberDbEntityBuilder() {
        return initialiseBuilder(EanNumberDbEntity.builder())
                .number("number")
                .identifies(4);
    }

    private static <E extends ServerDbEntity<E>, T extends ServerDbEntity.Builder<E, T>> T initialiseBuilder(T builder) {
        return builder
                .id(1)
                .version(2)
                .validTimeStart(EPOCH)
                .validTimeEnd(INFINITY)
                .transactionTimeStart(EPOCH)
                .transactionTimeEnd(INFINITY)
                .initiates(3);
    }

    public static <E extends ServerDbEntity<E>, B extends ServerDbEntity.Builder<E, B>>
    List<E> bitemporalEdit(E current,
                           EntityEditor<E, B> editor,
                           Instant when) {
        E deletedCurrent = current.toBuilder()
                .transactionTimeEnd(when)
                .build();
        E terminatedCurrent = current.toBuilder()
                .validTimeEnd(when)
                .transactionTimeStart(when)
                .build();
        E temporaryToSatisfyTypeSystem = current.toBuilder()
                .validTimeStart(when)
                .transactionTimeStart(when)
                .version(current.version() + 1).build();
        E edited = editor.edit(temporaryToSatisfyTypeSystem.toBuilder()).build();

        return Arrays.asList(
                deletedCurrent,
                terminatedCurrent,
                edited
        );
    }

    public interface EntityEditor<E extends ServerDbEntity<E>, B extends ServerDbEntity.Builder<E, B>> {
        B edit(B builder);
    }
}
