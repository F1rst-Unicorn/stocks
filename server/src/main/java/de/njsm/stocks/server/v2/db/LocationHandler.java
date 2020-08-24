/* stocks is client-server program to manage a household's food stock
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
 */

package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.Location;
import de.njsm.stocks.server.v2.db.jooq.tables.records.LocationRecord;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static de.njsm.stocks.server.v2.db.jooq.Tables.LOCATION;


public class LocationHandler extends CrudDatabaseHandler<LocationRecord, Location> {

    private final FoodItemHandler foodItemHandler;

    public LocationHandler(ConnectionFactory connectionFactory,
                           String resourceIdentifier,
                           int timeout,
                           InsertVisitor<LocationRecord> visitor,
                           FoodItemHandler foodItemHandler) {
        super(connectionFactory, resourceIdentifier, timeout, visitor);
        this.foodItemHandler = foodItemHandler;
    }

    public StatusCode rename(Location item, String newName) {
        return runCommand(context -> {

            if (isCurrentlyMissing(item, context))
                return StatusCode.NOT_FOUND;

            StatusCode result = currentUpdate(Arrays.asList(
                    LOCATION.ID,
                    DSL.inline(newName),
                    LOCATION.VERSION.add(1)
                    ),
                    LOCATION.ID.eq(item.id)
                            .and(LOCATION.VERSION.eq(item.version)));

            return notFoundMeansInvalidVersion(result);
        });
    }

    @Override
    public StatusCode delete(Location item) {
        return runCommand(context -> {
            StatusCode checkResult = performDeleteChecks(item, context);

            if (checkResult != StatusCode.SUCCESS)
                return checkResult;

            return super.delete(item);
        });
    }

    private StatusCode performDeleteChecks(Location location, DSLContext context) {
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
    protected Function<LocationRecord, Location> getDtoMap() {
        return cursor -> new Location(
                cursor.getId(),
                cursor.getName(),
                cursor.getVersion()
        );
    }

    @Override
    protected List<Field<?>> getNontemporalFields() {
        return Arrays.asList(
                LOCATION.ID,
                LOCATION.NAME,
                LOCATION.VERSION
                );
    }
}
