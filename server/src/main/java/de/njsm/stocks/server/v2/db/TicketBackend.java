package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.ClientTicket;
import de.njsm.stocks.server.v2.business.data.ServerTicket;
import de.njsm.stocks.server.v2.business.data.UserDevice;
import de.njsm.stocks.server.v2.db.jooq.tables.records.TicketRecord;
import fj.data.Validation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.Record4;
import org.jooq.Result;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;

import static de.njsm.stocks.server.v2.db.jooq.tables.Ticket.TICKET;
import static de.njsm.stocks.server.v2.db.jooq.tables.User.USER;
import static de.njsm.stocks.server.v2.db.jooq.tables.UserDevice.USER_DEVICE;

public class TicketBackend extends FailSafeDatabaseHandler {

    private static final Logger LOG = LogManager.getLogger(TicketBackend.class);

    public TicketBackend(ConnectionFactory connectionFactory,
                         String resourceIdentifier) {
        super(connectionFactory, resourceIdentifier);
    }

    public StatusCode addTicket(UserDevice device, String ticket) {
        return runCommand(context -> {
            context.insertInto(TICKET)
                    .columns(TICKET.BELONGS_DEVICE, TICKET.CREATED_ON, TICKET.TICKET_)
                    .values(device.id, new Timestamp(Instant.now().toEpochMilli()), ticket)
                    .execute();
            return StatusCode.SUCCESS;
        });
    }

    public Validation<StatusCode, ServerTicket> getTicket(ClientTicket ticket) {
        return runFunction(context -> {
            Result<TicketRecord> dbResult = context.selectFrom(TICKET)
                    .where(TICKET.TICKET_.eq(ticket.ticket))
                    .limit(1)
                    .fetch();

            if (dbResult.isEmpty()) {
                LOG.warn("Not found");
                return Validation.fail(StatusCode.NOT_FOUND);
            } else {
                TicketRecord record = dbResult.get(0);

                return Validation.success(new ServerTicket(
                        record.getId(),
                        new Date(record.getCreatedOn().toInstant().toEpochMilli()),
                        record.getBelongsDevice(),
                        record.getTicket()));
            }
        });
    }

    public StatusCode removeTicket(ServerTicket ticket) {
        return runCommand(context -> {
            int changedItems = context.deleteFrom(TICKET)
                    .where(TICKET.ID.eq(ticket.id))
                    .execute();

            if (changedItems == 1) {
                return StatusCode.SUCCESS;
            } else {
                LOG.warn("Not found");
                return StatusCode.NOT_FOUND;
            }
        });
    }

    public Validation<StatusCode, Principals> getPrincipalsForTicket(String token) {
        return runFunction(context -> {
            Result<Record4<Integer, String, Integer, String>> dbResult = context.select(USER_DEVICE.ID, USER_DEVICE.NAME, USER.ID, USER.NAME)
                    .from(TICKET.join(USER_DEVICE
                                        .join(USER).on(USER.ID.eq(USER_DEVICE.BELONGS_TO)))
                                .on(TICKET.BELONGS_DEVICE.eq(USER_DEVICE.ID)))
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
