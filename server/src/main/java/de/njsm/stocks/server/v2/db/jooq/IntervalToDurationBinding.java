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
import java.time.Duration;
import java.util.Locale;

/**
 * Only supports `INTERVAL DAY HOUR MINUTE SECOND` type
 */
public class IntervalToDurationBinding implements Binding<Object, Duration> {

    @Override
    public Converter<Object, Duration> converter() {
        return Converter.of(
                Object.class,
                Duration.class,
                dbObject -> {
                    if (dbObject instanceof PGInterval) {
                        PGInterval interval = (PGInterval) dbObject;
                        return Duration.ZERO
                                .plusDays(interval.getDays())
                                .plusHours(interval.getHours())
                                .plusMinutes(interval.getMinutes())
                                .plusSeconds(interval.getWholeSeconds());
                    } else
                        return null;
                },

                duration -> new PGInterval(0, 0, (int) duration.toDaysPart(), duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart())
        );
    }

    @Override
    public void sql(BindingSQLContext<Duration> ctx) throws SQLException {
        if (ctx.render().paramType().value().equals("INLINED")) {
            Locale def = Locale.getDefault();
            Locale.setDefault(Locale.US);
            ctx.render().sql("interval '" + ctx.convert(converter()).value().toString() + "'");
            Locale.setDefault(def);
        } else
            ctx.render().sql("?");
    }

    @Override
    public void register(BindingRegisterContext<Duration> ctx) throws SQLException {
        ctx.statement().registerOutParameter(ctx.index(), Types.VARCHAR);
    }

    @Override
    public void set(BindingSetStatementContext<Duration> ctx) throws SQLException {
        Locale def = Locale.getDefault();
        Locale.setDefault(Locale.US);
        ctx.statement().setObject(ctx.index(), ctx.convert(converter()).value());
        Locale.setDefault(def);
    }

    @Override
    public void get(BindingGetResultSetContext<Duration> ctx) throws SQLException {
        ctx.convert(converter()).value(ctx.resultSet().getObject(ctx.index()));
    }

    @Override
    public void get(BindingGetStatementContext<Duration> ctx) throws SQLException {
        ctx.convert(converter()).value(ctx.statement().getObject(ctx.index()));
    }

    @Override
    public void set(BindingSetSQLOutputContext<Duration> ctx) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void get(BindingGetSQLInputContext<Duration> ctx) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
}
