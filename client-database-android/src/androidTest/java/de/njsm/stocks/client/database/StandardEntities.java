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

import de.njsm.stocks.client.database.util.RandomnessProvider;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Period;

import static de.njsm.stocks.client.business.Constants.INFINITY;
import static java.time.Instant.EPOCH;

public class StandardEntities {

    private final RandomnessProvider randomnessProvider;

    public StandardEntities(RandomnessProvider randomnessProvider) {
        this.randomnessProvider = randomnessProvider;
    }

    public LocationDbEntity locationDbEntity() {
        return locationDbEntityBuilder().build();
    }

    LocationDbEntity.Builder locationDbEntityBuilder() {
        return initialiseBuilder(LocationDbEntity.builder())
                .name("name")
                .description("description");
    }

    UserDbEntity.Builder userDbEntityBuilder() {
        return initialiseBuilder(UserDbEntity.builder())
                .name("name");
    }

    UserDeviceDbEntity.Builder userDeviceDbEntityBuilder() {
        return initialiseBuilder(UserDeviceDbEntity.builder())
                .name("name")
                .belongsTo(randomnessProvider.getId("user_device belongs_to"));
    }

    public FoodDbEntity foodDbEntity() {
        return foodDbEntityBuilder().build();
    }

    public FoodDbEntity.Builder foodDbEntityBuilder() {
        return initialiseBuilder(FoodDbEntity.builder())
                .name("name")
                .toBuy(true)
                .expirationOffset(Period.ofDays(4))
                .location(randomnessProvider.getId("food location"))
                .storeUnit(6)
                .description("description");
    }

    FoodItemDbEntity.Builder foodItemDbEntityBuilder() {
        return initialiseBuilder(FoodItemDbEntity.builder())
                .eatBy(EPOCH)
                .ofType(randomnessProvider.getId("food_item of_type"))
                .storedIn(randomnessProvider.getId("food_item stored_in"))
                .buys(randomnessProvider.getId("food_item buys"))
                .registers(randomnessProvider.getId("food_item registers"))
                .unit(randomnessProvider.getId("food_item unit"));
    }

    public UnitDbEntity unitDbEntity() {
        return unitDbEntityBuilder().build();
    }

    public UnitDbEntity.Builder unitDbEntityBuilder() {
        return initialiseBuilder(UnitDbEntity.builder())
                .name("name")
                .abbreviation("abbreviation");
    }

    public ScaledUnitDbEntity scaledUnitDbEntity() {
        return scaledUnitDbEntityBuilder().build();
    }

    public ScaledUnitDbEntity.Builder scaledUnitDbEntityBuilder() {
        return initialiseBuilder(ScaledUnitDbEntity.builder())
                .scale(BigDecimal.TEN)
                .unit(randomnessProvider.getId("scaled_unit unit"));
    }

    RecipeDbEntity.Builder recipeDbEntityBuilder() {
        return initialiseBuilder(RecipeDbEntity.builder())
                .name("name")
                .instructions("instructions")
                .duration(Duration.ofDays(4));
    }

    RecipeIngredientDbEntity.Builder recipeIngredientDbEntityBuilder() {
        return initialiseBuilder(RecipeIngredientDbEntity.builder())
                .amount(4)
                .ingredient(randomnessProvider.getId("recipe_ingredient ingredient"))
                .unit(randomnessProvider.getId("recipe_ingredient unit"))
                .recipe(randomnessProvider.getId("recipe_ingredient recipe"));
    }

    RecipeProductDbEntity.Builder recipeProductDbEntityBuilder() {
        return initialiseBuilder(RecipeProductDbEntity.builder())
                .amount(4)
                .product(randomnessProvider.getId("recipe_product product"))
                .unit(randomnessProvider.getId("recipe_ingredient unit"))
                .recipe(randomnessProvider.getId("recipe_ingredient recipe"));
    }

    EanNumberDbEntity.Builder eanNumberDbEntityBuilder() {
        return initialiseBuilder(EanNumberDbEntity.builder())
                .number("number")
                .identifies(randomnessProvider.getId("ean_number identifies"));
    }

    private <E extends ServerDbEntity<E>, T extends ServerDbEntity.Builder<E, T>> T initialiseBuilder(T builder) {
        return builder
                .id(randomnessProvider.getId(builder.getClass().getCanonicalName() + " id"))
                .version(2)
                .validTimeStart(EPOCH)
                .validTimeEnd(INFINITY)
                .transactionTimeStart(EPOCH)
                .transactionTimeEnd(INFINITY)
                .initiates(3);
    }
}
