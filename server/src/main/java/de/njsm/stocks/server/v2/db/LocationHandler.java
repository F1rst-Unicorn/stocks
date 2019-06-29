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
import org.jooq.Table;
import org.jooq.TableField;

import java.util.function.Function;

import static de.njsm.stocks.server.v2.db.jooq.Tables.LOCATION;


public class LocationHandler extends CrudDatabaseHandler<LocationRecord, Location> {

    private FoodItemHandler foodItemHandler;

    public LocationHandler(ConnectionFactory connectionFactory,
                           String resourceIdentifier,
                           int timeout,
                           InsertVisitor<LocationRecord> visitor,
                           FoodItemHandler foodItemHandler) {
        super(connectionFactory, resourceIdentifier, timeout, visitor);
        this.foodItemHandler = foodItemHandler;
    }

    @Override
    public StatusCode delete(Location item) {
        return runCommand(context -> {
            StatusCode checkResult = performDeleteChecks(item, context);

            if (checkResult != StatusCode.SUCCESS)
                return checkResult;

            return deleteInternally(item, context);
        });
    }

    public StatusCode rename(Location item)  {
        return runCommand(context -> {
            if (isMissing(item, context))
                return StatusCode.NOT_FOUND;

            int changedItems = context.update(getTable())
                    .set(LOCATION.NAME, item.name)
                    .set(getVersionField(), getVersionField().add(1))
                    .where(getIdField().eq(item.id)
                            .and(getVersionField().eq(item.version)))
                    .and(getVersionField().eq(item.version))
                    .execute();

            if (changedItems == 1)
                return StatusCode.SUCCESS;
            else
                return StatusCode.INVALID_DATA_VERSION;

        });
    }

    private StatusCode performDeleteChecks(Location location, DSLContext context) {

        if (isMissing(location, context))
            return StatusCode.NOT_FOUND;

        Boolean locationContainsItems = foodItemHandler.areItemsStoredIn(location, context);
        if (locationContainsItems)
            return StatusCode.FOREIGN_KEY_CONSTRAINT_VIOLATION;
        else
            return StatusCode.SUCCESS;
    }

    private StatusCode deleteInternally(Location item, DSLContext context) {
        int changedItems = context.deleteFrom(getTable())
                .where(getIdField().eq(item.id)
                        .and(getVersionField().eq(item.version)))
                .execute();

        if (changedItems == 1)
            return StatusCode.SUCCESS;
        else
            return StatusCode.INVALID_DATA_VERSION;
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

}
