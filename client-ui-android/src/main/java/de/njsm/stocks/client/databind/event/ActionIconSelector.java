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

package de.njsm.stocks.client.databind.event;

import androidx.annotation.DrawableRes;
import de.njsm.stocks.client.business.entities.event.*;
import de.njsm.stocks.client.ui.R;

public class ActionIconSelector implements Visitor<Void, Integer> {

    @DrawableRes
    int visit(ActivityEvent event) {
        return visit(event, null);
    }

    @Override
    public Integer userCreated(UserCreatedEvent userCreatedEvent, Void input) {
        return R.drawable.baseline_add_black_24;
    }

    @Override
    public Integer userDeleted(UserDeletedEvent userDeletedEvent, Void input) {
        return R.drawable.baseline_delete_black_24;
    }

    @Override
    public Integer userDeviceCreated(UserDeviceCreatedEvent userDeviceCreatedEvent, Void input) {
        return R.drawable.baseline_add_black_24;
    }

    @Override
    public Integer userDeviceDeleted(UserDeviceDeletedEvent userDeviceDeletedEvent, Void input) {
        return R.drawable.baseline_delete_black_24;
    }

    @Override
    public Integer locationCreated(LocationCreatedEvent locationCreatedEvent, Void input) {
        return R.drawable.baseline_add_black_24;
    }

    @Override
    public Integer locationDeleted(LocationDeletedEvent locationDeletedEvent, Void input) {
        return R.drawable.baseline_delete_black_24;
    }

    @Override
    public Integer locationEdited(LocationEditedEvent locationEditedEvent, Void input) {
        return R.drawable.ic_create_black_24;
    }

    @Override
    public Integer foodCreated(FoodCreatedEvent foodCreatedEvent, Void input) {
        return R.drawable.baseline_add_black_24;
    }

    @Override
    public Integer foodDeletedEvent(FoodDeletedEvent foodDeletedEvent, Void input) {
        return R.drawable.baseline_delete_black_24;
    }

    @Override
    public Integer foodEditedEvent(FoodEditedEvent foodEditedEvent, Void input) {
        if (foodEditedEvent.toBuy().changed() &&
                foodEditedEvent.name().unchanged() &&
                foodEditedEvent.expirationOffset().unchanged() &&
                foodEditedEvent.unit().unchanged() &&
                foodEditedEvent.locationName().unchanged() &&
                foodEditedEvent.description().unchanged()) {

            if (foodEditedEvent.toBuy().current()) {
                return R.drawable.baseline_add_shopping_cart_black_24;
            } else {
                return R.drawable.baseline_remove_shopping_cart_black_24;
            }
        } else {
            return R.drawable.ic_create_black_24;
        }
    }

    @Override
    public Integer foodItemCreated(FoodItemCreatedEvent foodItemCreatedEvent, Void input) {
        return R.drawable.baseline_add_black_24;
    }

    @Override
    public Integer foodItemDeleted(FoodItemDeletedEvent foodItemDeletedEvent, Void input) {
        return R.drawable.baseline_delete_black_24;
    }

    @Override
    public Integer foodItemEdited(FoodItemEditedEvent foodItemEditedEvent, Void input) {
        return R.drawable.ic_create_black_24;
    }

    @Override
    public Integer scaledUnitCreated(ScaledUnitCreatedEvent scaledUnitCreatedEvent, Void input) {
        return R.drawable.baseline_add_black_24;
    }

    @Override
    public Integer scaledUnitDeleted(ScaledUnitDeletedEvent scaledUnitDeletedEvent, Void input) {
        return R.drawable.baseline_delete_black_24;
    }

    @Override
    public Integer scaledUnitEdited(ScaledUnitEditedEvent scaledUnitEditedEvent, Void input) {
        return R.drawable.ic_create_black_24;
    }

    @Override
    public Integer unitCreated(UnitCreatedEvent unitCreatedEvent, Void input) {
        return R.drawable.baseline_add_black_24;
    }

    @Override
    public Integer unitDeleted(UnitDeletedEvent unitDeletedEvent, Void input) {
        return R.drawable.baseline_delete_black_24;
    }

    @Override
    public Integer unitEdited(UnitEditedEvent unitEditedEvent, Void input) {
        return R.drawable.ic_create_black_24;
    }

    @Override
    public Integer eanNumberCreated(EanNumberCreatedEvent eanNumberCreatedEvent, Void input) {
        return R.drawable.baseline_add_black_24;
    }

    @Override
    public Integer eanNumberDeleted(EanNumberDeletedEvent eanNumberDeletedEvent, Void input) {
        return R.drawable.baseline_delete_black_24;
    }
}
