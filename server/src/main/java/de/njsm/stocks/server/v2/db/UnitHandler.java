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
import de.njsm.stocks.server.v2.business.data.BitemporalUnit;
import de.njsm.stocks.server.v2.business.data.Unit;
import de.njsm.stocks.server.v2.business.data.UnitForGetting;
import de.njsm.stocks.server.v2.business.data.UnitForRenaming;
import de.njsm.stocks.server.v2.db.jooq.tables.records.UnitRecord;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static de.njsm.stocks.server.v2.db.jooq.Tables.UNIT;


public class UnitHandler extends CrudDatabaseHandler<UnitRecord, Unit> {


    public UnitHandler(ConnectionFactory connectionFactory,
                       String resourceIdentifier,
                       int timeout) {
        super(connectionFactory, resourceIdentifier, timeout);
    }

    public StatusCode rename(UnitForRenaming unit) {
        return runCommand(context -> {
            if (isCurrentlyMissing(unit, context)) {
                return StatusCode.NOT_FOUND;
            }

            return currentUpdate(context, Arrays.asList(
                    UNIT.ID,
                    UNIT.VERSION.add(1),
                    DSL.inline(unit.getName()),
                    DSL.inline(unit.getAbbreviation())),

                    UNIT.ID.eq(unit.getId())
                            .and(UNIT.VERSION.eq(unit.getVersion()))
                            .and(UNIT.NAME.ne(unit.getName()))
                            .and(UNIT.ABBREVIATION.ne(unit.getAbbreviation()))
            ).map(this::notFoundMeansInvalidVersion);

        });
    }

    @Override
    protected Table<UnitRecord> getTable() {
        return UNIT;
    }

    @Override
    protected TableField<UnitRecord, Integer> getIdField() {
        return UNIT.ID;
    }

    @Override
    protected TableField<UnitRecord, Integer> getVersionField() {
        return UNIT.VERSION;
    }

    @Override
    protected Function<UnitRecord, Unit> getDtoMap(boolean bitemporal) {
        if (bitemporal)
            return cursor -> new BitemporalUnit(
                    cursor.getId(),
                    cursor.getVersion(),
                    cursor.getValidTimeStart().toInstant(),
                    cursor.getValidTimeEnd().toInstant(),
                    cursor.getTransactionTimeStart().toInstant(),
                    cursor.getTransactionTimeEnd().toInstant(),
                    cursor.getInitiates(),
                    cursor.getName(),
                    cursor.getAbbreviation()
            );
        else
            return cursor -> new UnitForGetting(
                    cursor.getId(),
                    cursor.getVersion(),
                    cursor.getName(),
                    cursor.getAbbreviation()
            );
    }

    @Override
    protected List<Field<?>> getNontemporalFields() {
        return Arrays.asList(
                UNIT.ID,
                UNIT.VERSION,
                UNIT.NAME,
                UNIT.ABBREVIATION
        );
    }
}
