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

package de.njsm.stocks.client.network;

import de.njsm.stocks.client.business.entities.Bitemporal;
import de.njsm.stocks.client.business.entities.StatusCode;
import de.njsm.stocks.client.business.entities.Update;
import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.common.api.Entity;
import de.njsm.stocks.common.api.LocationForEditing;
import de.njsm.stocks.common.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class DataMapper {

    private static final Logger LOG = LoggerFactory.getLogger(DataMapper.class);

    static Optional<Update> map(de.njsm.stocks.common.api.Update update) {
        return map(update.table())
                .map(v -> Update.create(v, update.lastUpdate()));
    }

    public static LocationForSynchronisation map(BitemporalLocation source) {
        return map(source, LocationForSynchronisation.builder())
                .name(source.name())
                .description(source.description())
                .build();
    }

    public static UserForSynchronisation map(BitemporalUser source) {
        return map(source, UserForSynchronisation.builder())
                .name(source.name())
                .build();
    }

    public static UserDeviceForSynchronisation map(BitemporalUserDevice source) {
        return map(source, UserDeviceForSynchronisation.builder())
                .name(source.name())
                .belongsTo(source.belongsTo())
                .build();
    }

    public static FoodForSynchronisation map(BitemporalFood source) {
        return map(source, FoodForSynchronisation.builder())
                .name(source.name())
                .toBuy(source.toBuy())
                .expirationOffset(source.expirationOffset())
                .location(source.location())
                .storeUnit(source.storeUnit())
                .description(source.description())
                .build();
    }

    public static EanNumberForSynchronisation map(BitemporalEanNumber source) {
        return map(source, EanNumberForSynchronisation.builder())
                .number(source.eanNumber())
                .identifies(source.identifiesFood())
                .build();
    }

    public static FoodItemForSynchronisation map(BitemporalFoodItem source) {
        return map(source, FoodItemForSynchronisation.builder())
                .eatBy(source.eatByDate())
                .ofType(source.ofType())
                .storedIn(source.storedIn())
                .buys(source.buys())
                .registers(source.registers())
                .unit(source.unit())
                .build();
    }

    public static UnitForSynchronisation map(BitemporalUnit source) {
        return map(source, UnitForSynchronisation.builder())
                .name(source.name())
                .abbreviation(source.abbreviation())
                .build();
    }

    public static ScaledUnitForSynchronisation map(BitemporalScaledUnit source) {
        return map(source, ScaledUnitForSynchronisation.builder())
                .scale(source.scale())
                .unit(source.unit())
                .build();
    }

    public static RecipeForSynchronisation map(BitemporalRecipe source) {
        return map(source, RecipeForSynchronisation.builder())
                .name(source.name())
                .instructions(source.instructions())
                .duration(source.duration())
                .build();
    }

    public static RecipeIngredientForSynchronisation map(BitemporalRecipeIngredient source) {
        return map(source, RecipeIngredientForSynchronisation.builder())
                .amount(source.amount())
                .ingredient(source.ingredient())
                .recipe(source.recipe())
                .unit(source.unit())
                .build();
    }

    public static RecipeProductForSynchronisation map(BitemporalRecipeProduct source) {
        return map(source, RecipeProductForSynchronisation.builder())
                .amount(source.amount())
                .product(source.product())
                .recipe(source.recipe())
                .unit(source.unit())
                .build();
    }

    private static <T extends Bitemporal.Builder<T>, E extends Entity<E>> T map(de.njsm.stocks.common.api.Bitemporal<E> source, T destination) {
        return destination
                .id(source.id())
                .version(source.version())
                .validTimeStart(source.validTimeStart())
                .validTimeEnd(source.validTimeEnd())
                .transactionTimeStart(source.transactionTimeStart())
                .transactionTimeEnd(source.transactionTimeEnd())
                .initiates(source.initiates());
    }

    static Optional<EntityType> map(String entityType) {
        if (entityType.equalsIgnoreCase("location")) {
            return Optional.of(EntityType.LOCATION);
        } else if (entityType.equalsIgnoreCase("user")) {
            return Optional.of(EntityType.USER);
        } else if (entityType.equalsIgnoreCase("user_device")) {
            return Optional.of(EntityType.USER_DEVICE);
        } else if (entityType.equalsIgnoreCase("food")) {
            return Optional.of(EntityType.FOOD);
        } else if (entityType.equalsIgnoreCase("ean_number")) {
            return Optional.of(EntityType.EAN_NUMBER);
        } else if (entityType.equalsIgnoreCase("food_item")) {
            return Optional.of(EntityType.FOOD_ITEM);
        } else if (entityType.equalsIgnoreCase("unit")) {
            return Optional.of(EntityType.UNIT);
        } else if (entityType.equalsIgnoreCase("scaled_unit")) {
            return Optional.of(EntityType.SCALED_UNIT);
        } else if (entityType.equalsIgnoreCase("recipe")) {
            return Optional.of(EntityType.RECIPE);
        } else if (entityType.equalsIgnoreCase("recipe_ingredient")) {
            return Optional.of(EntityType.RECIPE_INGREDIENT);
        } else if (entityType.equalsIgnoreCase("recipe_product")) {
            return Optional.of(EntityType.RECIPE_PRODUCT);
        }

        LOG.info("unknown entity type '" + entityType + "'");
        return Optional.empty();
    }

    static StatusCode map(de.njsm.stocks.common.api.StatusCode input) {
        int ordinal = input.ordinal();
        if (ordinal < StatusCode.values().length)
            return StatusCode.values()[ordinal];
        LOG.warn("Unknown status code " + input + " mapped to " + StatusCode.GENERAL_ERROR);
        return StatusCode.GENERAL_ERROR;
    }

    static LocationForEditing map(de.njsm.stocks.client.business.entities.LocationForEditing location) {
        return LocationForEditing.builder()
                .id(location.id())
                .version(location.version())
                .name(location.name())
                .description(location.description())
                .build();
    }
}
