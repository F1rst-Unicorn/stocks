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

import de.njsm.stocks.client.business.entities.LocationToEdit;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static de.njsm.stocks.client.business.Constants.INFINITY;
import static java.time.Instant.EPOCH;

public class StandardEntities {

    public static LocationDbEntity locationDbEntity() {
        return locationDbEntityBuilder().build();
    }

    static LocationDbEntity.Builder locationDbEntityBuilder() {
        return LocationDbEntity.builder()
                .id(1)
                .version(2)
                .validTimeStart(EPOCH)
                .validTimeEnd(INFINITY)
                .transactionTimeStart(EPOCH)
                .transactionTimeEnd(INFINITY)
                .initiates(3)
                .name("name")
                .description("description");
    }

    public static List<LocationDbEntity> bitemporalEdit(LocationDbEntity current, LocationToEdit edit, Instant when) {
        LocationDbEntity deletedCurrent = current.toBuilder()
                .transactionTimeEnd(when)
                .build();
        LocationDbEntity terminatedCurrent = current.toBuilder()
                .validTimeEnd(when)
                .transactionTimeStart(when)
                .build();
        LocationDbEntity edited = current.toBuilder()
                .validTimeStart(when)
                .transactionTimeStart(when)
                .version(current.version() + 1)
                .name(edit.name())
                .description(edit.description())
                .build();

        return Arrays.asList(
                deletedCurrent,
                terminatedCurrent,
                edited
        );
    }
}
