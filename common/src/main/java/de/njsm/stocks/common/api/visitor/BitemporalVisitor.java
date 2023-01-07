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

package de.njsm.stocks.common.api.visitor;

import de.njsm.stocks.common.api.*;

public interface BitemporalVisitor<I, O> {

    default <T extends Entity<T>> O visit(Bitemporal<T> bitemporal, I argument) {
        return bitemporal.accept(this, argument);
    }

    O bitemporalEanNumber(BitemporalEanNumber bitemporalEanNumber, I argument);

    O bitemporalFood(BitemporalFood bitemporalFood, I argument);

    O bitemporalFoodItem(BitemporalFoodItem bitemporalFoodItem, I argument);

    O bitemporalLocation(BitemporalLocation bitemporalLocation, I argument);

    O bitemporalRecipe(BitemporalRecipe bitemporalRecipe, I argument);

    O bitemporalScaledUnit(BitemporalScaledUnit bitemporalScaledUnit, I argument);

    O bitemporalUnit(BitemporalUnit bitemporalUnit, I argument);

    O bitemporalUserDevice(BitemporalUserDevice bitemporalUserDevice, I argument);

    O bitemporalUser(BitemporalUser bitemporalUser, I argument);

    O bitemporalRecipeIngredient(BitemporalRecipeIngredient bitemporalRecipeIngredient, I argument);

    O bitemporalRecipeProduct(BitemporalRecipeProduct bitemporalRecipeProduct, I argument);
}
