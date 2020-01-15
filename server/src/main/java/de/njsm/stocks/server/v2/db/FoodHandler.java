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
import de.njsm.stocks.server.v2.business.data.Food;
import de.njsm.stocks.server.v2.db.jooq.tables.records.FoodRecord;
import org.jooq.Table;
import org.jooq.TableField;

import java.time.Period;
import java.util.function.Function;

import static de.njsm.stocks.server.v2.db.jooq.Tables.FOOD;


public class FoodHandler extends CrudDatabaseHandler<FoodRecord, Food> {


    public FoodHandler(ConnectionFactory connectionFactory,
                       String resourceIdentifier,
                       int timeout,
                       InsertVisitor<FoodRecord> visitor) {
        super(connectionFactory, resourceIdentifier, timeout, visitor);
    }

    public StatusCode setToBuyStatus(Food item) {
        return runCommand(context -> {
            if (isMissing(item, context))
                return StatusCode.NOT_FOUND;

            int changedItems = context.update(getTable())
                    .set(FOOD.TO_BUY, item.toBuy)
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

    public StatusCode edit(Food item, String newName, Period expirationOffset) {
        return runCommand(context -> {
            if (isMissing(item, context))
                return StatusCode.NOT_FOUND;

            int changedItems = context.update(getTable())
                    .set(FOOD.NAME, newName)
                    .set(FOOD.EXPIRATION_OFFSET, expirationOffset)
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

    @Override
    protected Table<FoodRecord> getTable() {
        return FOOD;
    }

    @Override
    protected TableField<FoodRecord, Integer> getIdField() {
        return FOOD.ID;
    }

    @Override
    protected TableField<FoodRecord, Integer> getVersionField() {
        return FOOD.VERSION;
    }

    @Override
    protected Function<FoodRecord, Food> getDtoMap() {
        return cursor -> new Food(
                cursor.getId(),
                cursor.getName(),
                cursor.getVersion(),
                cursor.getToBuy(),
                cursor.getExpirationOffset()
        );
    }
}
