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

package de.njsm.stocks.client.business.entities;

public enum EntityType {

    LOCATION {
        @Override
        <I, O> O accept(Visitor<I, O> visitor, I input) {
            return visitor.location(input);
        }
    },

    USER {
        @Override
        <I, O> O accept(Visitor<I, O> visitor, I input) {
            return visitor.user(input);
        }
    },
    USER_DEVICE {
        @Override
        <I, O> O accept(Visitor<I, O> visitor, I input) {
            return visitor.userDevice(input);
        }
    },
    FOOD {
        @Override
        <I, O> O accept(Visitor<I, O> visitor, I input) {
            return visitor.food(input);
        }
    },
    EAN_NUMBER {
        @Override
        <I, O> O accept(Visitor<I, O> visitor, I input) {
            return visitor.eanNumber(input);
        }
    },
    FOOD_ITEM {
        @Override
        <I, O> O accept(Visitor<I, O> visitor, I input) {
            return visitor.foodItem(input);
        }
    },
    UNIT {
        @Override
        <I, O> O accept(Visitor<I, O> visitor, I input) {
            return visitor.unit(input);
        }
    },
    SCALED_UNIT {
        @Override
        <I, O> O accept(Visitor<I, O> visitor, I input) {
            return visitor.scaledUnit(input);
        }
    },
    RECIPE {
        @Override
        <I, O> O accept(Visitor<I, O> visitor, I input) {
            return visitor.recipe(input);
        }
    },
    RECIPE_INGREDIENT {
        @Override
        <I, O> O accept(Visitor<I, O> visitor, I input) {
            return visitor.recipeIngredient(input);
        }
    },
    RECIPE_PRODUCT {
        @Override
        <I, O> O accept(Visitor<I, O> visitor, I input) {
            return visitor.recipeProduct(input);
        }
    };

    abstract <I, O> O accept(Visitor<I, O> visitor, I input);

    public interface Visitor<I, O> {

        default O visit(EntityType item, I input) {
            return item.accept(this, input);
        }

        O location(I input);

        O user(I input);

        O userDevice(I input);

        O food(I input);

        O eanNumber(I input);

        O foodItem(I input);

        O unit(I input);

        O scaledUnit(I input);

        O recipe(I input);

        O recipeIngredient(I input);

        O recipeProduct(I input);
    }
}
