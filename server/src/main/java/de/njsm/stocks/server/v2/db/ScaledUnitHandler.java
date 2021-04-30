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

import de.njsm.stocks.server.v2.business.data.BitemporalScaledUnit;
import de.njsm.stocks.server.v2.business.data.ScaledUnit;
import de.njsm.stocks.server.v2.business.data.ScaledUnitForGetting;
import de.njsm.stocks.server.v2.db.jooq.tables.records.ScaledUnitRecord;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableField;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static de.njsm.stocks.server.v2.db.jooq.Tables.SCALED_UNIT;


public class ScaledUnitHandler extends CrudDatabaseHandler<ScaledUnitRecord, ScaledUnit> {


    public ScaledUnitHandler(ConnectionFactory connectionFactory,
                             String resourceIdentifier,
                             int timeout) {
        super(connectionFactory, resourceIdentifier, timeout);
    }

    @Override
    protected Table<ScaledUnitRecord> getTable() {
        return SCALED_UNIT;
    }

    @Override
    protected TableField<ScaledUnitRecord, Integer> getIdField() {
        return SCALED_UNIT.ID;
    }

    @Override
    protected TableField<ScaledUnitRecord, Integer> getVersionField() {
        return SCALED_UNIT.VERSION;
    }

    @Override
    protected Function<ScaledUnitRecord, ScaledUnit> getDtoMap(boolean bitemporal) {
        if (bitemporal)
            return cursor -> new BitemporalScaledUnit(
                    cursor.getId(),
                    cursor.getVersion(),
                    cursor.getValidTimeStart().toInstant(),
                    cursor.getValidTimeEnd().toInstant(),
                    cursor.getTransactionTimeStart().toInstant(),
                    cursor.getTransactionTimeEnd().toInstant(),
                    cursor.getInitiates(),
                    cursor.getScale(),
                    cursor.getUnit()
            );
        else
            return cursor -> new ScaledUnitForGetting(
                    cursor.getId(),
                    cursor.getVersion(),
                    cursor.getScale(),
                    cursor.getUnit()
            );
    }

    @Override
    protected List<Field<?>> getNontemporalFields() {
        return Arrays.asList(
                SCALED_UNIT.ID,
                SCALED_UNIT.VERSION,
                SCALED_UNIT.SCALE,
                SCALED_UNIT.UNIT
        );
    }
}
