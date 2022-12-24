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

package de.njsm.stocks.client.business.entities.event;

public interface Visitor<I, O> {

    default O visit(ActivityEvent event, I input) {
        return event.accept(this, input);
    }

    O userCreated(UserCreatedEvent userCreatedEvent, I input);

    O userDeleted(UserDeletedEvent userDeletedEvent, I input);

    O userDeviceCreated(UserDeviceCreatedEvent userDeviceCreatedEvent, I input);

    O userDeviceDeleted(UserDeviceDeletedEvent userDeviceDeletedEvent, I input);

    O locationCreated(LocationCreatedEvent locationCreatedEvent, I input);

    O locationDeleted(LocationDeletedEvent locationDeletedEvent, I input);

    O locationEdited(LocationEditedEvent locationEditedEvent, I input);

    O foodCreated(FoodCreatedEvent foodCreatedEvent, I input);

    O foodDeletedEvent(FoodDeletedEvent foodDeletedEvent, I input);

    O foodEditedEvent(FoodEditedEvent foodEditedEvent, I input);

    O foodItemCreated(FoodItemCreatedEvent foodItemCreatedEvent, I input);

    O foodItemDeleted(FoodItemDeletedEvent foodItemDeletedEvent, I input);

    O foodItemEdited(FoodItemEditedEvent foodItemEditedEvent, I input);

    O scaledUnitCreated(ScaledUnitCreatedEvent scaledUnitCreatedEvent, I input);

    O scaledUnitDeleted(ScaledUnitDeletedEvent scaledUnitDeletedEvent, I input);

    O scaledUnitEdited(ScaledUnitEditedEvent scaledUnitEditedEvent, I input);

    O unitCreated(UnitCreatedEvent unitCreatedEvent, I input);

    O unitDeleted(UnitDeletedEvent unitDeletedEvent, I input);

    O unitEdited(UnitEditedEvent unitEditedEvent, I input);

    O eanNumberCreated(EanNumberCreatedEvent eanNumberCreatedEvent, I input);

    O eanNumberDeleted(EanNumberDeletedEvent eanNumberDeletedEvent, I input);
}
