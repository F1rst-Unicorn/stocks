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

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class FoodToBuy implements Id<Food>, ShoppingFlagModifying {

    public abstract boolean toBuy();

    @Override
    public boolean needsAction(boolean currentState) {
        return toBuy() != currentState;
    }

    public static FoodToBuy create(int food, boolean toBuy) {
        return new AutoValue_FoodToBuy(food, toBuy);
    }

    public static FoodToBuy putOnShoppingList(int food) {
        return create(food, true);
    }

    public static FoodToBuy removeFromShoppingList(int food) {
        return create(food, false);
    }
}
