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

package org.jooq.impl;

import org.jooq.*;
import org.postgresql.PGStatement;

import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.OffsetDateTime;

import static de.njsm.stocks.server.v2.db.CrudDatabaseHandler.INFINITY;
import static de.njsm.stocks.server.v2.db.CrudDatabaseHandler.NEGATIVE_INFINITY;

/**
 * Needed as long as
 * https://github.com/jOOQ/jOOQ/issues/10517
 * is not resolved
 */
public class OffsetDateTimeBinding implements Binding<OffsetDateTime, OffsetDateTime> {

    @Override
    public Converter<OffsetDateTime, OffsetDateTime> converter() {
        return Converters.identity(OffsetDateTime.class);
    }

    @Override
    public void sql(BindingSQLContext<OffsetDateTime> ctx) {
        ctx.render().sql("?");
    }

    @Override
    public void register(BindingRegisterContext<OffsetDateTime> ctx) throws SQLException {
        ctx.statement().registerOutParameter(ctx.index(), Types.TIMESTAMP_WITH_TIMEZONE);
    }

    @Override
    public void set(BindingSetStatementContext<OffsetDateTime> ctx) throws SQLException {
        OffsetDateTime value = converter().to(ctx.value());

        if (value == null)
            ctx.statement().setNull(ctx.index(), Types.TIMESTAMP_WITH_TIMEZONE);
        else if (value.equals(NEGATIVE_INFINITY))
            ctx.statement().setTimestamp(ctx.index(), new Timestamp(PGStatement.DATE_NEGATIVE_INFINITY));
        else if (value.equals(INFINITY))
            ctx.statement().setTimestamp(ctx.index(), new Timestamp(PGStatement.DATE_POSITIVE_INFINITY));
        else {
            ctx.statement().setObject(ctx.index(), value);
        }
    }

    @Override
    public void get(BindingGetResultSetContext<OffsetDateTime> ctx) throws SQLException {
        String raw = ctx.resultSet().getString(ctx.index());
        OffsetDateTime result;

        if (raw.equals("infinity")) {
            result = INFINITY;
        } else if (raw.equals("-infinity")) {
            result = NEGATIVE_INFINITY;
        } else {
            result = DefaultBinding.OffsetDateTimeParser.offsetDateTime(raw);
        }

        ctx.convert(converter()).value(result);
    }

    @Override
    public void get(BindingGetStatementContext<OffsetDateTime> ctx) throws SQLException {
        String raw = ctx.statement().getString(ctx.index());
        OffsetDateTime result;

        if (raw.equals("infinity")) {
            result = INFINITY;
        } else if (raw.equals("-infinity")) {
            result = NEGATIVE_INFINITY;
        } else {
            result = DefaultBinding.OffsetDateTimeParser.offsetDateTime(raw);
        }

        ctx.convert(converter()).value(result);
    }

    @Override
    public void set(BindingSetSQLOutputContext<OffsetDateTime> ctx) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void get(BindingGetSQLInputContext<OffsetDateTime> ctx) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
}
