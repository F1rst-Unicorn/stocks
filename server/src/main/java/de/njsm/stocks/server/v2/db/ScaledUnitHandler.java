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

import de.njsm.stocks.common.api.BitemporalScaledUnit;
import de.njsm.stocks.common.api.ScaledUnit;
import de.njsm.stocks.common.api.ScaledUnitForEditing;
import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.server.v2.db.jooq.tables.records.ScaledUnitRecord;
import fj.data.Validation;
import org.jooq.Field;
import org.jooq.RecordMapper;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static de.njsm.stocks.server.v2.db.jooq.Tables.SCALED_UNIT;


@Repository
@RequestScope
public class ScaledUnitHandler extends CrudDatabaseHandler<ScaledUnitRecord, ScaledUnit> {


    public ScaledUnitHandler(ConnectionFactory connectionFactory) {
        super(connectionFactory);
    }

    public StatusCode edit(ScaledUnitForEditing data) {
        return runCommand(context -> {
            if (isCurrentlyMissing(data, context))
                return StatusCode.NOT_FOUND;

            return currentUpdate(context,
                    Arrays.asList(
                            SCALED_UNIT.ID,
                            SCALED_UNIT.VERSION.add(1),
                            DSL.inline(data.scale()),
                            DSL.inline(data.unit())
                    ),
                    getIdField().eq(data.id())
                            .and(getVersionField().eq(data.version()))
                            .and(SCALED_UNIT.SCALE.ne(data.scale())
                                    .or(SCALED_UNIT.UNIT.ne(data.unit())))

            )
                    .map(this::notFoundMeansInvalidVersion);
        });
    }

    public Validation<StatusCode, Integer> countCurrent() {
        return runFunction(context -> {
            Optional<Integer> count = context.selectCount()
                    .from(SCALED_UNIT)
                    .where(nowAsBestKnown())
                    .fetchOptional(0, Integer.class);

            return count
                    .map(Validation::<StatusCode, Integer>success)
                    .orElse(Validation.fail(StatusCode.NOT_FOUND));
        });
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
    protected RecordMapper<ScaledUnitRecord, ScaledUnit> getDtoMap() {
        return cursor -> BitemporalScaledUnit.builder()
                .id(cursor.getId())
                .version(cursor.getVersion())
                .validTimeStart(cursor.getValidTimeStart().toInstant())
                .validTimeEnd(cursor.getValidTimeEnd().toInstant())
                .transactionTimeStart(cursor.getTransactionTimeStart().toInstant())
                .transactionTimeEnd(cursor.getTransactionTimeEnd().toInstant())
                .initiates(cursor.getInitiates())
                .scale(cursor.getScale())
                .unit(cursor.getUnit())
                .build();
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
