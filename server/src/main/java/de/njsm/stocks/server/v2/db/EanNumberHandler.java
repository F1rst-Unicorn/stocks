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
import de.njsm.stocks.server.v2.db.jooq.tables.records.EanNumberRecord;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableField;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static de.njsm.stocks.server.v2.db.jooq.Tables.EAN_NUMBER;


public class EanNumberHandler extends CrudDatabaseHandler<EanNumberRecord, EanNumber> {

    public EanNumberHandler(ConnectionFactory connectionFactory) {
        super(connectionFactory);
    }

    public StatusCode deleteOwnedByFood(Identifiable<Food> food) {
        return currentDelete(EAN_NUMBER.IDENTIFIES.eq(food.id()))
                .map(this::notFoundIsOk);
    }

    @Override
    protected Table<EanNumberRecord> getTable() {
        return EAN_NUMBER;
    }

    @Override
    protected TableField<EanNumberRecord, Integer> getIdField() {
        return EAN_NUMBER.ID;
    }

    @Override
    protected TableField<EanNumberRecord, Integer> getVersionField() {
        return EAN_NUMBER.VERSION;
    }

    @Override
    protected Function<EanNumberRecord, EanNumber> getDtoMap() {
        return cursor -> BitemporalEanNumber.builder()
                .id(cursor.getId())
                .version(cursor.getVersion())
                .validTimeStart(cursor.getValidTimeStart().toInstant())
                .validTimeEnd(cursor.getValidTimeEnd().toInstant())
                .transactionTimeStart(cursor.getTransactionTimeStart().toInstant())
                .transactionTimeEnd(cursor.getTransactionTimeEnd().toInstant())
                .initiates(cursor.getInitiates())
                .identifiesFood(cursor.getIdentifies())
                .eanNumber(cursor.getNumber())
                .build();
    }

    @Override
    protected List<Field<?>> getNontemporalFields() {
        return Arrays.asList(
                EAN_NUMBER.ID,
                EAN_NUMBER.VERSION,
                EAN_NUMBER.NUMBER,
                EAN_NUMBER.IDENTIFIES
        );
    }
}
