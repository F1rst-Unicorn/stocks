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

package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.common.api.*;
import de.njsm.stocks.server.v2.db.jooq.tables.records.LocationRecord;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableField;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static de.njsm.stocks.server.v2.db.jooq.Tables.LOCATION;
import static org.jooq.impl.DSL.inline;


public class LocationHandler extends CrudDatabaseHandler<LocationRecord, Location> {

    private final FoodItemHandler foodItemHandler;

    public LocationHandler(ConnectionFactory connectionFactory,
                           FoodItemHandler foodItemHandler) {
        super(connectionFactory);
        this.foodItemHandler = foodItemHandler;
    }

    public StatusCode rename(LocationForRenaming item) {
        return runCommand(context -> {

            if (isCurrentlyMissing(item, context))
                return StatusCode.NOT_FOUND;

            return currentUpdate(context, Arrays.asList(
                    LOCATION.ID,
                    inline(item.name()),
                    LOCATION.VERSION.add(1),
                    LOCATION.DESCRIPTION
                    ),
                    LOCATION.ID.eq(item.id())
                            .and(LOCATION.VERSION.eq(item.version()))
                            .and(LOCATION.NAME.ne(item.name())))
                    .map(this::notFoundMeansInvalidVersion);
        });
    }

    @Override
    public StatusCode delete(Versionable<Location> item) {
        return runCommand(context -> performDeleteChecks(item, context)
                .bind(() -> super.delete(item)));
    }

    public StatusCode setDescription(LocationForSetDescription input) {
        return runCommand(context -> {

            if (isCurrentlyMissing(input, context))
                return StatusCode.NOT_FOUND;

            return currentUpdate(context, Arrays.asList(
                    LOCATION.ID,
                    LOCATION.NAME,
                    LOCATION.VERSION.add(1),
                    inline(input.description())
                    ),
                    LOCATION.ID.eq(input.id())
                            .and(LOCATION.VERSION.eq(input.version()))
                            .and(LOCATION.DESCRIPTION.ne(input.description())))
                    .map(this::notFoundMeansInvalidVersion);
        });
    }

    public StatusCode edit(LocationForEditing data) {
        return runCommand(context -> {

            if (isCurrentlyMissing(data, context))
                return StatusCode.NOT_FOUND;

            return currentUpdate(context, Arrays.asList(
                    LOCATION.ID,
                    inline(data.name()),
                    LOCATION.VERSION.add(1),
                    inline(data.description())
                    ),
                    LOCATION.ID.eq(data.id())
                            .and(LOCATION.VERSION.eq(data.version()))
                            .and(LOCATION.NAME.ne(data.name())
                                    .or(LOCATION.DESCRIPTION.ne(data.description()))))
                    .map(this::notFoundMeansInvalidVersion);
        });
    }

    private StatusCode performDeleteChecks(Versionable<Location> location, DSLContext context) {
        boolean locationContainsItems = foodItemHandler.areItemsStoredIn(location, context);
        if (locationContainsItems)
            return StatusCode.FOREIGN_KEY_CONSTRAINT_VIOLATION;
        else
            return StatusCode.SUCCESS;
    }

    @Override
    protected Table<LocationRecord> getTable() {
        return LOCATION;
    }

    @Override
    protected TableField<LocationRecord, Integer> getIdField() {
        return LOCATION.ID;
    }

    @Override
    protected TableField<LocationRecord, Integer> getVersionField() {
        return LOCATION.VERSION;
    }

    @Override
    protected Function<LocationRecord, Location> getDtoMap(boolean bitemporal) {
        if (bitemporal)
            return cursor -> BitemporalLocation.builder()
                    .id(cursor.getId())
                    .version(cursor.getVersion())
                    .validTimeStart(cursor.getValidTimeStart().toInstant())
                    .validTimeEnd(cursor.getValidTimeEnd().toInstant())
                    .transactionTimeStart(cursor.getTransactionTimeStart().toInstant())
                    .transactionTimeEnd(cursor.getTransactionTimeEnd().toInstant())
                    .initiates(cursor.getInitiates())
                    .name(cursor.getName())
                    .description(cursor.getDescription())
                    .build();
        else
            return cursor -> LocationForGetting.builder()
                    .id(cursor.getId())
                    .version(cursor.getVersion())
                    .name(cursor.getName())
                    .description(cursor.getDescription())
                    .build();
    }

    @Override
    protected List<Field<?>> getNontemporalFields() {
        return Arrays.asList(
                LOCATION.ID,
                LOCATION.NAME,
                LOCATION.VERSION,
                LOCATION.DESCRIPTION
        );
    }
}
