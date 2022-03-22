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

import de.njsm.stocks.common.api.Identifiable;
import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.common.api.UserDevice;
import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.business.data.ClientTicket;
import de.njsm.stocks.server.v2.business.data.ServerTicket;
import de.njsm.stocks.server.v2.db.jooq.tables.records.TicketRecord;
import fj.data.Validation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.Field;
import org.jooq.Record4;
import org.jooq.Result;
import org.jooq.impl.DSL;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Date;

import static de.njsm.stocks.server.v2.db.jooq.tables.Ticket.TICKET;
import static de.njsm.stocks.server.v2.db.jooq.tables.User.USER;
import static de.njsm.stocks.server.v2.db.jooq.tables.UserDevice.USER_DEVICE;

public class TicketHandler extends FailSafeDatabaseHandler {

    private static final Logger LOG = LogManager.getLogger(TicketHandler.class);

    public TicketHandler(ConnectionFactory connectionFactory) {
        super(connectionFactory);
    }

    public StatusCode addTicket(int deviceId, String ticket) {
        return runCommand(context -> {
            context.insertInto(TICKET)
                    .columns(TICKET.BELONGS_DEVICE, TICKET.CREATED_ON, TICKET.TICKET_)
                    .values(deviceId, new Timestamp(Instant.now().toEpochMilli()), ticket)
                    .execute();
            return StatusCode.SUCCESS;
        });
    }

    public Validation<StatusCode, ServerTicket> getTicket(ClientTicket ticket) {
        return runFunction(context -> {
            Result<TicketRecord> dbResult = context.selectFrom(TICKET)
                    .where(TICKET.TICKET_.eq(ticket.ticket()))
                    .limit(1)
                    .fetch();

            if (dbResult.isEmpty()) {
                LOG.warn("Not found");
                return Validation.fail(StatusCode.NOT_FOUND);
            } else {
                TicketRecord record = dbResult.get(0);

                return Validation.success(ServerTicket.builder()
                        .id(record.getId())
                        .creationDate(new Date(record.getCreatedOn().toInstant().toEpochMilli()))
                        .deviceId(record.getBelongsDevice())
                        .ticket(record.getTicket())
                        .build());
            }
        });
    }

    public StatusCode removeTicket(ServerTicket ticket) {
        return runCommand(context -> {
            int changedItems = context.deleteFrom(TICKET)
                    .where(TICKET.ID.eq(ticket.id()))
                    .execute();

            if (changedItems == 1) {
                return StatusCode.SUCCESS;
            } else {
                LOG.warn("Not found");
                return StatusCode.NOT_FOUND;
            }
        });
    }

    public StatusCode removeTicketOfDevice(Identifiable<UserDevice> device) {
        return runCommand(context -> {
            context.deleteFrom(TICKET)
                    .where(TICKET.BELONGS_DEVICE.eq(device.id()))
                    .execute();

            return StatusCode.SUCCESS;
        });
    }

    public Validation<StatusCode, Principals> getPrincipalsForTicket(String token) {
        return runFunction(context -> {
            Field<OffsetDateTime> now = DSL.currentOffsetDateTime();

            Result<Record4<Integer, String, Integer, String>> dbResult = context.select(USER_DEVICE.ID, USER_DEVICE.NAME, USER.ID, USER.NAME)
                    .from(TICKET.join(USER_DEVICE
                            .join(USER).on(USER.ID.eq(USER_DEVICE.BELONGS_TO)
                                    .and(USER.VALID_TIME_START.lessOrEqual(now))
                                    .and(now.lessThan(USER.VALID_TIME_END))
                                    .and(USER.TRANSACTION_TIME_END.eq(CrudDatabaseHandler.INFINITY))
                            ))
                            .on(TICKET.BELONGS_DEVICE.eq(USER_DEVICE.ID)
                                    .and(USER_DEVICE.VALID_TIME_START.lessOrEqual(now))
                                    .and(now.lessThan(USER_DEVICE.VALID_TIME_END))
                                    .and(USER_DEVICE.TRANSACTION_TIME_END.eq(CrudDatabaseHandler.INFINITY))
                            )
                    )
                    .where(TICKET.TICKET_.eq(token))
                    .limit(1)
                    .fetch();

            if (dbResult.isEmpty()) {
                LOG.warn("Not found");
                return Validation.fail(StatusCode.NOT_FOUND);
            } else {
                Record4<Integer, String, Integer, String> record = dbResult.get(0);
                return Validation.success(new Principals(
                        record.component4(),
                        record.component2(),
                        record.component3(),
                        record.component1()));
            }
        });
    }
}
