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

import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.server.util.Principals;
import fj.data.Validation;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import static de.njsm.stocks.server.v2.db.jooq.tables.Ticket.TICKET;
import static de.njsm.stocks.server.v2.db.jooq.tables.User.USER;
import static de.njsm.stocks.server.v2.db.jooq.tables.UserDevice.USER_DEVICE;

public class PrincipalsHandler extends FailSafeDatabaseHandler {

    public PrincipalsHandler(ConnectionFactory connectionFactory) {
        super(connectionFactory);
    }

    public Validation<StatusCode, Set<Principals>> getPrincipals() {
        return runFunction(context -> {
            Field<OffsetDateTime> now = DSL.currentOffsetDateTime();
            Set<Principals> result = context
                    .selectFrom(USER.join(USER_DEVICE)
                            .on(USER.ID.eq(USER_DEVICE.BELONGS_TO)))
                    .where(USER_DEVICE.ID.notIn(context.select(TICKET.BELONGS_DEVICE)
                                                .from(TICKET))
                            .and(USER_DEVICE.TECHNICAL_USE_CASE.isNull())
                            .and(USER.VALID_TIME_START.lessOrEqual(now))
                            .and(now.lessThan(USER.VALID_TIME_END))
                            .and(USER.TRANSACTION_TIME_END.eq(CrudDatabaseHandler.INFINITY))
                            .and(USER_DEVICE.VALID_TIME_START.lessOrEqual(now))
                            .and(now.lessThan(USER_DEVICE.VALID_TIME_END))
                            .and(USER_DEVICE.TRANSACTION_TIME_END.eq(CrudDatabaseHandler.INFINITY))
                    )
                    .fetch()
                    .stream()
                    .map(record -> new Principals(
                            record.get(USER.NAME),
                            record.get(USER_DEVICE.NAME),
                            record.get(USER.ID),
                            record.get(USER_DEVICE.ID)
                    ))
                    .collect(Collectors.toSet());

            return Validation.success(result);
        });
    }

    public Validation<StatusCode, Principals> getJobRunnerPrincipal() {
        return runFunction(context -> {
            Field<OffsetDateTime> now = DSL.currentOffsetDateTime();

            Principals result = context
                    .selectFrom(USER.join(USER_DEVICE)
                            .on(USER.ID.eq(USER_DEVICE.BELONGS_TO)))
                    .where(USER_DEVICE.TECHNICAL_USE_CASE.eq(TechnicalUseCase.JOB_RUNNER.getDbIdentifier())
                            .and(USER.VALID_TIME_START.lessOrEqual(now))
                            .and(now.lessThan(USER.VALID_TIME_END))
                            .and(USER.TRANSACTION_TIME_END.eq(CrudDatabaseHandler.INFINITY))
                            .and(USER_DEVICE.VALID_TIME_START.lessOrEqual(now))
                            .and(now.lessThan(USER_DEVICE.VALID_TIME_END))
                            .and(USER_DEVICE.TRANSACTION_TIME_END.eq(CrudDatabaseHandler.INFINITY))
                    )
                    .fetchAny(record -> new Principals(
                            record.get(USER.NAME),
                            record.get(USER_DEVICE.NAME),
                            record.get(USER.ID),
                            record.get(USER_DEVICE.ID)
                    ));

            return Validation.success(result);
        });
    }
}
