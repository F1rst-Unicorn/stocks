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

import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.database.error.LocationAddEntity;
import de.njsm.stocks.client.database.error.LocationDeleteEntity;
import de.njsm.stocks.client.database.error.LocationEditEntity;

public class DataMapper {

    static Update map(UpdateDbEntity input) {
        return Update.create(input.table(), input.lastUpdate());
    }

    static UpdateDbEntity map(Update input) {
        return UpdateDbEntity.create(input.table(), input.lastUpdate());
    }

    static LocationDbEntity map(LocationForSynchronisation location) {
        return LocationDbEntity.create(
                location.id(),
                location.version(),
                location.validTimeStart(),
                location.validTimeEnd(),
                location.transactionTimeStart(),
                location.transactionTimeEnd(),
                location.initiates(),
                location.name(),
                location.description()
        );
    }

    static UserDbEntity map(UserForSynchronisation user) {
        return UserDbEntity.create(
                user.id(),
                user.version(),
                user.validTimeStart(),
                user.validTimeEnd(),
                user.transactionTimeStart(),
                user.transactionTimeEnd(),
                user.initiates(),
                user.name()
        );
    }

    static UserDeviceDbEntity map(UserDeviceForSynchronisation userDevice) {
        return UserDeviceDbEntity.create(
                userDevice.id(),
                userDevice.version(),
                userDevice.validTimeStart(),
                userDevice.validTimeEnd(),
                userDevice.transactionTimeStart(),
                userDevice.transactionTimeEnd(),
                userDevice.initiates(),
                userDevice.name(),
                userDevice.belongsTo()
        );
    }

    static FoodDbEntity map(FoodForSynchronisation food) {
        return FoodDbEntity.create(
                food.id(),
                food.version(),
                food.validTimeStart(),
                food.validTimeEnd(),
                food.transactionTimeStart(),
                food.transactionTimeEnd(),
                food.initiates(),
                food.name(),
                food.toBuy(),
                food.expirationOffset(),
                food.location().orElse(null),
                food.storeUnit(),
                food.description()
        );
    }

    static LocationForListing map(LocationDbEntity input) {
        return LocationForListing.create(input.id(), input.name());
    }

    static LocationForDeletion mapForDeletion(LocationDbEntity input) {
        return LocationForDeletion.builder()
                .id(input.id())
                .version(input.version())
                .build();
    }

    public static LocationDeleteEntity map(LocationForDeletion locationForDeletion) {
        return LocationDeleteEntity.create(locationForDeletion.id(), locationForDeletion.version());
    }

    public static LocationAddForm map(LocationAddEntity input) {
        return LocationAddForm.create(input.name(), input.description());
    }

    public static LocationAddEntity map(LocationAddForm input) {
        return LocationAddEntity.create(input.name(), input.description());
    }

    public static LocationToEdit mapToEdit(LocationDbEntity location) {
        return LocationToEdit.builder()
                .id(location.id())
                .name(location.name())
                .description(location.description())
                .build();
    }

    public static LocationForEditing mapForEditing(LocationDbEntity location) {
        return LocationForEditing.builder()
                .id(location.id())
                .version(location.version())
                .name(location.name())
                .description(location.description())
                .build();
    }

    public static LocationEditEntity map(LocationForEditing locationForEditing) {
        return LocationEditEntity.create(
                locationForEditing.version(),
                locationForEditing.name(),
                locationForEditing.description(),
                locationForEditing.id());
    }
}
