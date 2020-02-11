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

package de.njsm.stocks.server.v2.db.jooq;

import org.jooq.*;
import org.postgresql.util.PGInterval;

import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Types;
import java.time.Period;
import java.util.Locale;

/**
 * Only supports `INTERVAL YEAR MONTH DAY` type
 */
public class IntervalBinding implements Binding<Object, Period> {

    @Override
    public Converter<Object, Period> converter() {
        return Converter.of(
                Object.class,
                Period.class,
                dbObject -> {
                    if (dbObject instanceof PGInterval) {
                        PGInterval interval = (PGInterval) dbObject;
                        return Period.ZERO
                                .plusYears(interval.getYears())
                                .plusMonths(interval.getMonths())
                                .plusDays(interval.getDays());
                    } else
                        return null;
                },

                period -> new PGInterval(period.getYears(), period.getMonths(), period.getDays(), 0, 0, 0)
        );
    }

    @Override
    public void sql(BindingSQLContext<Period> ctx) throws SQLException {
        ctx.render().sql("?");
    }

    @Override
    public void register(BindingRegisterContext<Period> ctx) throws SQLException {
        ctx.statement().registerOutParameter(ctx.index(), Types.VARCHAR);
    }

    @Override
    public void set(BindingSetStatementContext<Period> ctx) throws SQLException {
        Locale def = Locale.getDefault();
        Locale.setDefault(Locale.US);
        ctx.statement().setObject(ctx.index(), ctx.convert(converter()).value());
        Locale.setDefault(def);
    }

    @Override
    public void get(BindingGetResultSetContext<Period> ctx) throws SQLException {
        ctx.convert(converter()).value(ctx.resultSet().getObject(ctx.index()));
    }

    @Override
    public void get(BindingGetStatementContext<Period> ctx) throws SQLException {
        ctx.convert(converter()).value(ctx.statement().getObject(ctx.index()));
    }

    @Override
    public void set(BindingSetSQLOutputContext<Period> ctx) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void get(BindingGetSQLInputContext<Period> ctx) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
}
